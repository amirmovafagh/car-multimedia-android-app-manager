package ir.dev_roid.testusb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import ir.dev_roid.testusb.app.AudioValues;
import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.MyAudioManager;
import ir.dev_roid.testusb.app.PrefManager;

public class AUXActivity extends AppCompatActivity {
    public static boolean auxSoundChannelActivity = true;
    private ConnectUsbService connectUsbService;
    private AudioValues audioValues;
    private BoxedVertical soundModule;
    private PrefManager pref;
    MyAudioManager myAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aux);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connectUsbService = new ConnectUsbService(AUXActivity.this);
        pref = new PrefManager(AUXActivity.this);
        audioValues = new AudioValues(pref);
        myAudioManager = new MyAudioManager(getApplicationContext());
        soundModule = findViewById(R.id.sound_module_seekbar_aux);
        soundModule.setValue(pref.getVolumeValue(0));
        soundModule.setMax(pref.getVolumeValue(13));
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setSize(3);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                startActivity(new Intent(AUXActivity.this, SettingsActivity.class));
            }
        });

        soundModule.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedVertical, int i) {
                audioValues.setVolume(i);
                sendData(audioValues.getVolumeValue(), 10);

            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedVertical) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedVertical) {

            }
        });


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
    protected void onResume() {
        super.onResume();
        auxSoundChannelActivity = true;
        runActivity();


    }

    @Override
    protected void onPause() {
        super.onPause();
        auxSoundChannelActivity = false;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        closeActivity();
        unbindService(connectUsbService.onDestroyUsb());
    }

    private void closeActivity() {

        connectUsbService.write(audioValues.androidBTMode());
        pref.setRadioIsRun(false);
        pref.setAUXAudioIsActive(false);
        pref.setHeadUnitAudioIsActive(true);
    }

    private void runActivity() {

        connectUsbService.write(audioValues.auxMode());
        myAudioManager.pauseHeadUnitMusicPlayer();
        pref.setAUXAudioIsActive(true);
        pref.setHeadUnitAudioIsActive(false);
        pref.setRadioIsRun(false);
        if (soundModule != null)
            soundModule.setValue(pref.getVolumeValue(0));
    }
}
