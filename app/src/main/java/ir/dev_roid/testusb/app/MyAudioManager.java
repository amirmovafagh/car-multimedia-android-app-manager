package ir.dev_roid.testusb.app;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.view.KeyEvent;

public class MyAudioManager {

    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String CMDPLAY = "play";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDSTOP = "stop";

    long eventtime ;
    private AudioManager audioManager;
    private Context context;

    public MyAudioManager(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        eventtime= SystemClock.uptimeMillis();
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
    public void nextHeadUnitMusicPlayer(){
        if (isMusicPlay()){
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME , CMDNEXT );
            context.sendBroadcast(i);
        }
    }

    public void previousHeadUnitMusicPlayer(){
        if (isMusicPlay()){
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME , CMDPREVIOUS );
            context.sendBroadcast(i);
        }
    }

    public void playHeadUnitMusicPlayer(){
        if (!isMusicPlay()){
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME , CMDPLAY );
            context.sendBroadcast(i);
        }
    }

}
