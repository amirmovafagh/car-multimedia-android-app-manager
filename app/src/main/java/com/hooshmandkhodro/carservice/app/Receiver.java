package com.hooshmandkhodro.carservice.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;


import com.hooshmandkhodro.carservice.AudioStreamVolumeObserver;
import com.hooshmandkhodro.carservice.UsbService;
import com.hooshmandkhodro.carservice.app.dagger.App;

import javax.inject.Inject;


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

    @Inject
    GpioUart gpioUart;
    @Inject
    PrefManager prefManager;
    /*@Inject
    Application application;*/
    AudioValues audioValues;
    ArmRTC armRTC;

    private CpuManager cpuManager;

    public Receiver() {



    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ((App)context.getApplicationContext()).getComponent().inject(this);
        //boot device do this method
        if (intent.getAction().equals(BOOT_COMPLETED) || intent.getAction().equals(QUICKBOOT_POWERON)) {
            armRTC = new ArmRTC(gpioUart);

            audioValues   = new AudioValues(prefManager);
            context.startService(new Intent(context, UsbService.class));
            Toast.makeText(context, "سرویس مولتی مدیا راه اندازی شد", Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            i.setClassName("com.hooshmandkhodro.carservice", "com.hooshmandkhodro.carservice.MainActivity");
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


