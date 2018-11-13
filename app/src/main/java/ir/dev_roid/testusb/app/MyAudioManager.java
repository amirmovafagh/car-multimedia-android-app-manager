package ir.dev_roid.testusb.app;

import android.content.Context;
import android.media.AudioManager;

public class MyAudioManager {

    private AudioManager audioManager;
    private Context context;

    public MyAudioManager(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean isMusicPlay() {
        if (audioManager.isMusicActive()){
            return true;
        }else return false;
    }

    public boolean pauseHeadUnitMusicPlayer(){
        int result = audioManager.requestAudioFocus(null,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
            // Start playback.
        }return false;
    }
}
