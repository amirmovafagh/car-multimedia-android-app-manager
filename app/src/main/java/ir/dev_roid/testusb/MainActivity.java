package ir.dev_roid.testusb;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.CustomDialog;
import ir.dev_roid.testusb.app.PrefManager;
import ir.dev_roid.testusb.app.ToolBar_ResideMenu;
import ir.dev_roid.testusb.steeringWheelController.SteeringWheelContorllerActivity;


public class MainActivity extends AppCompatActivity {

    private ResideMenu resideMenu;
    private ToolBar_ResideMenu toolBarResideMenu;
    private Handler handlerHardwareInitializing;
    private PrefManager pref;
    int p;
    int i = 0;

    private SeekBar lf,rf,lr,rr;


    private Button aux,pine,radio,bluetooth;

    private ConnectUsbService connectUsbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        lf = (SeekBar) findViewById(R.id.left_front);
        rf = (SeekBar) findViewById(R.id.right_front);
        lr = (SeekBar) findViewById(R.id.left_rear);
        rr = (SeekBar) findViewById(R.id.right_rear);

        //toolbarInit
        toolBarResideMenu = new ToolBar_ResideMenu(this, "Multi Media");
        //resideMenuInit
        toolBarResideMenu.resideMenuInit("Bluetooth", "Radio", "Settings",
                R.drawable.icon_home, R.drawable.icon_home, R.drawable.icon_home,
                BluetoothActivity.class, RadioActivity.class, SettingsActivity.class, "Home");

        connectUsbService = new ConnectUsbService(MainActivity.this);
        pref = new PrefManager(MainActivity.this);


        radio = (Button) findViewById(R.id.radio);
        aux = (Button) findViewById(R.id.aux);
        pine = (Button) findViewById(R.id.pine);
        bluetooth = (Button) findViewById(R.id.bluetooth);


        startHardwareInitializing();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        double x = Math.pow(width/displayMetrics.xdpi,2);
        double y = Math.pow(height/displayMetrics.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        Toast.makeText(MainActivity.this, ""+width+" * "+height+" inch:"+screenInches, Toast.LENGTH_SHORT).show();
        Log.i("AAA",width+" ,"+height+" ,"+screenInches);



        radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectUsbService.write("mod-rad?");
            }
        });

        pine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectUsbService.write("mod-pin?");
            }
        });

        aux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectUsbService.write("mod-aux?");
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
                p=progress;
                connectUsbService.write("aud-vlf-"+(223-progress)+"?");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pref.setVolumeValue(1, p);
            }
        });

        rf.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                p=progress;
                connectUsbService.write("aud-vrf-"+(255-progress)+"?");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pref.setVolumeValue(2, p);
            }
        });

        lr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                p=progress;
                connectUsbService.write("aud-vlr-"+(159-progress)+"?");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pref.setVolumeValue(3, p);
            }
        });

        rr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                p=progress;
                connectUsbService.write("aud-vrr-"+(191-progress)+"?");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pref.setVolumeValue(4, p);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
          //Start listening notifications from UsbService
        //startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();

        //unbindService(usbConnection); //when devicee diconnected force close
    }


    private void startHardwareInitializing(){

        lf.setProgress(pref.getVolumeValue(1));
        rf.setProgress(pref.getVolumeValue(2));
        lr.setProgress(pref.getVolumeValue(3));
        rr.setProgress(pref.getVolumeValue(4));

        handlerHardwareInitializing =new Handler();
                handlerHardwareInitializing.postDelayed(new Runnable() {

            @Override
            public void run() {
                switch (i){

                    case 0:
                        connectUsbService.write("aud-vol-"+(63-pref.getVolumeValue(0))+"?");
                        handlerHardwareWait(this,500);
                        break;
                    case 1:
                        connectUsbService.write("aud-vlf-"+(223-pref.getVolumeValue(1))+"?");
                        handlerHardwareWait(this,500);
                        break;
                    case 2:
                        connectUsbService.write("aud-vrf-"+(255-pref.getVolumeValue(2))+"?");
                        handlerHardwareWait(this,500);
                        break;
                    case 3:
                        connectUsbService.write("aud-vlr-"+(159-pref.getVolumeValue(3))+"?");
                        handlerHardwareWait(this,500);
                        break;
                    case 4:
                        connectUsbService.write("aud-vrr-"+(191-pref.getVolumeValue(4))+"?");

                        break;
                    case 5:
                        connectUsbService.write("aud-bas-"+(112-pref.getVolumeValue(5))+"?");
                        handlerHardwareInitializing.removeCallbacks(this);
                        break;

                }
                i++;

            }
        },500);

    }

    private void handlerHardwareWait(Runnable r,int delay){
        handlerHardwareInitializing.postDelayed(r,500);
    }


}
