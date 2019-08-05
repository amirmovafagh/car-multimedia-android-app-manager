package com.hooshmandkhodro.carservice;


import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;

import com.hooshmandkhodro.carservice.app.AudioValues;
import com.hooshmandkhodro.carservice.app.GpioUart;


/**
 * check the increase and decrease state of media volume
 *
 * @author Amir Movafagh
 */

public class AudioStreamVolumeObserver extends ContentObserver {
    private static final String TAG = AudioStreamVolumeObserver.class.getSimpleName();
    int previousVolume;
    private Context context;
    private AudioValues audioValues;
    private GpioUart gpioUart;
    private AudioManager audio;
    private boolean swcDelay = false;

    /**
     * build Audio manager and get previos State of volume
     *
     * @param c             context from destination class
     * @param handler       handler for Content observer
     * @param audioValues   call sound values from audioValues Class
     * @param gpioUart      using for send sound details to MCU
     * */
    public AudioStreamVolumeObserver(Context c, Handler handler, AudioValues audioValues, GpioUart gpioUart) {
        super(handler);
        this.gpioUart = gpioUart;
        context = c;
        this.audioValues = audioValues;

        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        //audioValues.setAndroidLastVolume((int) currentVolume);
        //int delta = previousVolume - currentVolume;
        int pt23Volume = audioValues.getVolumeFromPref();


        float vol = (currentVolume / 15) * audioValues.getSoundLimitValue();
        //Log.i(TAG, "currentVolume :" + currentVolume + " , ptVolume :" + pt23Volume + " , vol :" + vol);
        setSoundModuleVolume(Math.round(vol));

        /*if (delta > 0) {
            //audio.setStreamVolume(AudioManager.STREAM_MUSIC, 15, AudioManager.FLAG_PLAY_SOUND);
            Log.d(TAG, "Decreased");
            previousVolume = currentVolume;

            if (moduleVolume < 56 && moduleVolume >= 3) {
                setSoundModuleVolume(moduleVolume - 3);
                Log.i(TAG, "  ptVolume :" + moduleVolume);
            }
        } else if (delta < 0) {
            //audio.setStreamVolume(AudioManager.STREAM_MUSIC, 15, AudioManager.FLAG_PLAY_SOUND);
            Log.d(TAG, "Increased");
            previousVolume = currentVolume;

            if (moduleVolume <= 52 && moduleVolume >= 0) {
                setSoundModuleVolume(moduleVolume + 3);
                Log.i(TAG, "  ptVolume :" + moduleVolume);
            }
        }*/

    }

    private void setSoundModuleVolume(int volume) {
        audioValues.setVolume(volume);

        if (gpioUart != null) {
            gpioUart.sendData(audioValues.getVolumeValue());

            /*if (swcDelay) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    gpioUart.sendData(audioValues.getVolumeValue());
                    Log.i(TAG,"deldel2");
                }, 600);

            }*/
            swcDelay = false;

        }
    }

    /*
    * increase volume with steering wheel key*/
    public void increaseVolumeSWC() {
        int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume + 1, AudioManager.FLAG_PLAY_SOUND);
        showVolumeUI();
    }
    /*
     * decrease volume with steering wheel key*/
    public void decreaseVolumeSWC() {
        int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume - 1, AudioManager.FLAG_PLAY_SOUND);
        showVolumeUI();
    }

     /* mute volume with steering wheel key*/
    public void muteSWC() {
        int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (volume == 0) {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, audioValues.getAndroidLastVolume(), AudioManager.FLAG_PLAY_SOUND);
        } else {
            audioValues.setAndroidLastVolume(volume);
            audioValues.setVolume(0);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
        }

        showVolumeUI();


    }

    private void showVolumeUI() {
        if (audio != null)
            audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
    }

    public void swcDelayOn() {
        swcDelay = true;
    }
}