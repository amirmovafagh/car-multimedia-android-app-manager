package com.hooshmandkhodro.carservice.app;

import android.app.Activity;
import android.util.Log;

import com.hooshmandkhodro.carservice.UsbService;

import static com.hooshmandkhodro.carservice.UsbService.threadStatus;

public class ConnectUsbService {
    private static final String tag = ConnectUsbService.class.getSimpleName();
    private Activity activity;
    private UsbService usbService;

    public ConnectUsbService(Activity activity) {
        this.activity = activity;

    }

    public void changeDelay(int delay) {
        usbService.changeHandlerDelay(delay);
    }

    public void enableCheckCallStatus() {
        usbService.enableCheckCallStatus();
        threadStatus = true;
        Log.i(tag,"enableCheckCallStatus");
    }


    public void disableCheckCallStatus() {
        usbService.disableCheckCallStatus();
        threadStatus = false;
        Log.i(tag,"disableCheckCallStatus");
    }

    public void enableThreadService() {
        usbService.enableThreadService();
    }

    public void disableThreadService() {
        usbService.disableThreadService();
    }
}
