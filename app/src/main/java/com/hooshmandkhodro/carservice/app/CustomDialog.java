package com.hooshmandkhodro.carservice.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.hooshmandkhodro.carservice.R;

/**
 * build a customDialog in every activity for control volume And brightness
 *
 * @author Amir Movafagh
 * */

public class CustomDialog {

    private Activity activity;
    private ImageButton mute;
    private AudioManager audioManager;
    private GpioUart gpioUart;
    private PrefManager pref;
    private Brightness brightness;
    private int frqSeekVal;
    private AudioValues audioValues;

    public CustomDialog(Activity activity, PrefManager prefManager, GpioUart gpioUart) {
        this.activity = activity;
        this.gpioUart = gpioUart;
        this.pref = prefManager;
        audioValues = new AudioValues(pref);
    }


    public void show() {

        Dialog dialog = new Dialog(activity, R.style.PauseDialog);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mute = dialog.findViewById(R.id.ib_mute_cDialog);
        SeekBar carVolumeSeek = dialog.findViewById(R.id.seekbar_car_volume);
        SeekBar osVolumeSeek = dialog.findViewById(R.id.seekbar_os_volume);
        SeekBar brightnessSeek = dialog.findViewById(R.id.seekbar_brightness);
        brightnessSeek.setMax(255);
        osVolumeSeek.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        osVolumeSeek.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        carVolumeSeek.setMax(audioValues.getSoundLimitValue());
        carVolumeSeek.setProgress(pref.getVolumeValue(0));

        brightness = new Brightness(activity);
        brightnessSeek.setProgress(brightness.getScreenBrightness());

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
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
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
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {

                audioValues.setVolume(progress);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        gpioUart.sendData(audioValues.getVolumeValue());
                    }
                },10);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        dialog.show();

        brightnessSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brightness.setScreenBrightness(i);
                gpioUart.sendData("oth-brg-" + i / 5 + "?");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


}
