package ir.dev_roid.testusb.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Set;

import ir.dev_roid.testusb.UsbService;

import static ir.dev_roid.testusb.UsbService.threadStatus;

public class ConnectUsbService {
    private static final String tag = ConnectUsbService.class.getSimpleName();
    private Activity activity;
    private ServiceConnection sConnection;


    private UsbService usbService;

    public ConnectUsbService(Activity activity) {
        this.activity = activity;
        startService(UsbService.class, usbConnection, null);
    }


    public void write(String data) {
        if (usbService != null) { // if UsbService was correctly binded, Send data
            usbService.write(data.getBytes());
        }
    }

    public void enableCheckCallStatus() {
        usbService.enableCheckCallStatus();
        threadStatus = true;
        Log.i(tag,"enableCheckCallStatus");
    }
    public void changeDelay(int delay) {
        usbService.changeHandlerDelay(delay);
    }

    public void disableCheckCallStatus() {
        usbService.disableCheckCallStatus();
        threadStatus = false;
        Log.i(tag,"disableCheckCallStatus");
    }


    public final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    public void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(activity, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            activity.startService(startService);

        }
        Intent bindingIntent = new Intent(activity, service);

        activity.bindService(bindingIntent, serviceConnection, activity.BIND_AUTO_CREATE);
        sConnection = serviceConnection;

    }

    public ServiceConnection onDestroyUsb(){
        return sConnection;
    }

}
