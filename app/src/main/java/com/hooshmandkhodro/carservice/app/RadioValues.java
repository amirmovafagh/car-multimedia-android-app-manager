package com.hooshmandkhodro.carservice.app;

/**
 * this class config the frequency of radio / AM or FM station and radioGain
 * convert this specifications to the String and send this with GpioUart to MCU and
 * will set on radio chip:tef6621
 *
 * @author Amir Movafagh
 * */

public class RadioValues {

    private int mode, soundGain;
    private String frequenceFM,frequenceAM;
    private SharedPreference sharedPreference;

    public RadioValues(SharedPreference sharedPreference) {
        this.sharedPreference = sharedPreference;
        getMode();
        getFrequencyFM();
        getFrequencyAM();
        getSoundGain();
    }


    public String getRadioManualSearchValues(){
        if(mode == 0)
            return "rad-"+mode+"-"+frequenceAM+"-"+soundGain+"?";
        else return "rad-"+mode+"-"+frequenceFM+"-"+soundGain+"?";
    }

    public String getRadioAutoSearchValues(){

        return "rad-"+mode+"-0000-"+soundGain+"?";
    }

    private void getFrequencyFM() {
        float frq = sharedPreference.getRadioFrequencyFM() * 10;
        frequenceFM = String.valueOf(frq);
        frequenceFM = frequenceFM.replaceAll("\\.0", "");

        if (frq < 1000) {
            frequenceFM = "0" + frequenceFM;
        }

    }

    private void getFrequencyAM() {
        int frq = sharedPreference.getRadioFrequencyAM();
        frequenceAM = String.valueOf(frq);


        if (frq < 1000) {
            frequenceAM = "0" + frequenceAM;
        }

    }

    public int getMode() {
        mode = sharedPreference.getRadioMode();
        return mode;
    }

    public int getSoundGain() {
        soundGain = sharedPreference.getRadioSoundGain();
        return soundGain;
    }

    public void setMode(int mode) {
        sharedPreference.setRadioMode(mode);
        getMode();
    }

    public void setSoundGain(int soundGain){

        sharedPreference.setRadioSoundGain(soundGain);
        getSoundGain();
    }

    public void setFrequencyFM (float frequency){
        sharedPreference.setRadioFrequencyFM(frequency);
        getFrequencyFM();
    }
    public void setFrequencyAM (int frequency){
        sharedPreference.setRadioFrequencyAM(frequency);
        getFrequencyAM();
    }

}
