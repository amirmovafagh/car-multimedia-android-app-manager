package com.hooshmandkhodro.carservice.app;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.things.pio.Gpio;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static com.hooshmandkhodro.carservice.MyHandler.buffer;
import static com.hooshmandkhodro.carservice.app.Receiver.timeWasSet;

public class ArmRTC {

    private Handler handler;
    private GpioUart gpioUart;
    int i;


    public ArmRTC(GpioUart gpioUart) {
        this.gpioUart = gpioUart;
    }

    public void setTimeOnHeadUnit() {
        timeWasSet = true;
        i = 0;
        handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gpioUart.sendData("oth-rtg?");
                handler.removeCallbacks(this,this);
            }
        },300);

        handler.postDelayed(() -> {
            i++;
            if (buffer.length() > 15 && buffer.contains("rtc-") && buffer.substring(10,11).equalsIgnoreCase("-") && Integer.parseInt(buffer.substring(11, 13)) != 0) {

                changeSystemTime(buffer.substring(4, 6), buffer.substring(6, 8), buffer.substring(8, 10), "20" + buffer.substring(11, 13),
                        buffer.substring(13, 15), buffer.substring(15, 17));
                Log.d("amir",""+buffer.substring(4, 6)+ buffer.substring(6, 8)+ buffer.substring(8, 10)+ "20" + buffer.substring(11, 13)+
                        buffer.substring(13, 15)+ buffer.substring(15, 17));
                handler.removeCallbacksAndMessages(this);
                return;
            } else if (i > 20) {
                i = 0;

                handler.removeCallbacksAndMessages(this);
                return;
            }
            handler.postDelayed(this::setTimeOnHeadUnit, 100);
        }, 350);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeWasSet = false;
                handler.removeCallbacksAndMessages(this);
            }
        },5000);

    }

    public void setTimeOnARM(){
        String date, month, year, hours, minutes, seconds;


        Date currentTime = Calendar.getInstance().getTime();
        currentTime.getDay();

        date = String.valueOf(currentTime.getDate());
        month = String.valueOf(currentTime.getMonth() +1 ); //start index from 0
        year = String.valueOf(currentTime.getYear()).substring(1);

        hours = String.valueOf(currentTime.getHours());
        minutes = String.valueOf(currentTime.getMinutes());
        seconds = String.valueOf(currentTime.getSeconds());

        if (currentTime.getHours() < 10)
            hours = "0" + hours;
        if (currentTime.getMinutes() < 10)
            minutes = "0" + minutes;
        if (currentTime.getSeconds() < 10)
            seconds = "0" + seconds;
        if (currentTime.getDate() < 10)
            date = "0" + date;
        if (currentTime.getMonth() < 10)
            month = "0" + month;
        if(currentTime.getYear() == 1970)
            return;
        Log.d("amir","oth-rtc-"+hours+"-"+minutes+"-"+seconds+"-"+year+"-"+month+"-"+date+"?");
        gpioUart.sendData("oth-rtc-"+hours+"-"+minutes+"-"+seconds+"-"+year+"-"+month+"-"+date+"?");
        //Toast.makeText(context, "time was set On Arm", Toast.LENGTH_SHORT).show();
    }

    private void changeSystemTime(String hour, String minute, String second, String year, String month, String day) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            String command = "date -s " + year + month + day + "." + hour + minute + second + "\n";
            Log.e("command", command);
            os.writeBytes(command);
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
