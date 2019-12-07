package com.hooshmandkhodro.carservice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hooshmandkhodro.carservice.app.AudioValues;
import com.hooshmandkhodro.carservice.app.MyAudioManager;
import com.hooshmandkhodro.carservice.app.PrefManager;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;


import com.hooshmandkhodro.carservice.app.GpioUart;
import com.hooshmandkhodro.carservice.app.dagger.App;

import javax.inject.Inject;

public class AUXActivity extends AppCompatActivity {
    @Inject
    PrefManager prefManager;
    public static boolean auxSoundChannelActivity = true;
    @Inject
    GpioUart gpioUart;
    private AudioValues audioValues;
    private BoxedVertical soundModule;
    MyAudioManager myAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aux);
        ((App)getApplicationContext()).getComponent().inject(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



//        pref = new PrefManager(AUXActivity.this);
        audioValues = new AudioValues(prefManager);
        myAudioManager = new MyAudioManager(getApplicationContext());
        soundModule = findViewById(R.id.sound_module_seekbar_aux);
        soundModule.setValue(prefManager.getVolumeValue(0));
        soundModule.setMax(prefManager.getVolumeValue(13));
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
                gpioUart.sendData(data);
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

    }

    private void closeActivity() {

        gpioUart.sendData(audioValues.androidBTMode());
        prefManager.setRadioIsRun(false);
        prefManager.setAUXAudioIsActive(false);
        prefManager.setHeadUnitAudioIsActive(true);
    }

    private void runActivity() {

        gpioUart.sendData(audioValues.auxMode());
        myAudioManager.pauseHeadUnitMusicPlayer();
        prefManager.setAUXAudioIsActive(true);
        prefManager.setHeadUnitAudioIsActive(false);
        prefManager.setRadioIsRun(false);
        if (soundModule != null)
            soundModule.setValue(prefManager.getVolumeValue(0));
    }
}
