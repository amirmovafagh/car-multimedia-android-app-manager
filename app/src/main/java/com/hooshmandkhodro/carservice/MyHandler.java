package com.hooshmandkhodro.carservice;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.hooshmandkhodro.carservice.app.MyAudioManager;
import com.hooshmandkhodro.carservice.app.PrefManager;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment.PhoneDialerFragment;

/**
 * Created by hirad on 3/5/18.
 */

public class MyHandler extends Handler {
    private static final String TAG = MyHandler.class.getSimpleName();
    private String data;
    public static String buffer = " ";
    public static int steeringWheelData;
    public static int touchBtnPanelData = 999;
    public static boolean telephoneActivityIsOpen = false;
    public static boolean steeringWheelDataStatus = false;
    private MyAudioManager audioManager;
    private PrefManager prefManager;

    //private final WeakReference<MainActivity> mActivity;
    private Context context;


    public MyHandler(Context context, PrefManager prefManager) {
        this.context = context;
        this.prefManager = prefManager;
    }

    /**
     * handle the incoming serial data from background Service
     *
     * @param msg get from service
     */
    @Override
    public void handleMessage(Message msg) {
        boolean dbug = prefManager.getDebugModeState();
        switch (msg.what) {

            case UsbService.MESSAGE_FROM_GPIO_UART_TTYS1:


                try {
                    buffer = msg.obj.toString().trim();
                    Log.i(TAG, " " + buffer);
                    if (dbug) {
                        Toast.makeText(context, "2 " + buffer, Toast.LENGTH_SHORT).show();
                    }

                    if(buffer != null && buffer.contains("swc-")){
                        int i = Integer.parseInt(buffer.substring(4,8));
                        if (i < 3600) { /*control wheel data value check and convert*/
                            steeringWheelDataStatus = true;
                            steeringWheelData = i / 10;

                            Log.i(TAG, "SW " + steeringWheelData);
                        } else {steeringWheelDataStatus = false;}
                    }


                    if( buffer != null && buffer.contains("tbn-")){
                        Log.i(TAG, " in tb");
                        touchBtnPanelData= Integer.parseInt(buffer.substring(4,5));
                        //buffer = "---";
                    }

                } catch (Exception e) {
                    //Log.i(TAG, " "+e);
                    steeringWheelDataStatus = false;
                }


                checkIncomingCall();
                checkOutgoingCall();
                break;
        }
    }

    private void stopHeadUnitMusic() {
        audioManager = new MyAudioManager(context);
        audioManager.pauseHeadUnitMusicPlayer();

    }

    /*check if there is incoming call from bluetooth */
    private void checkIncomingCall() {
        if (buffer.equalsIgnoreCase("MG5") && !PhoneDialerFragment.dialFragmentIsRun) {
            stopHeadUnitMusic();

            Intent intent = new Intent(context, TelephoneActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.i(TAG, "Incoming call");
            return;
        }

    }

    /*check if there is outgoing call from bluetooth */
    private void checkOutgoingCall() {
        if (buffer.equalsIgnoreCase("MG4") && !PhoneDialerFragment.dialFragmentIsRun) {
            stopHeadUnitMusic();
            Intent intent = new Intent(context, TelephoneActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            Log.i(TAG, "Outgoing call");
            return;
        }

    }


}
