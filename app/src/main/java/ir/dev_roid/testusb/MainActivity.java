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

import ir.dev_roid.testusb.app.AudioValues;
import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.PrefManager;
import ir.dev_roid.testusb.app.ToolBar_ResideMenu;


public class MainActivity extends AppCompatActivity {

    private ToolBar_ResideMenu toolBarResideMenu;
    private PrefManager pref;
    int p;
    int i = 0;
    private AudioValues audioValues;


    private SeekBar lf, rf, lr, rr;


    private Button aux, pine, radio, bluetooth;

    private ConnectUsbService connectUsbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectUsbService = new ConnectUsbService(MainActivity.this);
        pref = new PrefManager(MainActivity.this);
        audioValues= new AudioValues(pref) ;
        Toast.makeText(this, ""+audioValues.getAudioValues(), Toast.LENGTH_LONG).show();

        lf = findViewById(R.id.left_front);
        rf = findViewById(R.id.right_front);
        lr = findViewById(R.id.left_rear);
        rr = findViewById(R.id.right_rear);

        //toolbarInit
        toolBarResideMenu = new ToolBar_ResideMenu(this, "Multi Media", connectUsbService, pref);
        //resideMenuInit
        toolBarResideMenu.resideMenuInit("Bluetooth", "Radio", "Settings",
                R.drawable.icon_home, R.drawable.icon_home, R.drawable.icon_home,
                BluetoothActivity.class, RadioActivity.class, SettingsActivity.class, "Home");


        radio = findViewById(R.id.radio);
        aux = findViewById(R.id.aux);
        pine = findViewById(R.id.pine);
        bluetooth = findViewById(R.id.bluetooth);





        radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectUsbService.write(audioValues.radioMode());
            }
        });

        pine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectUsbService.write(audioValues.androidBTMode());
            }
        });
        pine.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                connectUsbService.write("oth?");
                return false;
            }
        });

        aux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectUsbService.write(audioValues.auxMode());
            }
        });

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SteeringWheelContorllerActivity.class));
            }
        });

        lf.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioValues.setVolumeLeftFront(progress);
                connectUsbService.write(audioValues.getAudioValues());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rf.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioValues.setVolumeRightFront(progress);
                connectUsbService.write(audioValues.getAudioValues());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        lr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioValues.setVolumeLeftRear(progress);
                connectUsbService.write(audioValues.getAudioValues());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        rr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioValues.setVolumeRightRear(progress);
                connectUsbService.write(audioValues.getAudioValues());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        startHardwareInitializing();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Start listening notifications from UsbService
        startHardwareInitializing();
        //startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!pref.getRadioIsRun())
            sendData(audioValues.androidBTMode(), 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connectUsbService.onDestroyUsb());
    }

    private void startHardwareInitializing() {
        if (!pref.getRadioIsRun())
            sendData(audioValues.androidBTMode(), 500);
            sendData(audioValues.getAudioValues(), 500);


        lf.setProgress(pref.getVolumeValue(1));
        rf.setProgress(pref.getVolumeValue(2));
        lr.setProgress(pref.getVolumeValue(3));
        rr.setProgress(pref.getVolumeValue(4));
        /*sendData("aud-vol-" + (63 - pref.getVolumeValue(0)) + "?", 200);
        sendData("aud-vlf-" + (223 - pref.getVolumeValue(1)) + "?", 300);
        sendData("aud-vrf-" + (255 - pref.getVolumeValue(2)) + "?", 400);
        sendData("aud-vlr-" + (159 - pref.getVolumeValue(3)) + "?", 500);
        sendData("aud-vrr-" + (191 - pref.getVolumeValue(4)) + "?", 600);
        sendData("aud-bas-" + (112 - pref.getVolumeValue(5)) + "?", 700);*/

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
