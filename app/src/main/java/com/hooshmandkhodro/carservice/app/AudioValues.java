package com.hooshmandkhodro.carservice.app;

import com.hooshmandkhodro.carservice.app.dagger.App;

/**
 * this class make a string value for pushing the audio
 * values to sound module PT2313 and set Output volume
 * on all of output 4 channels
 *
 * @author Amir Movafagh
 */

public class AudioValues {

    private PrefManager prefManager;
    private String vol, vlf, vrf, vlr, vrr, base, treble;

    /**
     * AudioValues *constructor*
     * get all of saved volume values from prefManager and work on them in other functions
     *
     * @param   prefManager
     * */
    public AudioValues(PrefManager prefManager) {
        this.prefManager = prefManager;
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
     * @return  prefManager.getVolumeValue(13)  an int number
     * */
    public int getSoundLimitValue() {
        return prefManager.getVolumeValue(13);
    }


    public int getAndroidLastVolume() {
        return prefManager.getVolumeValue(12);
    }
    /**
     * getAndroidLastVolume   setAndroidLastVolume
     * set and get last volume on pt2313
     * and will be using in other classes
     *
     * @param   lastVolume  int num for set the last value of volume
     * @return  prefManager.getVolumeValue(12)  an int number
     *
     * */
    public void setAndroidLastVolume(int lastVolume) {
        prefManager.setVolumeValue(12, lastVolume);
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
     * @param value will save in prefManager
     * */
    public void setVolume(int value) {
        prefManager.setVolumeValue(0, value);
    }

    /**
     * getVolume
     * get total output volume as int type and convert to string
     * and control lentgh of that string must be 2.
     *
     * @return "##?"  type is String
     * */
    private String getVolume() {
        int min = (63 - prefManager.getVolumeValue(0));
        if (min < 10) {
            vol = 0 + "" + min;
        } else vol = String.valueOf(min);
        return vol;
    }

    public int getVolumeFromPref() {
        return prefManager.getVolumeValue(0);
    }

    public void setVolumeLeftFront(int value) {
        prefManager.setVolumeValue(1, value);
    }

    private String getVolumeLeftFront() {
        vlf = String.valueOf(223 - prefManager.getVolumeValue(1));
        return vlf;
    }

    public void setVolumeRightFront(int value) {
        prefManager.setVolumeValue(2, value);
    }

    private String getVolumeRightFront() {
        vrf = String.valueOf(255 - prefManager.getVolumeValue(2));
        return vrf;
    }

    public void setVolumeLeftRear(int value) {
        prefManager.setVolumeValue(3, value);
    }

    private String getVolumeLeftRear() {
        vlr = String.valueOf(159 - prefManager.getVolumeValue(3));
        return vlr;
    }

    public void setVolumeRightRear(int value) {
        prefManager.setVolumeValue(4, value);
    }

    private String getVolumeRightRear() {
        vrr = String.valueOf(191 - prefManager.getVolumeValue(4));
        return vrr;
    }

    public void setBaseValue(int value) {
        prefManager.setVolumeValue(5, value);
    }

    private String getBaseValue() {
        int i = prefManager.getVolumeValue(5);
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
        prefManager.setVolumeValue(6, value);
    }

    private String getTrebleValue() {
        int i = prefManager.getVolumeValue(6);
        if (i < 0) {
            treble = String.valueOf(119 + i);
        } else
            treble = String.valueOf(127 - i);
        return treble;
    }

    public void loudState(boolean state) {
        if (state) {
            prefManager.setVolumeValue(7, 1);
            modifySoundModeChannel(2, 0);
        } else {
            prefManager.setVolumeValue(7, 0);
            modifySoundModeChannel(2, 1);
        }
    }

    public void setGainValue(int value) {
        switch (value) {
            case 0:
                prefManager.setVolumeValue(8, value);
                modifySoundModeChannel(3, 1);
                modifySoundModeChannel(4, 1);
                return;
            case 4:
                prefManager.setVolumeValue(8, value);
                modifySoundModeChannel(3, 0);
                modifySoundModeChannel(4, 1);
                return;
            case 8:
                prefManager.setVolumeValue(8, value);
                modifySoundModeChannel(3, 1);
                modifySoundModeChannel(4, 0);
                return;
            case 12:
                prefManager.setVolumeValue(8, value);
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

        int aux = modifyBit(prefManager.getVolumeValue(9), position, bitValue); //aux
        prefManager.setVolumeValue(9, aux);
        int androidBT = modifyBit(prefManager.getVolumeValue(11), position, bitValue); //android AND BT
        prefManager.setVolumeValue(11, androidBT);
        int radio = modifyBit(prefManager.getVolumeValue(10), position, bitValue); //RADIO
        prefManager.setVolumeValue(10, radio);
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
        return "mod-" + prefManager.getVolumeValue(9) + "?";
    }

    public String androidBTMode() {
        return "mod-" + prefManager.getVolumeValue(11) + "?";
    }

    public String radioMode() {
        return "mod-" + prefManager.getVolumeValue(10) + "?";
    }

}
