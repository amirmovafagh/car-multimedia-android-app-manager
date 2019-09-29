package com.hooshmandkhodro.carservice.bluetoothFragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.hooshmandkhodro.carservice.R;
import com.hooshmandkhodro.carservice.app.AudioValues;
import com.hooshmandkhodro.carservice.app.MyAudioManager;



import static com.hooshmandkhodro.carservice.BluetoothActivity.connectGpioUartBt;
import static com.hooshmandkhodro.carservice.MyHandler.buffer;

import com.hooshmandkhodro.carservice.app.GpioUart;
import com.hooshmandkhodro.carservice.app.SharedPreference;

public class MediaFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView imgDisk;
    private ImageButton previous, next, play, pause, stop, decVolume, incVolume;
    private GpioUart gpioUart;
    private SharedPreference sharedPreference;
    private MyAudioManager audioManager;
    private AudioValues audioValues;

    private Handler handler;
    private Runnable runnable;


    public MediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gpioUart = connectGpioUartBt;
        sharedPreference = new SharedPreference(getContext());
        audioValues = new AudioValues(sharedPreference);
        audioManager = new MyAudioManager(getContext());
        handler = new Handler();

        audioManager.pauseHeadUnitMusicPlayer();
        sharedPreference.setHeadUnitAudioIsActive(false);
        sharedPreference.setBluetoothPlayerState(true);
        startHandlerMusicPlayerState();
        if (sharedPreference.getBluetoothPlayerState()) {
            playWhithOutCMD();
        }


    }

    private void startHandlerMusicPlayerState() {

        runnable = new Runnable() {
            @Override
            public void run() {
                if (buffer.equalsIgnoreCase("MB") || buffer.equalsIgnoreCase("MR")) {
                    playWhithOutCMD();
                    Log.i("test", "...");
                } else if (buffer.equalsIgnoreCase("MP")) {
                    pauseWhithOutCMD();
                    Log.i("test", "..");
                } else if (buffer.equalsIgnoreCase("MA")) {
                    Log.i("test", ".");
                    stopWithoutCommand();
                } else if (buffer.equalsIgnoreCase("MG1")) {
                    pauseWhithOutCMD();
                }

                handler.postDelayed(runnable, 3000);
            }
        };
        handler.postDelayed(runnable, 100);
    }


    private void setAnimateMusicLogo(ImageView imageView) {
        Animation a = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        a.setRepeatCount(-1);
        a.setDuration(9000);
        imageView.startAnimation(a);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_media, container, false);
        imgDisk = view.findViewById(R.id.img_disk);
        previous = view.findViewById(R.id.img_btn_previous);
        previous.setOnClickListener(this);
        next = view.findViewById(R.id.img_btn_next);
        next.setOnClickListener(this);
        play = view.findViewById(R.id.img_btn_play);
        play.setOnClickListener(this);
        stop = view.findViewById(R.id.img_btn_stop);
        stop.setOnClickListener(this);
        decVolume = view.findViewById(R.id.img_btn_dec_volume);
        decVolume.setOnClickListener(this);
        incVolume = view.findViewById(R.id.img_btn_inc_volume);
        incVolume.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_btn_previous:
                gpioUart.sendData("blt-mus-bwd?");
                return;
            case R.id.img_btn_next:
                gpioUart.sendData("blt-mus-fwd?");
                return;
            case R.id.img_btn_play:
                if (sharedPreference.getBluetoothPlayerState())
                    play();
                else
                    pause();
                return;
            case R.id.img_btn_stop:
                stop();
                return;
            case R.id.img_btn_dec_volume:
                gpioUart.sendData("blt-mus-vdn?");
                return;
            case R.id.img_btn_inc_volume:
                gpioUart.sendData("blt-mus-vup?");
                return;
        }
    }

    private void amplifireState() {
        if (sharedPreference.getAmplifireState()) {
            gpioUart.sendData("oth-amp-001?");
            sendData("oth-amp-001?", 100);
        } else {
            gpioUart.sendData("oth-amp-000?");
            sendData("oth-amp-000?", 100);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedPreference.setBluetoothPlayerState(true);
        checkVoiceChannel();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gpioUart.sendData("oth-amp-000?");
        sharedPreference.setBluetoothPlayerState(false);
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreference.setBluetoothPlayerState(true);
        amplifireState();
        checkVoiceChannel();
        handler.postDelayed(runnable, 100);


    }

    private void stopWithoutCommand() {
        //sharedPreference.setBluetoothPlayerState(false);
        play.setImageResource(R.drawable.play);

        imgDisk.clearAnimation();

        handler.removeCallbacksAndMessages(null);
    }

    private void stop() {
        //sharedPreference.setBluetoothPlayerState(false);
        play.setImageResource(R.drawable.play);
        gpioUart.sendData("blt-mus-stp?");
        imgDisk.clearAnimation();

        handler.removeCallbacksAndMessages(null);

    }

    private void pause() {
        //sharedPreference.setBluetoothPlayerState(false);
        play.setImageResource(R.drawable.play);
        gpioUart.sendData("blt-mus-ppp?");
        imgDisk.clearAnimation();

    }

    private void pauseWhithOutCMD() {
        //sharedPreference.setBluetoothPlayerState(false);
        play.setImageResource(R.drawable.play);
        imgDisk.clearAnimation();

    }

    private void play() {
        //sharedPreference.setBluetoothPlayerState(true);
        if (audioManager.isMusicPlay()) {
            //sendData("mod-rad?");
            audioManager.pauseHeadUnitMusicPlayer();
        }
        gpioUart.sendData("blt-mus-ppp?");

        //sharedPreference.setBluetoothPlayerState(true);
        setAnimateMusicLogo(imgDisk);
        play = view.findViewById(R.id.img_btn_play);
        play.setImageResource(R.drawable.pause);
        handler.postDelayed(runnable, 100);

    }

    private void playWhithOutCMD() {

        //sharedPreference.setBluetoothPlayerState(true);
        setAnimateMusicLogo(imgDisk);
        play = view.findViewById(R.id.img_btn_play);
        play.setImageResource(R.drawable.pause);

    }

    private void sendData(final String data, int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gpioUart.sendData(data);
            }
        }, delay);
    }

    private void checkVoiceChannel() {
        if (sharedPreference.getRadioIsRun()) {
            gpioUart.sendData(audioValues.androidBTMode());
            sharedPreference.setRadioIsRun(false);
        }
    }


}
