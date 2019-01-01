package ir.dev_roid.testusb.app;

import android.app.Activity;

import android.util.Log;


import static ir.dev_roid.testusb.MyHandler.buffer;

public class BluetoothService {
    private String log = BluetoothService.class.getName();
    private Activity activity;
    private String status;

    public BluetoothService(Activity activity){
        this.activity = activity;

    }

    public String checkStatus(){ //check status of bluetooth 1.raedy 2.connecting 3.connected 4.outCall 5.inCall 6.onCall

        //connectUsbService.write("blt-cll-chk?");
        if(buffer.equalsIgnoreCase("MG1")){
            status = "ready";
        }else if(buffer.equalsIgnoreCase("MG2")){
            status = "connecting";
        }else if(buffer.equalsIgnoreCase("MG3")){
            status = "connected";
        }else if(buffer.equalsIgnoreCase("MG4")){
            status = "outCall";
        }else if(buffer.equalsIgnoreCase("MG5")){
            status = "inCall";
        }else if(buffer.equalsIgnoreCase("MG6")){
            status = "onCall";
        }else status = "fail";

        Log.i(log, status);
        return status;
    }


}
