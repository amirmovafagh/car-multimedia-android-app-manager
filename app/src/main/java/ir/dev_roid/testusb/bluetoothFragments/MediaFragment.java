package ir.dev_roid.testusb.bluetoothFragments;

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

import java.util.Timer;

import ir.dev_roid.testusb.R;

import static ir.dev_roid.testusb.BluetoothActivity.connectUsbServiceStatic;
import static ir.dev_roid.testusb.MyHandler.buffer;

import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.MyAudioManager;
import ir.dev_roid.testusb.app.PrefManager;

public class MediaFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView imgDisk;
    private ImageButton previous, next, play, pause, stop, decVolume, incVolume;
    private ConnectUsbService connectUsbService;
    private PrefManager prefManager;
    private MyAudioManager audioManager;
    private Timer timer;
    private Handler handler;
    private Runnable runnable;


    public MediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        connectUsbService = connectUsbServiceStatic;
        prefManager = new PrefManager(getContext());
        audioManager = new MyAudioManager(getContext());
        timer = new Timer();
        handler = new Handler();

        audioManager.pauseHeadUnitMusicPlayer();
        prefManager.setHeadUnitAudioIsActive(false);

        startHandlerMusicPlayerState();
        if (prefManager.getBluetoothPlayerState()) {
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
                } else if(buffer.equalsIgnoreCase("MG1")){
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
                connectUsbService.write("blt-mus-bwd?");
                return;
            case R.id.img_btn_next:
                connectUsbService.write("blt-mus-fwd?");
                return;
            case R.id.img_btn_play:
                if(!prefManager.getBluetoothPlayerState())
                    play();
                else
                    pause();
                return;
            case R.id.img_btn_stop:
                stop();
                return;
            case R.id.img_btn_dec_volume:
                connectUsbService.write("blt-mus-vdn?");
                return;
            case R.id.img_btn_inc_volume:
                connectUsbService.write("blt-mus-vup?");
                return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkVoiceChannel ();
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

        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onResume() {
        super.onResume();
        checkVoiceChannel ();
        handler.postDelayed(runnable,100);

    }

    private void stopWithoutCommand() {
        prefManager.setBluetoothPlayerState(false);
        play.setImageResource(R.drawable.play);

        imgDisk.clearAnimation();

        handler.removeCallbacksAndMessages(null);
    }

    private void stop() {
        prefManager.setBluetoothPlayerState(false);
        play.setImageResource(R.drawable.play);
        connectUsbService.write("blt-mus-stp?");
        imgDisk.clearAnimation();

        handler.removeCallbacksAndMessages(null);

    }

    private void pause() {
        prefManager.setBluetoothPlayerState(false);
        play.setImageResource(R.drawable.play);
        connectUsbService.write("blt-mus-ppp?");
        imgDisk.clearAnimation();

    }

    private void pauseWhithOutCMD() {
        prefManager.setBluetoothPlayerState(false);
        play.setImageResource(R.drawable.play);
        imgDisk.clearAnimation();

    }

    private void play() {
        prefManager.setBluetoothPlayerState(true);
        if (audioManager.isMusicPlay()) {
            //sendData("mod-rad?");
            audioManager.pauseHeadUnitMusicPlayer();
        }
        connectUsbService.write("blt-mus-ppp?");

        prefManager.setBluetoothPlayerState(true);
        setAnimateMusicLogo(imgDisk);
        play = view.findViewById(R.id.img_btn_play);
        play.setImageResource(R.drawable.pause);
        handler.postDelayed(runnable, 100);

    }

    private void playWhithOutCMD() {

        prefManager.setBluetoothPlayerState(true);
        setAnimateMusicLogo(imgDisk);
        play = view.findViewById(R.id.img_btn_play);
        play.setImageResource(R.drawable.pause);

    }

    private void checkVoiceChannel (){
        if (prefManager.getRadioIsRun()){
            connectUsbService.write("mod-?");
            prefManager.setRadioIsRun(false);
        }
    }


}
