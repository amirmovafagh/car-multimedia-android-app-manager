package com.hooshmandkhodro.carservice.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;


import com.hooshmandkhodro.carservice.AudioStreamVolumeObserver;
import com.hooshmandkhodro.carservice.UsbService;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by hirad on 2/28/18.
 */

public class Receiver extends BroadcastReceiver {
    private static final String TAG = AudioStreamVolumeObserver.class.getSimpleName();
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON";
    private static final String TIME_SET = "android.intent.action.TIME_SET";
    private static final String USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static boolean timeWasSet = false;
    GpioUart gpioUart;
    PrefManager prefManager;
    AudioValues audioValues;
    ArmRTC armRTC;

    private CpuManager cpuManager;

    public Receiver() {



    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //boot device do this method
        if (intent.getAction().equals(BOOT_COMPLETED) || intent.getAction().equals(QUICKBOOT_POWERON)) {
            gpioUart = new GpioUart(1);
            armRTC = new ArmRTC(gpioUart);
            prefManager = new PrefManager(context);
            audioValues   = new AudioValues(prefManager);
            context.startService(new Intent(context, UsbService.class));
            Toast.makeText(context, "سرویس مولتی مدیا راه اندازی شد", Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            i.setClassName("com.hooshmandkhodro.carservice", "MainActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, 9999999);

            gpioUart.sendData(audioValues.androidBTMode());
            prefManager.setHeadUnitAudioIsActive(true);
            prefManager.setAUXAudioIsActive(false);
            prefManager.setRadioIsRun(false);


            /*cpuManager = new CpuManager();
            cpuManager.cpuMinFrequency(480000);
            cpuManager.cpuMaxFrequency(816000);
            cpuManager.cpuGoverner("interactive");*/

            /*//******go to main screen  do not need for now
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);*/
        }

        if (intent.getAction().equals(TIME_SET) && !timeWasSet) {

            gpioUart = new GpioUart(1);
            armRTC = new ArmRTC(gpioUart);
            armRTC.setTimeOnARM();
        }


        if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            //Toast.makeText(context, "vol", Toast.LENGTH_SHORT).show();
        }
        /*when connected USb to device check that is target usb or not
        if (intent.getAction().equals(USB_DEVICE_ATTACHED)) {
        }*/
    }
}


