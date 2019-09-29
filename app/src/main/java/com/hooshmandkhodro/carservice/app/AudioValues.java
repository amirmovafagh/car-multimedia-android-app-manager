package com.hooshmandkhodro.carservice.app;

/**
 * this class make a string value for pushing the audio
 * values to sound module PT2313 and set Output volume
 * on all of output 4 channels
 *
 * @author Amir Movafagh
 */

public class AudioValues {

    private SharedPreference sharedPreference;
    private String vol, vlf, vrf, vlr, vrr, base, treble;

    /**
     * AudioValues *constructor*
     * get all of saved volume values from sharedPreference and work on them in other functions
     *
     * @param   sharedPreference
     * */
    public AudioValues(SharedPreference sharedPreference) {
        this.sharedPreference = sharedPreference;
        getVolume();
        getVolumeLeftFront();
        getVolumeRightFront();
        getVolumeLeftRear();
        getVolumeRightRear();
        getBaseValue();
        getTrebleValue();
    }

    /**
     * getAudioValues
     * return a string with all of volume requirments for set on pt2313 module
     * # is Intended value
     *
     * @return String   "aud-##-###-###-###-###-###-###?"
     * */
    public String getAudioValues() {
        return "aud-" + getVolume() + "-" + getVolumeLeftFront() + "-" + getVolumeRightFront() + "-" + getVolumeLeftRear() + "-" + getVolumeRightRear() + "-" + getBaseValue() + "-" + getTrebleValue() + "?";
    }

    public String getMuteValue() {
        return "aud-63?";
    }

    /**
     * getSoundLimitValue *function*
     * define sound module has limitation on output or not
     * and will be using in other classes
     *
     * @return  sharedPreference.getVolumeValue(13)  an int number
     * */
    public int getSoundLimitValue() {
        return sharedPreference.getVolumeValue(13);
    }


    public int getAndroidLastVolume() {
        return sharedPreference.getVolumeValue(12);
    }
    /**
     * getAndroidLastVolume   setAndroidLastVolume
     * set and get last volume on pt2313
     * and will be using in other classes
     *
     * @param   lastVolume  int num for set the last value of volume
     * @return  sharedPreference.getVolumeValue(12)  an int number
     *
     * */
    public void setAndroidLastVolume(int lastVolume) {
        sharedPreference.setVolumeValue(12, lastVolume);
    }

    /**
     * getVolumeValue
     * get total output volume
     * and will using in other classes
     *
     * @return "aud-##?"  type is String
     * */
    public String getVolumeValue() {
        return "aud-" + getVolume() + "?";
    }

    /**
     * setVolume
     * set total output volume
     *
     * @param value will save in sharedPreference
     * */
    public void setVolume(int value) {
        sharedPreference.setVolumeValue(0, value);
    }

    /**
     * getVolume
     * get total output volume as int type and convert to string
     * and control lentgh of that string must be 2.
     *
     * @return "##?"  type is String
     * */
    private String getVolume() {
        int min = (63 - sharedPreference.getVolumeValue(0));
        if (min < 10) {
            vol = 0 + "" + min;
        } else vol = String.valueOf(min);
        return vol;
    }

    public int getVolumeFromPref() {
        return sharedPreference.getVolumeValue(0);
    }

    public void setVolumeLeftFront(int value) {
        sharedPreference.setVolumeValue(1, value);
    }

    private String getVolumeLeftFront() {
        vlf = String.valueOf(223 - sharedPreference.getVolumeValue(1));
        return vlf;
    }

    public void setVolumeRightFront(int value) {
        sharedPreference.setVolumeValue(2, value);
    }

    private String getVolumeRightFront() {
        vrf = String.valueOf(255 - sharedPreference.getVolumeValue(2));
        return vrf;
    }

    public void setVolumeLeftRear(int value) {
        sharedPreference.setVolumeValue(3, value);
    }

    private String getVolumeLeftRear() {
        vlr = String.valueOf(159 - sharedPreference.getVolumeValue(3));
        return vlr;
    }

    public void setVolumeRightRear(int value) {
        sharedPreference.setVolumeValue(4, value);
    }

    private String getVolumeRightRear() {
        vrr = String.valueOf(191 - sharedPreference.getVolumeValue(4));
        return vrr;
    }

    public void setBaseValue(int value) {
        sharedPreference.setVolumeValue(5, value);
    }

    private String getBaseValue() {
        int i = sharedPreference.getVolumeValue(5);
        if (i < 0) {
            if (i < -3)
                base = 0 + "" + (103 + i);
            else
                String.valueOf(103 + i);
        } else
            base = String.valueOf(111 - i);
        return base;
    }

    public void setTrebleValue(int value) {
        sharedPreference.setVolumeValue(6, value);
    }

    private String getTrebleValue() {
        int i = sharedPreference.getVolumeValue(6);
        if (i < 0) {
            treble = String.valueOf(119 + i);
        } else
            treble = String.valueOf(127 - i);
        return treble;
    }

    public void loudState(boolean state) {
        if (state) {
            sharedPreference.setVolumeValue(7, 1);
            modifySoundModeChannel(2, 0);
        } else {
            sharedPreference.setVolumeValue(7, 0);
            modifySoundModeChannel(2, 1);
        }
    }

    public void setGainValue(int value) {
        switch (value) {
            case 0:
                sharedPreference.setVolumeValue(8, value);
                modifySoundModeChannel(3, 1);
                modifySoundModeChannel(4, 1);
                return;
            case 4:
                sharedPreference.setVolumeValue(8, value);
                modifySoundModeChannel(3, 0);
                modifySoundModeChannel(4, 1);
                return;
            case 8:
                sharedPreference.setVolumeValue(8, value);
                modifySoundModeChannel(3, 1);
                modifySoundModeChannel(4, 0);
                return;
            case 12:
                sharedPreference.setVolumeValue(8, value);
                modifySoundModeChannel(3, 0);
                modifySoundModeChannel(4, 0);
                return;
        }
    }

    /**
     * modify aux, androiHeaunit\BT or radio output channels
     * in order to set base, loud, treble and gain settings On every one of the outputs
     *
     * @param position
     * @param bitValue
     * */
    private void modifySoundModeChannel(int position, int bitValue) {

        int aux = modifyBit(sharedPreference.getVolumeValue(9), position, bitValue); //aux
        sharedPreference.setVolumeValue(9, aux);
        int androidBT = modifyBit(sharedPreference.getVolumeValue(11), position, bitValue); //android AND BT
        sharedPreference.setVolumeValue(11, androidBT);
        int radio = modifyBit(sharedPreference.getVolumeValue(10), position, bitValue); //RADIO
        sharedPreference.setVolumeValue(10, radio);
    }

    private int modifyBit(int number, int position, int bitValue) {

        int mask = 1 << position;
        return (number & ~mask) |
                ((bitValue << position) & mask);
    }

    /**
     *these three functions will set output of pt2313
     * */
    public String auxMode() {
        return "mod-" + sharedPreference.getVolumeValue(9) + "?";
    }

    public String androidBTMode() {
        return "mod-" + sharedPreference.getVolumeValue(11) + "?";
    }

    public String radioMode() {
        return "mod-" + sharedPreference.getVolumeValue(10) + "?";
    }

}
