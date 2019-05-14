package ir.dev_roid.testusb;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import ir.dev_roid.testusb.app.AudioValues;
import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.MyAudioManager;
import ir.dev_roid.testusb.app.PrefManager;

public class TvChannelActivity extends AppCompatActivity {
    private ConnectUsbService connectUsbService;
    private MyAudioManager myAudioManager;
    private PrefManager pref;
    private AudioValues audioValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_channel);

        connectUsbService= new ConnectUsbService(TvChannelActivity.this);
        myAudioManager =new MyAudioManager(getApplicationContext());
        pref = new PrefManager(getApplicationContext());
        audioValues= new AudioValues(pref) ;



    }

    private void setToAV(){
        connectUsbService.write("oth-cnl-001?");
        myAudioManager.pauseHeadUnitMusicPlayer();
        sendData(audioValues.auxMode(),100);
        pref.setAUXAudioIsActive(true);
        pref.setHeadUnitAudioIsActive(false);
        pref.setRadioIsRun(false);
        sendData(audioValues.auxMode(),500);
    }

    private void setToHeadUnit(){
        connectUsbService.write("oth-cnl-000?");

        connectUsbService.write(audioValues.androidBTMode());
        sendData(audioValues.androidBTMode(),200);
        pref.setHeadUnitAudioIsActive(true);
        pref.setAUXAudioIsActive(false);
        pref.setRadioIsRun(false);
        sendData(audioValues.androidBTMode(),500);
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
        unbindService(connectUsbService.onDestroyUsb());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);

    }
}
