package ir.dev_roid.testusb;


import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
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

    public AudioStreamVolumeObserver(Context c, Handler handler, AudioValues audioValues) {
        super(handler);

        context = c;
        this.audioValues = audioValues;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);


        //write(data.getBytes());
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int delta = previousVolume - currentVolume;
        int moduleVolume = Integer.parseInt(audioValues.getVolumeValue());


        if (delta > 0) {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, 15, AudioManager.FLAG_PLAY_SOUND);
            Log.d(TAG, "Decreased");
            previousVolume = currentVolume;

            if(moduleVolume < 63 && moduleVolume > 0)
                setSoundModuleVolume(moduleVolume-1);
        } else if (delta < 0) {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, 15, AudioManager.FLAG_PLAY_SOUND);
            Log.d(TAG, "Increased");
            previousVolume = currentVolume;

            if(moduleVolume < 63 && moduleVolume > 0)
                setSoundModuleVolume(moduleVolume+1);
        }

    }

    private void setSoundModuleVolume(int volume) {
        audioValues.setVolume(volume);
        //mConnectUsbServiceStatic.write(audioValues.getVolumeValue());
    }
}


