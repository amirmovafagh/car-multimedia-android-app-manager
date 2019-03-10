package ir.dev_roid.testusb;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import ir.dev_roid.testusb.app.AudioValues;
import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.MyAudioManager;
import ir.dev_roid.testusb.app.PrefManager;
import ir.dev_roid.testusb.app.ToolBar_ResideMenu;


public class MainActivity extends AppCompatActivity {

    private ToolBar_ResideMenu toolBarResideMenu;
    private PrefManager pref;
    MyAudioManager myAudioManager;
    int p;
    int i = 0;
    private AudioValues audioValues;
    private BoxedVertical soundModule;





    private Button aux, pine, radio, bluetooth;

    private ConnectUsbService connectUsbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectUsbService = new ConnectUsbService(MainActivity.this);
        pref = new PrefManager(MainActivity.this);
        audioValues= new AudioValues(pref) ;
        myAudioManager = new MyAudioManager(getApplicationContext());

        soundModule = findViewById(R.id.sound_module_seekbar);
        soundModule.setValue(pref.getVolumeValue(0));

        //toolbarInit
        toolBarResideMenu = new ToolBar_ResideMenu(this, "Multi Media", connectUsbService, pref);
        //resideMenuInit
        toolBarResideMenu.resideMenuInit("Bluetooth", "Radio", "Settings",
                R.drawable.icon_home, R.drawable.icon_home, R.drawable.icon_home,
                BluetoothActivity.class, RadioActivity.class, SettingsActivity.class, "Home");


        radio = findViewById(R.id.radio);
        aux = findViewById(R.id.aux);
        pine = findViewById(R.id.pine);






        radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectUsbService.write(audioValues.radioMode());
                pref.setHeadUnitAudioIsActive(false);
                pref.setAUXAudioIsActive(false);
                pref.setRadioIsRun(true);
            }
        });

        pine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectUsbService.write(audioValues.androidBTMode());
                pref.setHeadUnitAudioIsActive(true);
                pref.setAUXAudioIsActive(false);
                pref.setRadioIsRun(false);

            }
        });
        pine.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                /*connectUsbService.write("oth?");
                connectUsbService.write(audioValues.auxMode());*/
                return false;
            }
        });

        aux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectUsbService.write(audioValues.auxMode());
                myAudioManager.pauseHeadUnitMusicPlayer();
                pref.setAUXAudioIsActive(true);
                pref.setHeadUnitAudioIsActive(false);
                pref.setRadioIsRun(false);
            }
        });

        soundModule.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedVertical, int i) {
                audioValues.setVolume(i);
                sendData(audioValues.getAudioValues(),10);

            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedVertical) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedVertical) {

            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        //startHardwareInitializing();
    }

    @Override
    public void onResume() {
        super.onResume();
        soundModule.setValue(pref.getVolumeValue(0));
        //Start listening notifications from UsbService
        //startHardwareInitializing();
        //startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if (!pref.getRadioIsRun()){
            sendData(audioValues.androidBTMode(), 4000);
            }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connectUsbService.onDestroyUsb());
    }

    private void startHardwareInitializing() {
        /*if (!pref.getRadioIsRun()){  // make problem when the acc witched becuse sound modole is off and mcu will crash
            sendData(audioValues.androidBTMode(), 500);
            sendData(audioValues.getAudioValues(), 500);
        }
*/


    }

    private void sendData(final String data, int delay) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectUsbService.write(data);
            }
        }, delay);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

return super.onTouchEvent(event);

    }
}
