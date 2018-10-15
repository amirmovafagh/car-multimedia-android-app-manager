package ir.dev_roid.testusb.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;

import ir.dev_roid.testusb.R;

public class CustomDialog extends Activity {

    private Activity activity;
    private ImageButton mute;
    private AudioManager audioManager;
    private ConnectUsbService connectUsbService;
    private PrefManager pref;
    private int frqSeekVal;

    public CustomDialog (Activity activity){
        this.activity=activity;
        connectUsbService = new ConnectUsbService(activity);
        pref= new PrefManager(activity);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void show (){
        Dialog dialog = new Dialog(activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mute = (ImageButton) dialog.findViewById(R.id.ib_mute_cDialog);
        SeekBar carVolumeSeek = (SeekBar) dialog.findViewById(R.id.seekbar_car_volume);
        SeekBar osVolumeSeek = (SeekBar) dialog.findViewById(R.id.seekbar_os_volume);
        osVolumeSeek.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        osVolumeSeek.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        carVolumeSeek.setMax(63);
        carVolumeSeek.setProgress(pref.getVolumeValue(0));



        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //------------------------------------------------------------------------------------------
        osVolumeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Toast.makeText(activity, ""+progress, Toast.LENGTH_SHORT).show();
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //------------------------------------------------------------------------------------------

        carVolumeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frqSeekVal=progress;
                connectUsbService.write("aud-vol-"+(63-progress)+"?");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pref.setVolumeValue(0,frqSeekVal);
            }
        });
        dialog.show();

    }

}
