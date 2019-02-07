package ir.dev_roid.testusb;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.PrefManager;
import ir.dev_roid.testusb.app.ToolBar_ResideMenu;
import ir.dev_roid.testusb.bluetoothFragments.MediaFragment;
import ir.dev_roid.testusb.bluetoothFragments.SettingsFragment;

import static ir.dev_roid.testusb.MyHandler.buffer;
import static ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment.PhoneDialerFragment.dialFragmentIsRun;


public class BluetoothActivity extends AppCompatActivity {
    private final String tag = BluetoothActivity.class.getSimpleName();
    private ToolBar_ResideMenu toolBarResideMenu;
    private Handler handler;
    private Runnable runnable;
    public static ConnectUsbService connectUsbServiceStatic;
    private PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        connectUsbServiceStatic = new ConnectUsbService(this);
        prefManager = new PrefManager(getBaseContext());
        //new ConnectUsbService(BluetoothActivity.this);
        //toolbarInit
        toolBarResideMenu = new ToolBar_ResideMenu(this, "Bluetooth", connectUsbServiceStatic, prefManager);
        //resideMenuInit
        toolBarResideMenu.resideMenuInit("Home", "Radio", "Settings",
                R.drawable.icon_home, R.drawable.icon_home, R.drawable.icon_home,
                MainActivity.class, RadioActivity.class, SettingsActivity.class, "Bluetooth");

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new SettingsFragment());

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (buffer.equalsIgnoreCase("MG4") || buffer.equalsIgnoreCase("MG5")) {
                    if (!dialFragmentIsRun) {
                        startActivity(new Intent(BluetoothActivity.this, TelephoneActivity.class));
                        finish();
                        //navigation.setSelectedItemId(R.id.action_dialler);
                        Log.i(tag, "bt");
                    }
                }
                handler.postDelayed(runnable, 3000);
            }
        };

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_media:
                    toolBarResideMenu.Title("MEDIA");
                    fragment = new MediaFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.action_phone:
                    toolBarResideMenu.Title("Phone");
                    startActivity(new Intent(BluetoothActivity.this, TelephoneActivity.class));
                    finish();
                    return true;
                case R.id.action_settings:
                    toolBarResideMenu.Title("Settings");
                    fragment = new SettingsFragment();
                    loadFragment(fragment);
                    return true;

            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connectUsbServiceStatic.onDestroyUsb());

    }
}
