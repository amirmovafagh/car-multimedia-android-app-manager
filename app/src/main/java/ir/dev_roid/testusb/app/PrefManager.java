package ir.dev_roid.testusb.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static ir.dev_roid.testusb.SettingsActivity.defaultX;
import static ir.dev_roid.testusb.SettingsActivity.defaultY;

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

    private static final String[] KEY_FAV_RADIO_BTNs = {"btn1fm", "btn2fm", "btn3fm", "btn4fm", "btn5fm", "btn6fm",
            "btn1am", "btn2am", "btn3am", "btn4am", "btn5am", "btn6am"};
    private static final String[] FAV_RADIO_DEFAULT_STATION = {"88.0", "90.0", "92.0", "94.5", "96.0", "106.1",
            "558", "585", "1188", "1035", "1296", "1332"};
    private static final String IS_RUN_RADIO = "runRadio";

    private static final String KEY_RADIO_FRQ_FM = "radioFrequencyFM";
    private static final String KEY_RADIO_FRQ_AM = "radioFrequencyAM";
    private static final Float frqFM = (float) 98.0;
    private static final int frqAM = 1000;

    private static final String[] KEY_VOLUME_VALUES = {"mainVal", "lfVal", "rfVal", "lrVal", "rrVal",
            "bassVal", "trebleVal", "loudVal","gainVal", "auxMode", "androidMode", "radioMode","lastMainVal","soundLimit"};
    private static final int[] values = {48, 30, 30, 30, 30, 0, 0, 0,0,92,93, 94,14,55};

    private static final String KEY_X_CORDINATE = "xCordinate";
    private static final String KEY_Y_CORDINATE = "yCordinate";

    private static final String BRIGHTNESS_VALUE = "brightnessValue";
    private static final String DEBUG_MODE = "debugMode";
    private static final String IS_PLAY_BLUETOOTH = "isPlayBluetoothPlayer";
    private static final String HEAD_UNIT_AUDIO_IS_ACTIVE = "huAudioIsActive";
    private static final String AUX_AUDIO_IS_ACTIVE = "auxAudioIsActive";

    private static final String RADIO_MODE_CHANNEL = "radioModeChannel";
    private static final String RADIO_SOUND_GAIN = "radioSoundGain";
    private static final String AMPLIFIRE_STATE ="ampliFire";

    public PrefManager(Context context) {

        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setRadioFrequencyFM(float frequenceNumber) {

        editor.putFloat(KEY_RADIO_FRQ_FM, frequenceNumber);
        // commit changes
        editor.commit();
        Log.d(TAG, "set  Radio Frequency FM" + frequenceNumber + " MHz ");
    }


    public float getRadioFrequencyFM() {

        return pref.getFloat(KEY_RADIO_FRQ_FM, frqFM);
    }

    public void setRadioFrequencyAM (int frequenceNumber) {

        editor.putInt(KEY_RADIO_FRQ_AM, frequenceNumber);
        // commit changes
        editor.commit();
        Log.d(TAG, "set  Radio Frequency AM" + frequenceNumber + " MHz ");
    }


    public int getRadioFrequencyAM() {

        return pref.getInt(KEY_RADIO_FRQ_AM, frqAM);
    }

    public void setFavoriteRadioFrequency(int i, String frequenceNumber) {

        editor.putString(KEY_FAV_RADIO_BTNs[i], frequenceNumber);

        // commit changes
        editor.commit();

        Log.d(TAG, "set Favorite Radio Frequency " + frequenceNumber + " Hz to button nubmber" + i);
    }

    public String getFavoriteRadioFrequency(int i) {
        return pref.getString(KEY_FAV_RADIO_BTNs[i], FAV_RADIO_DEFAULT_STATION[i]);
    }

    public void setRadioIsRun(boolean value) {
        editor.putBoolean(IS_RUN_RADIO, value);
        editor.commit();
        //Log.d(TAG, "set Value " + value + " to : " + IS_RUN_RADIO);
    }

    public void setRadioSoundGain(int value) {
        editor.putInt(RADIO_SOUND_GAIN, value);
        editor.commit();

        Log.d(TAG, "RADIO_SOUND_GAIN : " + value);
    }

    public int getRadioSoundGain() {
        return pref.getInt(RADIO_SOUND_GAIN, 0);
    }

    public void setRadioMode(int value) {
        editor.putInt(RADIO_MODE_CHANNEL, value);
        editor.commit();

        Log.d(TAG, "RADIO_MODE : " + value);
    }

    public int getRadioMode() {
        return pref.getInt(RADIO_MODE_CHANNEL, 1);
    } //1 FM ,0 AM

    public boolean getRadioIsRun(){
        return pref.getBoolean(IS_RUN_RADIO, false);
    }

    public void setVolumeValue(int i, int value) {
        editor.putInt(KEY_VOLUME_VALUES[i], value);

        editor.commit();
        Log.d(TAG, "set Value " + value + " to : " + KEY_VOLUME_VALUES[i]);
    }

    public int getVolumeValue(int i) {

        return pref.getInt(KEY_VOLUME_VALUES[i], values[i]);
    }

    public void setXYcordinates(Float x, Float y) {

        editor.putFloat(KEY_X_CORDINATE, x);
        editor.commit();
        editor.putFloat(KEY_Y_CORDINATE, y);
        // commit changes
        editor.commit();
        Log.d(TAG, "set  X :" + x + "& Y :" + y);
    }

    public Float getXcordinate() {

        return pref.getFloat(KEY_X_CORDINATE, defaultX);
    }

    public Float getYcordinate() {

        return pref.getFloat(KEY_Y_CORDINATE, defaultY);
    }

    public void setBrightnessValue(int value) {
        editor.putInt(BRIGHTNESS_VALUE, value);
        editor.commit();

        Log.d(TAG, "BRIGHTNESS_VALUE : " + value);
    }

    public int getBrightnessValue() {
        return pref.getInt(BRIGHTNESS_VALUE, 255);
    }

    public void setBluetoothPlayerState(boolean value) {
        editor.putBoolean(IS_PLAY_BLUETOOTH, value);
        editor.commit();

        //Log.d(TAG, "IsPlayState : "+b);
    }

    public boolean getBluetoothPlayerState() {
        return pref.getBoolean(IS_PLAY_BLUETOOTH, false);
    }

    public void setHeadUnitAudioIsActive(boolean b) {
        editor.putBoolean(HEAD_UNIT_AUDIO_IS_ACTIVE, b);
        editor.commit();

        //Log.d(TAG, "HeadUnitAudioIsActive : "+b);
    }

    public boolean getHeadUnitAudioIsActive() {
        return pref.getBoolean(HEAD_UNIT_AUDIO_IS_ACTIVE, false);
    }

    public void setAUXAudioIsActive(boolean b) {
        editor.putBoolean(AUX_AUDIO_IS_ACTIVE, b);
        editor.commit();

        //Log.d(TAG, "AUXAudioIsActive : "+b);
    }

    public boolean getAUXAudioIsActive() {
        return pref.getBoolean(AUX_AUDIO_IS_ACTIVE, false);
    }

    public void setAmplifireState(boolean value) {
        editor.putBoolean(AMPLIFIRE_STATE, value);
        editor.commit();

        //Log.d(TAG, "AMPLIFIRE_STATE : "+b);
    }

    public boolean getAmplifireState() {
        return pref.getBoolean(AMPLIFIRE_STATE, false);
    }

    public void setDebugModeState(boolean value) {
        editor.putBoolean(DEBUG_MODE, value);
        editor.commit();

        //Log.d(TAG, "DEBUG_MODE : "+b);
    }

    public boolean getDebugModeState() {
        return pref.getBoolean(DEBUG_MODE, false);
    }


}
