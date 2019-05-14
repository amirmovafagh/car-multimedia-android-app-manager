package ir.dev_roid.testusb;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.felhr.usbserial.CDCSerialDevice;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ir.dev_roid.testusb.app.AudioValues;
import ir.dev_roid.testusb.app.Brightness;
import ir.dev_roid.testusb.app.CpuManager;
import ir.dev_roid.testusb.app.Logger;
import ir.dev_roid.testusb.app.MyAudioManager;
import ir.dev_roid.testusb.app.ObservableInteger;
import ir.dev_roid.testusb.app.PrefManager;
import ir.dev_roid.testusb.steeringWheelController.Pojo.ControllerOption;
import ir.dev_roid.testusb.steeringWheelController.Pojo.Options;
import ir.dev_roid.testusb.steeringWheelController.ProvidedModelOps;
import ir.dev_roid.testusb.steeringWheelController.SteeringWheelControllerModel;

import static ir.dev_roid.testusb.AUXActivity.auxSoundChannelActivity;
import static ir.dev_roid.testusb.MyHandler.buffer;
import static ir.dev_roid.testusb.MyHandler.steeringWheelData;
import static ir.dev_roid.testusb.MyHandler.telephoneActivityIsOpen;
import static ir.dev_roid.testusb.RadioActivity.RadioSoundChannelActivity;

import static ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment.PhoneDialerFragment.dialFragmentIsRun;
import static ir.dev_roid.testusb.steeringWheelController.SteeringWheelControllerPresenter.SteeringWheelControllerActivityIsRun;

/**
 * Created by hirad on 3/1/18.
 */

public class UsbService extends Service {
    private static final String tag = UsbService.class.getSimpleName();
    public static final String ACTION_USB_READY = "com.felhr.connectivityservices.USB_READY";
    public static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public static final String ACTION_USB_NOT_SUPPORTED = "com.felhr.usbservice.USB_NOT_SUPPORTED";
    public static final String ACTION_NO_USB = "com.felhr.usbservice.NO_USB";
    public static final String ACTION_USB_PERMISSION_GRANTED = "com.felhr.usbservice.USB_PERMISSION_GRANTED";
    public static final String ACTION_USB_PERMISSION_NOT_GRANTED = "com.felhr.usbservice.USB_PERMISSION_NOT_GRANTED";
    public static final String ACTION_USB_DISCONNECTED = "com.felhr.usbservice.USB_DISCONNECTED";
    public static final String ACTION_CDC_DRIVER_NOT_WORKING = "com.felhr.connectivityservices.ACTION_CDC_DRIVER_NOT_WORKING";
    public static final String ACTION_USB_DEVICE_NOT_WORKING = "com.felhr.connectivityservices.ACTION_USB_DEVICE_NOT_WORKING";
    public static final int MESSAGE_FROM_SERIAL_PORT = 0;
    public static final int SYNC_READ = 3;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int BAUD_RATE = 19200; // BaudRate. Change this value if you need
    public static boolean SERVICE_CONNECTED = false;
    private static int ONGOING_NOTIFICATION_ID = 1;
    AudioStreamVolumeObserver mAudioStreamVolumeObserver;
    private ProvidedModelOps modelOps;
    private List<ControllerOption> options;

    private PrefManager prefManager;
    private MyAudioManager audioManager;
    private IBinder binder = new UsbBinder();
    private ObservableInteger obsInit;
    private Brightness brightness;
    private Context context;
    private Handler mHandler, checkCpuTempHandler, audioStreamHandler;
    private Runnable checkCpuTempRunnable;
    private MyHandler myHandler;
    private UsbManager usbManager;
    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbSerialDevice serialPort;
    private CpuManager cpu;
    public static boolean threadStatus = true;
    private boolean swcDelay, isPlayMusic, isStopMusic = false;
    private AudioValues audioValues;
    private int handlerDelay = 1500;
    private Logger logger;
    private static int maxTemp = 75;
    private static int minTemp = 59;
    private static int emergencyTemp = 79;

    private boolean serialPortConnected;

    /*
     *  Data received from serial port will be received here. Just populate onReceivedData with your code
     *  In this particular example. byte stream is converted to String and send to UI thread to
     *  be treated there.
     */
    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
            try {
                String data = new String(arg0, "UTF-8");
                if (mHandler != null)
                    mHandler.obtainMessage(MESSAGE_FROM_SERIAL_PORT, data).sendToTarget();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };


    /*
     * Different notifications from OS will be received here (USB attached, detached, permission responses...)
     * About BroadcastReceiver: http://developer.android.com/reference/android/content/BroadcastReceiver.html
     */
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = arg1.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) // User accepted our USB connection. Try to open the device as a serial port
                {
                    Intent intent = new Intent(ACTION_USB_PERMISSION_GRANTED);
                    arg0.sendBroadcast(intent);
                    connection = usbManager.openDevice(device);
                    new ConnectionThread().start();

                } else // User not accepted our USB connection. Send an Intent to the Main Activity
                {
                    Intent intent = new Intent(ACTION_USB_PERMISSION_NOT_GRANTED);
                    arg0.sendBroadcast(intent);
                }
            } else if (arg1.getAction().equals(ACTION_USB_ATTACHED)) {
                if (!serialPortConnected)
                    findSerialPortDevice(); // A USB device has been attached. Try to open it as a Serial port
            } else if (arg1.getAction().equals(ACTION_USB_DETACHED)) {
                if (!checkUsbID()) {
                    //Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    // Usb device was disconnected. send an intent to the Main Activity
                    Intent intent = new Intent(ACTION_USB_DISCONNECTED);
                    arg0.sendBroadcast(intent);
                    if (serialPortConnected) {
                        serialPort.syncClose();
                    }
                    serialPortConnected = false;
                }
            }

        }
    };

    /*
     * onCreate will be executed when service is started. It configures an IntentFilter to listen for
     * incoming Intents (USB ATTACHED, USB DETACHED...) and it tries to open a serial port.
     */
    @Override
    public void onCreate() {
        this.context = this;
        serialPortConnected = false;
        cpu = new CpuManager();
        modelOps = SteeringWheelControllerModel.getInstance(this);
        modelOps.createAllDaosIfNotExsit();
        steeringWheelData = 999;
        audioStreamHandler = new Handler(Looper.getMainLooper());

        checkCpuTempHandler = new Handler(Looper.getMainLooper());
        checkCpuTempRunnable = new Runnable() {
            @Override
            public void run() {
                checkCpuTemp();
                checkCpuTempHandler.postDelayed(checkCpuTempRunnable, 5000);
            }
        };
        checkCpuTempHandler.postDelayed(checkCpuTempRunnable, 2000);
        //logger = new Logger();

        audioManager = new MyAudioManager(context);
        prefManager = new PrefManager(context);
        brightness = new Brightness(context);
        obsInit = new ObservableInteger();
        UsbService.SERVICE_CONNECTED = true;
        changeBaudRate(BAUD_RATE);
        setFilter();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        findSerialPortDevice();
        myHandler = new MyHandler(context);
        setHandler(myHandler);
        foregroundNotification();
        CheckCallStatus();
        //startService(new Intent(getBaseContext(), SteeringWheelControllerService.class));

        audioValues = new AudioValues(prefManager);

        obsInit.setOnIntegerChangeListener(new ObservableInteger.OnIntegerChangeListener() {
            @Override
            public void onIntegerChanged(final int newValue) {
                if (newValue != prefManager.getBrightnessValue()) {

                    /*new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //Do something after 100ms

                        }
                    }, 250);*/
                    sendData("oth-brg-" + (newValue / 5) + "?", 250);

                    prefManager.setBrightnessValue(newValue);
                }
            }
        });


        /*mAudioStreamVolumeObserver = new AudioStreamVolumeObserver(this, new Handler(), audioValues);
        this.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, mAudioStreamVolumeObserver);*/


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                options = modelOps.getAllControllerOptions();
                for (ControllerOption co : options) {
                    if (co.getValue() != null) {

                        int i = co.getValue();
                        if (i - 3 <= steeringWheelData && steeringWheelData <= i + 3) {
                            Options id = co.getId();
                            mAudioStreamVolumeObserver.swcDelayOn();
                            swcDelay = true;
                            switch (id) {
                                case OPT0:
                                    Log.i(tag, "#power" + steeringWheelData);

                                    break;
                                case OPT1:
                                    Log.i(tag, "#SRC" + steeringWheelData);
                                    Intent dialogIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(dialogIntent);
                                    break;
                                case OPT2:
                                    Log.i(tag, "#gps" + steeringWheelData);
                                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.sygic.aura");
                                    if (launchIntent != null) {
                                        startActivity(launchIntent);//null pointer check in case package name was not found
                                    }
                                    break;
                                case OPT3:
                                    Log.i(tag, "#upVolume" + steeringWheelData);
                                    mAudioStreamVolumeObserver.increaseVolumeSWC();
                                    /*

                                    int vol = prefManager.getVolumeValue(0);
                                    if (vol < 51 && vol >= 0) {
                                        audioValues.setVolume(vol + 5);
                                        write(audioValues.getVolumeValue().getBytes());
                                        sendData(audioValues.getVolumeValue(), 40);
                                    } else if (vol >= 51) {
                                        audioValues.setVolume(55);//max size
                                        write(audioValues.getVolumeValue().getBytes());
                                        sendData(audioValues.getVolumeValue(), 40);
                                    }
                                    */

                                    break;
                                case OPT4:
                                    Log.i(tag, "#downVolume" + steeringWheelData);
                                    mAudioStreamVolumeObserver.decreaseVolumeSWC();
                                    /*disableCheckCallStatus();

                                    int vold = prefManager.getVolumeValue(0);
                                    if (vold <= 55 && vold > 5) {
                                        audioValues.setVolume(vold - 5);
                                        write(audioValues.getVolumeValue().getBytes());
                                        sendData(audioValues.getVolumeValue(), 40);
                                    } else if (vold <= 5) {
                                        audioValues.setVolume(0);//max size
                                        write(audioValues.getVolumeValue().getBytes());
                                        sendData(audioValues.getVolumeValue(), 40);
                                    }
                                    */

                                    break;
                                case OPT5:

                                    Log.i(tag, "#mute" + steeringWheelData);
                                    mAudioStreamVolumeObserver.muteSWC();
                                    /*int lastVal = prefManager.getVolumeValue(0);
                                    if (lastVal != 0) {

                                        prefManager.setVolumeValue(12, lastVal);
                                        audioValues.setVolume(0);
                                        write(audioValues.getVolumeValue().getBytes());
                                        sendData(audioValues.getVolumeValue(), 40);
                                    } else {
                                        Log.i(tag, "#unmute" + steeringWheelData);
                                        audioValues.setVolume(prefManager.getVolumeValue(12));
                                        write(audioValues.getVolumeValue().getBytes());
                                        sendData(audioValues.getVolumeValue(), 40);
                                    }*/

                                    break;
                                case OPT6:
                                    disableCheckCallStatus();
                                    Log.i(tag, "#play" + steeringWheelData);
                                    if (prefManager.getBluetoothPlayerState()) {
                                        sendData("blt-mus-ppp?", 450);
                                        Log.i("AMIR", "ppp");
                                    } else
                                        audioManager.playHeadUnitMusicPlayer();

                                    break;
                                case OPT7:
                                    disableCheckCallStatus();
                                    Log.i(tag, "#back" + steeringWheelData);
                                    if (prefManager.getBluetoothPlayerState()) {
                                        sendData("blt-mus-bwd?", 450);
                                        Log.i("AMIR", "bwd");
                                    } else
                                        audioManager.previousHeadUnitMusicPlayer();

                                    break;
                                case OPT8:

                                    Log.i(tag, "#next" + steeringWheelData);
                                    if (prefManager.getBluetoothPlayerState()) {
                                        sendData("blt-mus-fwd?", 450);
                                        Log.i("AMIR", "fwd");
                                    } else
                                        audioManager.nextHeadUnitMusicPlayer();

                                    break;
                                case OPT10:
                                    Log.i(tag, "#<--" + steeringWheelData);
                                    break;
                                case OPT11:
                                    Log.i(tag, "#-->" + steeringWheelData);
                                    break;
                                case OPT12:
                                    Log.i(tag, "#answer" + steeringWheelData);
                                    if (telephoneActivityIsOpen) {
                                        sendData("blt-cll-ans?", 50);
                                    } else {
                                        Intent intent = new Intent(context, TelephoneActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }

                                    break;
                                case OPT13:
                                    Log.i(tag, "#end" + steeringWheelData);
                                    sendData("blt-cll-end?", 50);
                                    break;

                            }
                            steeringWheelData = 999;
                            swcDelay = false;
                        }
                    }
                    //Log.i(tag, "Dynamic Array Index #" + num.getId() );
                }
            }
        };
        performOnBackgroundThread(runnable);


    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {

        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        sleep(10);
                        //Log.i(tag, "#RUN" );
                        if (!SteeringWheelControllerActivityIsRun) {
                            runnable.run();
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        };

        t.start();
        return t;
    }

    public void enableCheckCallStatus() {
        threadStatus = true;
        //thread.start();
    }

    public void CheckCallStatus() {

        final Handler nh = new Handler();
        nh.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!swcDelay) {
                    if (threadStatus) {
                        //Log.d(tag, "service...");
                        String data = "blt-cll-chk?";
                        write(data.getBytes());
                    }
                    if (!dialFragmentIsRun) {
                        checkAudioManager();
                    }
                    obsInit.set(brightness.getScreenBrightness());
                    checkResetMCUstate();

                }

                nh.postDelayed(this, handlerDelay);
            }
        }, handlerDelay);
    }

    private void checkCpuTemp() {
        if (!swcDelay) {
            float temp = cpu.getTemperature();
            //logger.info(String.valueOf(temp));
            //Log.i(tag, " "+temp);
            if (temp > 85) {
                Toast.makeText(context, "HIGH Temperature " + temp + "C !!!", Toast.LENGTH_SHORT).show();
            }

            if (temp > maxTemp) {

                sendData("oth-tmp-001?", 300);
                //fanState = true;
                Log.i(tag, " fan on");

                if (temp > emergencyTemp) {
                    sendData("oth-tmp-001?", 1000);
                }
            } else if (temp < minTemp) {

                write("oth-tmp-000?".getBytes());
                //fanState = false;
                Log.i(tag, " fan off");
            }
        }
    }

    private void gotoMainScreen() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }

    private void checkResetMCUstate() {
        if (buffer.equalsIgnoreCase("RUN")) {
            gotoMainScreen();

            maxTemp = 73;
            minTemp = 58;

            cpu.cpuGoverner("interactive");
            Settings.System.putInt(getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, 1800000);
            PowerManager.WakeLock wakeLock;
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wake");
            wakeLock.acquire();
            threadStatus = true;
            //sendData(audioValues.getAudioValues(), 100);
            //sendData(audioValues.getAudioValues(), 400);
            buffer = "";

        }
        if (buffer.equalsIgnoreCase("OFF")) {
            gotoMainScreen();
            maxTemp = 76;
            minTemp = 63;
            threadStatus = false;

            cpu.cpuGoverner("powersave");
            Settings.System.putInt(getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, 15000);
            buffer = "";
            if (audioManager.isMusicPlay())
                audioManager.pauseHeadUnitMusicPlayer();
        }
    }

    private void checkAudioManager() {
        /*
         * when music is on headunit is playing,  this function change the sound channel
         * to the headunit channel
         *
         */
        if (audioManager.isMusicPlay()) {

            if (!isPlayMusic) {
                isPlayMusic = true;
                isStopMusic = false;
                Log.i(tag, "0.1");
                if (prefManager.getAmplifireState()) {
                    sendData("oth-amp-001?", 100);

                    if (prefManager.getDebugModeState())
                        Toast.makeText(context, "<<--amplifire is on-->>0.1", Toast.LENGTH_SHORT).show();
                }
            }

            //Log.i(tag, "1");
            /*if (prefManager.getBluetoothPlayerState()) {
                write("blt-mus-stp?".getBytes());
                sendData("blt-mus-stp?", 300);
                //  Log.i(tag, "1.1");
                if (prefManager.getDebugModeState())
                    Toast.makeText(context, "<<--send stop bluetooth command-->>1.1", Toast.LENGTH_SHORT).show();
                //delayTimer("mod-pin?");


                prefManager.setBluetoothPlayerState(false);
            }

            if (!prefManager.getHeadUnitAudioIsActive() && prefManager.getBluetoothPlayerState()) {
                //delayTimer("mod-pin?");
                sendData("blt-mus-stp?", 300);
                if (prefManager.getDebugModeState())
                    Toast.makeText(context, "<<--send stop bluetooth command-->>1.2", Toast.LENGTH_SHORT).show();

                // Log.i(tag, "1.2");
                prefManager.setHeadUnitAudioIsActive(true);
                prefManager.setBluetoothPlayerState(false);
            }*/

            if (!prefManager.getHeadUnitAudioIsActive() && prefManager.getRadioIsRun() && !RadioSoundChannelActivity) { //if radio is run switch channnel to the headUnit
                sendData(audioValues.androidBTMode(), 250);
                Log.i(tag, "1.3");
                if (prefManager.getDebugModeState())
                    Toast.makeText(context, "<<--set sound channel from RADIO to HU-->>1.3", Toast.LENGTH_SHORT).show();
                prefManager.setHeadUnitAudioIsActive(true);
                prefManager.setRadioIsRun(false);
            }

            if (!prefManager.getHeadUnitAudioIsActive() && prefManager.getAUXAudioIsActive() && !auxSoundChannelActivity) { //if AUX is ACTIVATE switch channnel to the headUnit
                sendData(audioValues.androidBTMode(), 150);
                Log.i(tag, "1.4");
                if (prefManager.getDebugModeState())
                    Toast.makeText(context, "<<--set sound channel from AUX to HU-->>1.4", Toast.LENGTH_SHORT).show();

                prefManager.setAUXAudioIsActive(false);
            }


            prefManager.setHeadUnitAudioIsActive(true);

        } else {
            prefManager.setHeadUnitAudioIsActive(false);
            if (!isStopMusic) {
                isPlayMusic = false;
                isStopMusic = true;
                Log.i(tag, "0.2");
                if (prefManager.getAmplifireState() && !prefManager.getBluetoothPlayerState() && !prefManager.getRadioIsRun()) {
                    sendData("oth-amp-000?", 200);
                    if (prefManager.getDebugModeState())
                        Toast.makeText(context, "<<--amplifire is off-->>0.2", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    /*private void checkBluetoothMusic() {

        if (buffer.equalsIgnoreCase("MB") || buffer.equalsIgnoreCase("MR")) {

            Log.i(tag, "2");

            prefManager.setBluetoothPlayerState(true);
        } else if (buffer.equalsIgnoreCase("MP")) {
            Log.i(tag, "2.1");
            if (!prefManager.getHeadUnitAudioIsActive()) {
                delayTimer("mod-pin?");
                Log.i(tag, "2.2");
            }
            prefManager.setBluetoothPlayerState(false);
        } else if (buffer.equalsIgnoreCase("MA")) {
            Log.i(tag, "2.3");
            if (!prefManager.getHeadUnitAudioIsActive()) {
                delayTimer("mod-pin?");
                Log.i(tag, "2.4");
            }
            prefManager.setBluetoothPlayerState(false);
        }
    }*/

    private void sendData(final String data, int delay) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                write(data.getBytes());
            }
        }, delay);
    }

    public void disableCheckCallStatus() {
        threadStatus = false;
    }

    public void disableThreadService() {
        swcDelay = true;
    }

    public void enableThreadService() {
        swcDelay = false;
    }
    public void changeHandlerDelay(int delay) {
        handlerDelay = delay;
    }

    /*
     * MUST READ about services
     * http://developer.android.com/guide/components/services.html
     * http://developer.android.com/guide/components/bound-services.html
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, "onDestroy service");
        UsbService.SERVICE_CONNECTED = false;
        getApplicationContext().getContentResolver().unregisterContentObserver(mAudioStreamVolumeObserver);


    }

    /*
     * This function will be called from MainActivity to write data through Serial Port
     */
    public void write(byte[] data) {
        if (serialPort != null)
            serialPort.syncWrite(data, 0);
    }

    /*
     * This function baud rate
     */

    public void changeBaudRate(int baudRate) {
        if (serialPort != null)
            serialPort.setBaudRate(baudRate);
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    private void findSerialPortDevice() {
        // This snippet will try to open the first encountered usb device connected, excluding usb root hubs
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();
                //Toast.makeText(context, "veID: "+deviceVID+" proID: "+devicePID, Toast.LENGTH_SHORT).show();

                if (deviceVID != 0x1d6b && (devicePID != 0x0001 && devicePID != 0x0002 && devicePID != 0x0003)) {
                    // There is a device connected to our Android device. Try to open it as a Serial Port.
                    if (devicePID == 29987 && deviceVID == 6790) {
                        requestUserPermission();
                        keep = false;
                    } else {
                        if (prefManager.getDebugModeState())
                            Toast.makeText(context, "serial device dont recognize", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
            if (!keep) {
                // There is no USB devices connected (but usb host were listed). Send an intent to MainActivity.
                Intent intent = new Intent(ACTION_NO_USB);
                sendBroadcast(intent);
            }
        } else {
            // There is no USB devices connected. Send an intent to MainActivity
            Intent intent = new Intent(ACTION_NO_USB);
            sendBroadcast(intent);
        }
    }

    private boolean checkUsbID() {
        boolean check = false;

        int vendorId = 6790;
        int productId = 29987;
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        if (usbManager.getDeviceList().isEmpty()) {
            Log.d(tag, "No connected devices");
            check = false;
        }

        //get device information
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        if (deviceList.size() == 0) {
            Log.d(tag, "no usb device found.");
            check = false;
        }
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        UsbDevice usbDevice;
        //check all of connected devices to find target device
        while (deviceIterator.hasNext()) {
            usbDevice = deviceIterator.next();

            if (usbDevice.getVendorId() == vendorId && usbDevice.getProductId() == productId) {
                check = true;

            }

        }

        return check;
    }

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_DETACHED);
        filter.addAction(ACTION_USB_ATTACHED);
        registerReceiver(usbReceiver, filter);
    }

    /*
     * Request user permission. The response will be received in the BroadcastReceiver
     */
    private void requestUserPermission() {
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        usbManager.requestPermission(device, mPendingIntent);
    }

    public class UsbBinder extends Binder {
        public UsbService getService() {
            return UsbService.this;
        }
    }

    /*
     * A simple thread to open a serial port.
     * Although it should be a fast operation. moving usb operations away from UI thread is a good thing.
     */
    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
            if (serialPort != null) {
                if (serialPort.syncOpen()) {
                    serialPortConnected = true;
                    serialPort.setBaudRate(BAUD_RATE);
                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                    /**
                     * Current flow control Options:
                     * UsbSerialInterface.FLOW_CONTROL_OFF
                     * UsbSerialInterface.FLOW_CONTROL_RTS_CTS only for CP2102 and FT232
                     * UsbSerialInterface.FLOW_CONTROL_DSR_DTR only for CP2102 and FT232
                     */
                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    serialPort.read(mCallback);


                    new ReadThread().start();


                    // Some Arduinos would need some sleep because firmware wait some time to know whether a new sketch is going
                    // to be uploaded or not
                    try {
                        Thread.sleep(50); // sleep some. YMMV with different chips.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Everything went as expected. Send an intent to MainActivity
                    Intent intent = new Intent(ACTION_USB_READY);
                    context.sendBroadcast(intent);

                    //check volume change and set pt2313 volume
                    mAudioStreamVolumeObserver = new AudioStreamVolumeObserver(context, audioStreamHandler, audioValues, serialPort);
                    context.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, mAudioStreamVolumeObserver);
                } else {
                    // Serial port could not be opened, maybe an I/O error or if CDC driver was chosen, it does not really fit
                    // Send an Intent to Main Activity
                    if (serialPort instanceof CDCSerialDevice) {
                        Intent intent = new Intent(ACTION_CDC_DRIVER_NOT_WORKING);
                        context.sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(ACTION_USB_DEVICE_NOT_WORKING);
                        context.sendBroadcast(intent);
                    }
                }
            } else {
                // No driver for given device, even generic CDC driver could not be loaded
                Intent intent = new Intent(ACTION_USB_NOT_SUPPORTED);
                context.sendBroadcast(intent);
            }
        }
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            while (true) {
                byte[] buffer = new byte[100];
                int n = serialPort.syncRead(buffer, 0);
                //Log.d(tag,""+buffer);
                if (n > 0) {
                    byte[] received = new byte[n];
                    System.arraycopy(buffer, 0, received, 0, n);
                    String receivedStr = new String(received);
                    mHandler.obtainMessage(SYNC_READ, receivedStr).sendToTarget();
                }
            }
        }
    }

    public void foregroundNotification() {
        Intent notificationIntent = new Intent(this, UsbService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this).setContentTitle("سرویس مولتی مدیا")
                    .setContentText("فعال")
                    .setSmallIcon(R.mipmap.car_multimedia_icon)
                    .setContentIntent(pendingIntent)
                    .setTicker("سرویس مولتی مدیا فعال شد.")
                    .build();

        }


        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }
}
