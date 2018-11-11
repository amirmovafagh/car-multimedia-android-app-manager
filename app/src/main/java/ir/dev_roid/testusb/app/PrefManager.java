package ir.dev_roid.testusb.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by hirad on 4/4/18.
 */

public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "PreferenceManager";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String[] KEY_FAV_RADIO_BTNs = {"btn1","btn2","btn3","btn4","btn5",
    "btn6"};
    private static final String KEY_RADIO_FRQ="radioFrequency";
    private static final Float frq = (float) 98.0;

    private static final String[] KEY_VOLUME_VALUES = {"mainVal","lfVal","rfVal","lrVal","rrVal",
            "bassVal","trebleVal", "loudVal"};
    private static final int[] values={31,16,16,16,16,15,8,3};

    private static final String KEY_X_CORDINATE="xCordinate";
    private static final String KEY_Y_CORDINATE="yCordinate";

    private static final String BRIGHTNESS_VALUE = "brightnessValue";

    private static final String IS_PLAY = "isPlay";


    public PrefManager(Context context) {

        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setRadioFrequency(float frequenceNumber) {

        editor.putFloat(KEY_RADIO_FRQ, frequenceNumber);
        // commit changes
        editor.commit();
        Log.d(TAG, "set  Radio Frequency "+frequenceNumber+" Hz ");
    }


    public float getRadioFrequency() {

        return pref.getFloat(KEY_RADIO_FRQ,frq);
    }

    public void setFavoriteRadioFrequency(int i ,String frequenceNumber) {

        editor.putString(KEY_FAV_RADIO_BTNs[i], frequenceNumber);

        // commit changes
        editor.commit();

        Log.d(TAG, "set Favorite Radio Frequency "+frequenceNumber+" Hz to button nubmber"+i);
    }

    public String getFavoriteRadioFrequency(int i){
        return pref.getString(KEY_FAV_RADIO_BTNs[i],"SAVE FREQUENCY");
    }

    public void setVolumeValue (int i, int value){
        editor.putInt(KEY_VOLUME_VALUES[i],value);

        editor.commit();
        Log.d(TAG, "set Value "+value+" to :"+KEY_VOLUME_VALUES[i]);
    }

    public int getVolumeValue (int i){

        return pref.getInt(KEY_VOLUME_VALUES[i],values[i]);
    }

    public void setXYcordinates(Float x, Float y) {

        editor.putFloat(KEY_X_CORDINATE, x);
        editor.commit();
        editor.putFloat(KEY_Y_CORDINATE, y);
        // commit changes
        editor.commit();
        Log.d(TAG, "set  X :"+x+"& Y :"+y);
    }

    public Float getXcordinate (){

        return pref.getFloat(KEY_X_CORDINATE,  220);
    }

    public Float getYcordinate (){

        return pref.getFloat(KEY_Y_CORDINATE,  222);
    }

    public void setBrightnessValue(int value){
        editor.putInt(BRIGHTNESS_VALUE, value);
        editor.commit();

        Log.d(TAG, "BRIGHTNESS_VALUE : "+value);
    }

    public int getBrightnessValue(){
        return pref.getInt(BRIGHTNESS_VALUE,  255);
    }

    public void setIsPlayState(boolean b){
        editor.putBoolean(IS_PLAY, b);
        editor.commit();

        Log.d(TAG, "IsPlayState : "+b);
    }

    public boolean getIsplayState(){
        return pref.getBoolean(IS_PLAY,  false);
    }
}
