package ir.dev_roid.testusb;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.HardwarePropertiesManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import ir.dev_roid.testusb.app.AudioValues;
import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.CpuManager;
import ir.dev_roid.testusb.app.MyAudioManager;
import ir.dev_roid.testusb.app.PrefManager;
import ir.dev_roid.testusb.app.ToolBar_ResideMenu;


public class MainActivity extends AppCompatActivity {
    private static final String tag = MainActivity.class.getSimpleName();

    private ToolBar_ResideMenu toolBarResideMenu;
    private PrefManager pref;
    private boolean locationUpdated = false;
    MyAudioManager myAudioManager;
    CpuManager cpuManager;
    LocationManager mLocationManager;

    private AudioValues audioValues;

    private String[] parts = {"", "", "", "", "", ""};

    private ImageButton aux, bluetooth, radio, tv, audioSettings, map, swc;

    private ConnectUsbService connectUsbService;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectUsbService = new ConnectUsbService(MainActivity.this);
        pref = new PrefManager(MainActivity.this);
        audioValues = new AudioValues(pref);
        myAudioManager = new MyAudioManager(getApplicationContext());
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10,
                0, locationListener);


        cpuManager = new CpuManager();
        cpuManager.cpuMinFrequency(480000);
        cpuManager.cpuMaxFrequency(816000);

        //toolbarInit
        toolBarResideMenu = new ToolBar_ResideMenu(this, "Multi Media", connectUsbService, pref);
        //resideMenuInit
        toolBarResideMenu.resideMenuInit("Bluetooth", "Radio", "Settings",
                R.drawable.icon_home, R.drawable.icon_home, R.drawable.icon_home,
                BluetoothActivity.class, RadioActivity.class, SettingsActivity.class, "Home");


        //checkResolution();

        radio = findViewById(R.id.image_view_radio);
        aux = findViewById(R.id.image_view_aux);
        bluetooth = findViewById(R.id.image_view_bluetooth);
        tv = findViewById(R.id.image_view_tv);
        audioSettings = findViewById(R.id.image_view_audioSettings);
        swc = findViewById(R.id.image_view_steering_wheel);
        map = findViewById(R.id.image_view_map);


        if (cpuManager.getTemperature() > 79) {
            sendData("oth-tmp-001?", 100);

        }

        swc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SteeringWheelContorllerActivity.class));
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.sygic.aura");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }*/
                if (parts[0] != null && parts[0] != "" && parts[5] != null && parts[5] != "" && locationUpdated) {
                    changeSystemTime(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                    locationUpdated = false;
                } else
                    Toast.makeText(MainActivity.this, "اطلاعات موقعیت یاب به روز نیست", Toast.LENGTH_SHORT).show();
            }
        });

        audioSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, RadioActivity.class));
                /*connectUsbService.write(audioValues.radioMode());
                myAudioManager.pauseHeadUnitMusicPlayer();
                pref.setHeadUnitAudioIsActive(false);
                pref.setAUXAudioIsActive(false);
                pref.setRadioIsRun(true);*/
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectUsbService.write("oth-cnl-001?");
                myAudioManager.pauseHeadUnitMusicPlayer();
                sendData(audioValues.auxMode(), 100);
                pref.setAUXAudioIsActive(true);
                pref.setHeadUnitAudioIsActive(false);
                pref.setRadioIsRun(false);
                startActivity(new Intent(MainActivity.this, TvChannelActivity.class));


            }
        });

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BluetoothActivity.class));
                /*connectUsbService.write(audioValues.androidBTMode());
                pref.setHeadUnitAudioIsActive(true);
                pref.setAUXAudioIsActive(false);
                pref.setRadioIsRun(false);*/

            }
        });
        bluetooth.setOnLongClickListener(new View.OnLongClickListener() {
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
                startActivity(new Intent(MainActivity.this, AUXActivity.class));
            }
        });



    }

    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            String gpsTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", new Locale("en", "IR"))
                    .format(new Date(location.getTime()));
            parts = gpsTime.split("-");
            if (location.getProvider().equals(android.location.LocationManager.GPS_PROVIDER)) {
                android.util.Log.d("Location", "Time GPS: " + gpsTime); // This is what we want!

            } else
                android.util.Log.d("Location", "Time Device (" + location.getProvider() + "): " + gpsTime);
            locationUpdated = true;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void checkResolution() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        double x = Math.pow(width / displayMetrics.xdpi, 2);
        double y = Math.pow(height / displayMetrics.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        Toast.makeText(this, "" + height + " * " + width + " " + screenInches + " inch", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //startHardwareInitializing();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, locationListener);

        if (cpuManager.getTemperature() > 79) {
            sendData("oth-tmp-001?", 0);

        }

        //Start listening notifications from UsbService
        //startHardwareInitializing();
        //startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(locationListener);
        /*if (!pref.getRadioIsRun()){
            sendData(audioValues.androidBTMode(), 4000);
            }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connectUsbService.onDestroyUsb());
    }

    private void changeSystemTime(String year, String month, String day, String hour, String minute, String second) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            String command = "date -s " + year + month + day + "." + hour + minute + second + "\n";
            Log.e("command", command);
            os.writeBytes(command);
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendData(final String data, int delay) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectUsbService.write(data);
            }
        }, delay);
    }


}
