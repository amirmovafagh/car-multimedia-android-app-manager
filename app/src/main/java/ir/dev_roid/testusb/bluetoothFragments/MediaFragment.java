package ir.dev_roid.testusb.bluetoothFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import ir.dev_roid.testusb.R;
import static ir.dev_roid.testusb.MyHandler.buffer;
import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.PrefManager;

public class MediaFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView imgDisk;
    private ImageButton previous, next, play, pause, stop, decVolume, incVolume;
    private ConnectUsbService connectUsbService;
    private PrefManager prefManager;


    public MediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        connectUsbService = new ConnectUsbService(getActivity());
        prefManager = new PrefManager(getContext());

        startHandlerMusicPlayerState();


    }

    private void startHandlerMusicPlayerState() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(buffer.equalsIgnoreCase("MB") || buffer.equalsIgnoreCase("MR")){
                    playWhithOutCMD();
                }else if(buffer.equalsIgnoreCase("MP")){
                    pauseWhithOutCMD();
                }else if(buffer.equalsIgnoreCase("MA")){
                    stop();
                }

                handler.postDelayed(this, 100);
            }
        },100);
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
                sendData("blt-mus-bwd?");
                return;
            case R.id.img_btn_next:
                sendData("blt-mus-fwd?");
                return;
            case R.id.img_btn_play:
                if (!prefManager.getIsplayState()) {
                    play();
                } else {
                    pause();
                }
                return;
            case R.id.img_btn_stop:
                stop();
                return;
            case R.id.img_btn_dec_volume:
                sendData("blt-mus-vdn?");
                return;
            case R.id.img_btn_inc_volume:
                sendData("blt-mus-vup?");
                return;
        }
    }

    private void stop() {
        play.setImageResource(R.drawable.play);
        sendData("blt-mus-stp?");
        imgDisk.clearAnimation();
        prefManager.setIsPlayState(false);
    }

    private void pause() {
        prefManager.setIsPlayState(false);
        play.setImageResource(R.drawable.play);
        sendData("blt-mus-ppp?");
        imgDisk.clearAnimation();
    }

    private void pauseWhithOutCMD() {
        prefManager.setIsPlayState(false);
        play.setImageResource(R.drawable.play);
        imgDisk.clearAnimation();
    }

    private void play() {
        prefManager.setIsPlayState(true);
        setAnimateMusicLogo(imgDisk);
        play = view.findViewById(R.id.img_btn_play);
        play.setImageResource(R.drawable.pause);
        sendData("blt-mus-ppp?");
    }

    private void playWhithOutCMD() {
        prefManager.setIsPlayState(true);
        setAnimateMusicLogo(imgDisk);
        play = view.findViewById(R.id.img_btn_play);
        play.setImageResource(R.drawable.pause);
    }

    private void sendData(String data) {
        connectUsbService.write(data);
    }


}
