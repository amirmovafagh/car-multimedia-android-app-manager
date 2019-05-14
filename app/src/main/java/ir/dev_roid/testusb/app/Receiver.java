package ir.dev_roid.testusb.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import ir.dev_roid.testusb.AudioStreamVolumeObserver;
import ir.dev_roid.testusb.UsbService;

/**
 * Created by hirad on 2/28/18.
 */

public class Receiver extends BroadcastReceiver {
    private static final String TAG = AudioStreamVolumeObserver.class.getSimpleName();
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON";
    private static final String USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

    private CpuManager cpuManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        //boot device do this method
        if (intent.getAction().equals(BOOT_COMPLETED) || intent.getAction().equals(QUICKBOOT_POWERON)) {
            context.startService(new Intent(context, UsbService.class));
            Toast.makeText(context, "سرویس مولتی مدیا راه اندازی شد", Toast.LENGTH_SHORT).show();
            cpuManager = new CpuManager();
            cpuManager.cpuMinFrequency(480000);
            cpuManager.cpuMaxFrequency(816000);
            cpuManager.cpuGoverner("interactive");

            /*//******go to main screen  do not need for now
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);*/
        }


        if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            //Toast.makeText(context, "vol", Toast.LENGTH_SHORT).show();
        }
        /*when connected USb to device check that is target usb or not
        if (intent.getAction().equals(USB_DEVICE_ATTACHED)) {
        }*/
    }
}


