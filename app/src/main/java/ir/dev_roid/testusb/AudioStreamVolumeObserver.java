package ir.dev_roid.testusb;


import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;

import java.util.logging.Logger;

import ir.dev_roid.testusb.app.AudioValues;


/**
 * check the increase and decrease state of media volume
 * for now this function not used in program
 */

public class AudioStreamVolumeObserver extends ContentObserver {
    private static final String TAG = AudioStreamVolumeObserver.class.getSimpleName();
    int previousVolume;
    private Context context;
    private AudioValues audioValues;
    private UsbSerialDevice serialPort;
    private AudioManager audio;
    private boolean swcDelay = false;


    public AudioStreamVolumeObserver(Context c, Handler handler, AudioValues audioValues, UsbSerialDevice serialPort) {
        super(handler);
        this.serialPort = serialPort;
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
        //mConnectUsbServiceStatic.write(audioValues.getVolumeValue());
        if (serialPort != null) {
            serialPort.syncWrite(audioValues.getVolumeValue().getBytes(), 0);
            //serialPort.syncWrite(audioValues.getVolumeValue().getBytes(), 300);
            if (swcDelay) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //serialPort.syncWrite(audioValues.getVolumeValue().getBytes(), 1000);

                    }
                }, 700);

            }
            swcDelay = false;

        }
    }

    public void increaseVolumeSWC() {
        int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume + 1, AudioManager.FLAG_PLAY_SOUND);
        showVolumeUI();
    }

    public void decreaseVolumeSWC() {
        int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume - 1, AudioManager.FLAG_PLAY_SOUND);
        showVolumeUI();
    }

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


