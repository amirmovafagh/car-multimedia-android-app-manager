package com.hooshmandkhodro.carservice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hooshmandkhodro.carservice.app.ArmRTC;
import com.hooshmandkhodro.carservice.app.AudioValues;
import com.hooshmandkhodro.carservice.app.CpuManager;
import com.hooshmandkhodro.carservice.app.GpioUart;
import com.hooshmandkhodro.carservice.app.MyAudioManager;
import com.hooshmandkhodro.carservice.app.PrefManager;
import com.hooshmandkhodro.carservice.app.ToolBar_ResideMenu;
import com.hooshmandkhodro.carservice.app.dagger.App;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import static com.hooshmandkhodro.carservice.MyHandler.buffer;


public class MainActivity extends AppCompatActivity {

    @Inject
    PrefManager prefManager;
    @Inject
    GpioUart gpioUart;
    private static final String tag = MainActivity.class.getSimpleName();

    private ToolBar_ResideMenu toolBarResideMenu;
    private boolean locationUpdated = false;
    MyAudioManager myAudioManager;
    CpuManager cpuManager;
    LocationManager mLocationManager;
    ArmRTC armRTC;


    private AudioValues audioValues;

    private String[] parts = {"", "", "", "", "", ""};

    private ImageButton aux, bluetooth, radio, tv, audioSettings, map, swc;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign singleton instances to fields
        // We need to cast to `App` in order to get the right method
        ((App) getApplicationContext()).getComponent().inject(this);

        startService(new Intent(MainActivity.this, UsbService.class));

        audioValues = new AudioValues(prefManager);
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
        armRTC = new ArmRTC(gpioUart);

        //toolbarInit
        toolBarResideMenu = new ToolBar_ResideMenu(this, "Multi Media", gpioUart, prefManager);
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
                //startActivity(new Intent(MainActivity.this, SteeringWheelContorllerActivity.class));
                //sendData(audioValues.androidBTMode(),100);
                sendData("oth-tmp-000?", 300);

            }
        });

        map.setOnClickListener(view -> {
            //changeSystemTime("2019","10","23","02","40","55"); just test

            /*if (parts[0] != null && parts[0] != "" && parts[5] != null && parts[5] != "" && locationUpdated) {
                changeSystemTime(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                locationUpdated = false;
            } else
                Toast.makeText(MainActivity.this, "?????????????? ???????????? ?????? ???? ?????? ????????", Toast.LENGTH_SHORT).show();*/


        });

        audioSettings.setOnClickListener(view -> {
            //startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            sendData("oth-tmp-002?", 300);
        });

        radio.setOnClickListener(v -> {
            gpioUart.sendData("oth-rtg?");
            if (buffer.contains("rtc-") && buffer.substring(10).equalsIgnoreCase("-") && Integer.parseInt(buffer.substring(11, 13)) != 0) {

                changeSystemTime(buffer.substring(4, 6), buffer.substring(6, 8), buffer.substring(8, 10), "20" + buffer.substring(11, 13),
                        buffer.substring(13, 15), buffer.substring(15, 17));

            }
            //sendData("oth-tmp-003?", 100);
            //startActivity(new Intent(MainActivity.this, RadioActivity.class));
            /*connectUsbService.write(audioValues.radioMode());
            myAudioManager.pauseHeadUnitMusicPlayer();
            pref.setHeadUnitAudioIsActive(false);
            pref.setAUXAudioIsActive(false);
            pref.setRadioIsRun(true);*/
        });

        tv.setOnClickListener(view -> {
            gpioUart.sendData("oth-cnl-001?");
            myAudioManager.pauseHeadUnitMusicPlayer();
            sendData(audioValues.auxMode(), 100);
            prefManager.setAUXAudioIsActive(true);
            prefManager.setHeadUnitAudioIsActive(false);
            prefManager.setRadioIsRun(false);
            startActivity(new Intent(MainActivity.this, TvChannelActivity.class));


        });

        bluetooth.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BluetoothActivity.class));
            /*connectUsbService.write(audioValues.androidBTMode());
            pref.setHeadUnitAudioIsActive(true);
            pref.setAUXAudioIsActive(false);
            pref.setRadioIsRun(false);*/

        });
        bluetooth.setOnLongClickListener(view -> {

            /*connectUsbService.write("oth?");
            connectUsbService.write(audioValues.auxMode());*/
            return false;
        });

        aux.setOnClickListener(v -> {

            gpioUart.sendData(audioValues.auxMode());
            myAudioManager.pauseHeadUnitMusicPlayer();
            prefManager.setAUXAudioIsActive(true);
            prefManager.setHeadUnitAudioIsActive(false);
            prefManager.setRadioIsRun(false);
            startActivity(new Intent(MainActivity.this, AUXActivity.class));
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
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, locationListener);

        if (cpuManager.getTemperature() > 79) {
            sendData("oth-tmp-001?", 0);

        }
        sendData(audioValues.getAudioValues(), 500);
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

        new Handler().postDelayed(() -> {

            if (gpioUart != null) gpioUart.sendData(data);
        }, delay);
    }


}
