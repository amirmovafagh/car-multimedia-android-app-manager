package com.hooshmandkhodro.carservice;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.hooshmandkhodro.carservice.app.AudioValues;
import com.hooshmandkhodro.carservice.app.MyAudioManager;
import com.hooshmandkhodro.carservice.app.SharedPreference;



import com.hooshmandkhodro.carservice.app.GpioUart;


public class TvChannelActivity extends AppCompatActivity {
    private GpioUart gpioUart;
    private MyAudioManager myAudioManager;
    private SharedPreference pref;
    private AudioValues audioValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_channel);


        gpioUart = new GpioUart(1);

        myAudioManager = new MyAudioManager(getApplicationContext());
        pref = new SharedPreference(getApplicationContext());
        audioValues = new AudioValues(pref);


    }

    private void setToAV() {
        gpioUart.sendData("oth-cnl-001?");
        myAudioManager.pauseHeadUnitMusicPlayer();
        sendData(audioValues.auxMode(), 100);
        pref.setAUXAudioIsActive(true);
        pref.setHeadUnitAudioIsActive(false);
        pref.setRadioIsRun(false);
        sendData(audioValues.auxMode(), 500);
    }

    private void setToHeadUnit() {
        gpioUart.sendData("oth-cnl-000?");

        gpioUart.sendData(audioValues.androidBTMode());
        sendData(audioValues.androidBTMode(), 200);
        pref.setHeadUnitAudioIsActive(true);
        pref.setAUXAudioIsActive(false);
        pref.setRadioIsRun(false);
        sendData(audioValues.androidBTMode(), 500);
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
    protected void onStart() {
        super.onStart();
        setToAV();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToAV();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setToHeadUnit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setToHeadUnit();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);

    }
}
