package ir.dev_roid.testusb;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.MyAudioManager;
import ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.TelephoneActivity;

import static ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment.PhoneDialerFragment.dialFragmentIsRun;

/**
 * Created by hirad on 3/5/18.
 */

public class MyHandler extends Handler {
    private static final String TAG = MyHandler.class.getSimpleName();
    private String data;
    public static String buffer = " ";
    public static int steeringWheelData ;
    public static boolean steeringWheelDataStatus = false ;
    private MyAudioManager audioManager;

    //private final WeakReference<MainActivity> mActivity;
    private Context context;


    public MyHandler(Context context) {
        this.context= context;

    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case UsbService.MESSAGE_FROM_SERIAL_PORT:
                data = (String) msg.obj;
                //Toast.makeText(context, "1 "+data, Toast.LENGTH_SHORT).show();
                break;
            case UsbService.SYNC_READ:

                buffer = (String) msg.obj;

                Toast.makeText(context, "2 "+buffer, Toast.LENGTH_SHORT).show();
                try{
                    int i = Integer.parseInt(buffer);
                    if(i<3350){
                        steeringWheelDataStatus = true;
                        steeringWheelData = i/10 ;

                        Log.i(TAG, " "+steeringWheelData);
                    }else steeringWheelDataStatus = false;

                }catch (Exception e){
                    Log.i(TAG, " "+e);
                    steeringWheelDataStatus = false;
                }


                checkIncomingCall();
                checkOutgoingCall();
                break;
        }
    }

    private void stopHeadUnitMusic(){
        audioManager = new MyAudioManager(context);
        audioManager.pauseHeadUnitMusicPlayer();

    }

    private void checkIncomingCall(){
        if(buffer.equalsIgnoreCase("MG5") && !dialFragmentIsRun)
        {
            stopHeadUnitMusic();

            Intent intent = new Intent(context,TelephoneActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.i(TAG, "Incoming call");
            return;
        }

    }

    private void checkOutgoingCall(){
        if(buffer.equalsIgnoreCase("MG4") && !dialFragmentIsRun)
        {
            stopHeadUnitMusic();
            Intent intent = new Intent(context,TelephoneActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            Log.i(TAG, "Outgoing call");
            return;
        }

    }


}
