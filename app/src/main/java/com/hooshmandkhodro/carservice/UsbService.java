package com.hooshmandkhodro.carservice;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.hooshmandkhodro.carservice.app.ArmRTC;
import com.hooshmandkhodro.carservice.app.AudioValues;
import com.hooshmandkhodro.carservice.app.Brightness;
import com.hooshmandkhodro.carservice.app.CpuManager;
import com.hooshmandkhodro.carservice.app.GpioUart;
import com.hooshmandkhodro.carservice.app.MyAudioManager;
import com.hooshmandkhodro.carservice.app.ObservableInteger;
import com.hooshmandkhodro.carservice.app.PrefManager;
import com.hooshmandkhodro.carservice.app.dagger.App;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment.PhoneDialerFragment;
import com.hooshmandkhodro.carservice.steeringWheelController.ProvidedModelOps;
import com.hooshmandkhodro.carservice.steeringWheelController.SteeringWheelControllerModel;
import com.hooshmandkhodro.carservice.steeringWheelController.SteeringWheelControllerPresenter;

import java.util.List;


import com.hooshmandkhodro.carservice.steeringWheelController.Pojo.ControllerOption;
import com.hooshmandkhodro.carservice.steeringWheelController.Pojo.Options;

import javax.inject.Inject;

import static com.hooshmandkhodro.carservice.MyHandler.buffer;
import static com.hooshmandkhodro.carservice.RadioActivity.RadioSoundChannelActivity;

/**
 * Created by hirad on 3/1/18.
 */

public class UsbService extends Service {
    private static final String tag = UsbService.class.getSimpleName();
    @Inject
    GpioUart gpioUart;
    @Inject
    PrefManager prefManager;
    public static final int MESSAGE_FROM_GPIO_UART_TTYS1 = 1;
    private static int ONGOING_NOTIFICATION_ID = 1;
    AudioStreamVolumeObserver mAudioStreamVolumeObserver;
    private ProvidedModelOps modelOps;
    private List<ControllerOption> options;


    private MyAudioManager audioManager;

    private ArmRTC armRTC;

    private ObservableInteger obsInit;
    private Brightness brightness;
    private Context context;
    private Handler mHandler, checkCpuTempHandler, audioStreamHandler;
    private Runnable checkCpuTempRunnable;
    private MyHandler myHandler;
    private Runnable carSteeringWheelRunnable, uartReadDataRunnable;
    private CpuManager cpu;
    public static boolean swcDelay = false;
    public static boolean isPlayMusic = false;

    public static boolean isStopMusic = false;

    public static boolean threadStatus = true;

    private AudioValues audioValues;
    public static int handlerDelay = 2500;
    private static boolean criticalTemp = false;
    private static int maxTemp = 68;
    private static int middleTemp = 60;
    private static int minTemp = 52;
    private static int emergencyTemp = 75;
    private static boolean emergencyTempOn = false;


    /*
     * onCreate will be executed when service is started. It configures an IntentFilter to listen for
     * incoming Intents (USB ATTACHED, USB DETACHED...) and it tries to open a serial port.
     */
    @Override
    public void onCreate() {
        ((App) getApplicationContext()).getComponent().inject(this);
        this.context = this;
        armRTC = new ArmRTC(gpioUart);
        cpu = new CpuManager();
        cpu.InitTouchConfig();
        modelOps = SteeringWheelControllerModel.getInstance(this);
        modelOps.createAllDaosIfNotExsit();
        MyHandler.steeringWheelData = 999;
        audioStreamHandler = new Handler(Looper.getMainLooper());

        checkCpuTempHandler = new Handler(Looper.getMainLooper());
        checkCpuTempRunnable = () -> {
            checkCpuTemp();
            checkCpuTempHandler.postDelayed(checkCpuTempRunnable, 10000);
        };
        checkCpuTempHandler.postDelayed(checkCpuTempRunnable, 2000);
        //logger = new Logger();

        audioManager = new MyAudioManager(context);
        brightness = new Brightness(context);
        obsInit = new ObservableInteger();

        armRTC.setTimeOnHeadUnit();


        myHandler = new MyHandler(context, prefManager);
        setHandler(myHandler);
        foregroundNotification();
        CheckCallStatus();
        //startService(new Intent(getBaseContext(), SteeringWheelControllerService.class));

        audioValues = new AudioValues(prefManager);

        obsInit.setOnIntegerChangeListener(newValue -> {
            if (newValue != prefManager.getBrightnessValue()) {

                /*new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Do something after 100ms

                    }
                }, 250);*/
                sendData_delay("oth-brg-" + (newValue / 3) + "?", 250);

                prefManager.setBrightnessValue(newValue);
            }
        });


        mAudioStreamVolumeObserver = new AudioStreamVolumeObserver(this, new Handler(), audioValues, gpioUart);
        this.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, mAudioStreamVolumeObserver);


        carSteeringWheelRunnable = () -> {
            //Log.i("AMIR", "" + buffer);

            if( MyHandler.touchBtnPanelData != 999){
                switch (MyHandler.touchBtnPanelData) {
                    case 1:
                        //Log.i("AMIR", " G");
                        mAudioStreamVolumeObserver.muteSWC();
                        break;
                    case 2:
                        mAudioStreamVolumeObserver.increaseVolumeSWC();
                        //Log.i("AMIR", " UP");
                        break;
                    case 3:
                        mAudioStreamVolumeObserver.decreaseVolumeSWC();
                        //Log.i("AMIR", " down");
                        break;
                    case 4:
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        break;
                }
                MyHandler.touchBtnPanelData = 999;
            }
            options = modelOps.getAllControllerOptions();
            for (ControllerOption co : options) {
                if (co.getValue() != null) {

                    int i = co.getValue();
                    if (i - 3 <= MyHandler.steeringWheelData && MyHandler.steeringWheelData <= i + 3) {
                        Options id = co.getId();
                        mAudioStreamVolumeObserver.swcDelayOn();
                        swcDelay = true;
                        switch (id) {
                            case OPT0:
                                Log.i(tag, "#power" + MyHandler.steeringWheelData);

                                break;
                            case OPT1:
                                Log.i(tag, "#SRC" + MyHandler.steeringWheelData);
                                Intent dialogIntent = new Intent(getApplicationContext(), MainActivity.class);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(dialogIntent);
                                break;
                            case OPT2:
                                Log.i(tag, "#gps" + MyHandler.steeringWheelData);
                                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.sygic.aura");
                                if (launchIntent != null) {
                                    startActivity(launchIntent);//null pointer check in case package name was not found
                                }
                                break;
                            case OPT3:
                                Log.i(tag, "#upVolume" + MyHandler.steeringWheelData);
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
                                Log.i(tag, "#downVolume" + MyHandler.steeringWheelData);
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

                                Log.i(tag, "#mute" + MyHandler.steeringWheelData);
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
                                Log.i(tag, "#play" + MyHandler.steeringWheelData);
                                if (prefManager.getBluetoothPlayerState()) {
                                    sendData_delay("blt-mus-ppp?", 300);
                                    Log.i("AMIR", "ppp");
                                } else
                                    audioManager.playHeadUnitMusicPlayer();

                                break;
                            case OPT7:
                                disableCheckCallStatus();
                                Log.i(tag, "#back" + MyHandler.steeringWheelData);
                                if (prefManager.getBluetoothPlayerState()) {
                                    sendData_delay("blt-mus-bwd?", 300);
                                    Log.i("AMIR", "bwd");
                                } else
                                    audioManager.previousHeadUnitMusicPlayer();

                                break;
                            case OPT8:

                                Log.i(tag, "#next" + MyHandler.steeringWheelData);
                                if (prefManager.getBluetoothPlayerState()) {
                                    sendData_delay("blt-mus-fwd?", 300);
                                    Log.i("AMIR", "fwd");
                                } else
                                    audioManager.nextHeadUnitMusicPlayer();

                                break;
                            case OPT10:
                                Log.i(tag, "#<--" + MyHandler.steeringWheelData);
                                break;
                            case OPT11:
                                Log.i(tag, "#-->" + MyHandler.steeringWheelData);
                                break;
                            case OPT12:
                                Log.i(tag, "#answer" + MyHandler.steeringWheelData);
                                if (MyHandler.telephoneActivityIsOpen) {
                                    sendData_delay("blt-cll-ans?", 50);
                                } else {
                                    Intent intent = new Intent(context, TelephoneActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }

                                break;
                            case OPT13:
                                Log.i(tag, "#end" + MyHandler.steeringWheelData);
                                sendData_delay("blt-cll-end?", 50);
                                break;

                        }

                        MyHandler.steeringWheelData = 999;
                        swcDelay = false;
                    }
                }
                //Log.i(tag, "Dynamic Array Index #" + num.getId() );
            }
            MyHandler.touchBtnPanelData = 999;
        };
        uartReadDataRunnable = () -> {
            if (mHandler != null)
                mHandler.obtainMessage(MESSAGE_FROM_GPIO_UART_TTYS1, gpioUart.readData()).sendToTarget();
        };
        performOnBackgroundThreadSteeringWheel(carSteeringWheelRunnable);
        performOnBackgroundThreadReadDataUart(uartReadDataRunnable);
    }

    private static void performOnBackgroundThreadSteeringWheel(final Runnable runnable) {

        final Thread tsw = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        sleep(10);
                        //Log.i(tag, "#RUN" );
                        if (!SteeringWheelControllerPresenter.SteeringWheelControllerActivityIsRun) {
                            runnable.run();
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        };

        tsw.start();

    }

    private static void performOnBackgroundThreadReadDataUart(final Runnable runnable) {

        final Thread tdu = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        sleep(100);
                        //Log.i(tag, "#RUN" );

                        runnable.run();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        };

        tdu.start();
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
                    checkMCUstate();

                    if (threadStatus) {
                        //Log.d(tag, "service...");
                        String data = "blt-cll-chk?";
                        sendData(data);
                    }
                    if (!PhoneDialerFragment.dialFragmentIsRun) {
                        checkAudioManager();
                    }
                    obsInit.set(brightness.getScreenBrightness());
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
            if (temp > 80) {
                Toast.makeText(context, "HIGH Temperature " + temp + "C !!!", Toast.LENGTH_SHORT).show();
                if (!criticalTemp) {
                    new Handler().postDelayed(() -> {
                        cpu.cpuMaxFrequency(912000);
                        criticalTemp = true;
                    }, 0);
                }
            }
            if (temp > emergencyTemp) {
                emergencyTempOn = true;
                sendData_delay("oth-tmp-003?", 300);
            } else if (temp > maxTemp && !emergencyTempOn && threadStatus) {

                sendData_delay("oth-tmp-002?", 300);
                //fanState = true;
                //Log.i(tag, " fan on");


            } else if (temp > middleTemp && !emergencyTempOn && threadStatus) {
                sendData_delay("oth-tmp-001?", 300);
            } else if (temp < minTemp) {
                emergencyTempOn = false;
                sendData("oth-tmp-000?");
                if (criticalTemp) {
                    new Handler().postDelayed(() -> {
                        cpu.cpuMaxFrequency(1104000);
                        Log.i(tag, " set on 1.1gh");
                        criticalTemp = false;
                    }, 0);
                }
                //fanState = false;
                //Log.i(tag, " fan off");
            }
        }
    }

    private void gotoMainScreen() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }

    private void checkMCUstate() {

        if (buffer.equalsIgnoreCase("RUN")) {
            cpu.InitTouchConfig();
            //gotoMainScreen();
            maxTemp = 68;
            middleTemp = 60;
            minTemp = 52;
            emergencyTemp = 75;
            cpu.cpuGoverner("interactive");
            Settings.System.putInt(getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, 9999999);
            /*try {
                PowerManager.WakeLock wakeLock;
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "lock_me");
                wakeLock.acquire();
            } catch (Exception e) {
            }*/
            threadStatus = true;
            //sendData(audioValues.getAudioValues(), 100);
            //sendData(audioValues.getAudioValues(), 400);
            //cpu.cpuOnlineStateConfig(3,1);
            buffer = "";
        }
        if (buffer.equalsIgnoreCase("OFF")) {
            cpu.deInitTouchConfig();
            gotoMainScreen();
            minTemp = 59;
            emergencyTemp = 74;

            threadStatus = false;

            cpu.cpuGoverner("powersave");
            Settings.System.putInt(getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, 15000);
            buffer = "";
            //cpu.cpuOnlineStateConfig(3,0);
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
                    sendData_delay("oth-amp-001?", 100);

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
                sendData_delay(audioValues.androidBTMode(), 250);
                Log.i(tag, "1.3");
                if (prefManager.getDebugModeState())
                    Toast.makeText(context, "<<--set sound channel from RADIO to HU-->>1.3", Toast.LENGTH_SHORT).show();
                prefManager.setHeadUnitAudioIsActive(true);
                prefManager.setRadioIsRun(false);
            }

            if (!prefManager.getHeadUnitAudioIsActive() && prefManager.getAUXAudioIsActive() && !AUXActivity.auxSoundChannelActivity) { //if AUX is ACTIVATE switch channnel to the headUnit
                sendData_delay(audioValues.androidBTMode(), 150);
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
                    sendData_delay("oth-amp-000?", 200);
                    if (prefManager.getDebugModeState())
                        Toast.makeText(context, "<<--amplifire is off-->>0.2", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void sendData_delay(final String data, int delay) {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (gpioUart != null)
                gpioUart.sendData(data);
        }, delay);
    }

    private void sendData(final String data) {
        if (gpioUart != null)
            gpioUart.sendData(data);
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, "onDestroy service");

        getApplicationContext().getContentResolver().unregisterContentObserver(mAudioStreamVolumeObserver);


    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void foregroundNotification() {
        Intent notificationIntent = new Intent(this, UsbService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this).setContentTitle("?????????? ?????????? ????????")
                    .setContentText("????????")
                    .setSmallIcon(R.mipmap.car_multimedia_icon)
                    .setContentIntent(pendingIntent)
                    .setTicker("?????????? ?????????? ???????? ???????? ????.")
                    .build();

        }
        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }
}
