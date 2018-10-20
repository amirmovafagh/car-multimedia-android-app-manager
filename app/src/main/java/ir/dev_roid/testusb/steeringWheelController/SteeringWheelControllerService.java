package ir.dev_roid.testusb.steeringWheelController;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Switch;

import java.util.List;
import ir.dev_roid.testusb.steeringWheelController.Pojo.ControllerOption;
import ir.dev_roid.testusb.steeringWheelController.Pojo.Options;

import static ir.dev_roid.testusb.MyHandler.steeringWheelData;
import static ir.dev_roid.testusb.MyHandler.steeringWheelDataStatus;
import static ir.dev_roid.testusb.steeringWheelController.SteeringWheelControllerPresenter.SteeringWheelControllerActivityIsRun;


public class SteeringWheelControllerService extends Service {
    private static final String tag = SteeringWheelControllerService.class.getSimpleName();
    private ProvidedModelOps modelOps;
    private List<ControllerOption> options;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        modelOps = SteeringWheelControllerModel.getInstance(this);
        modelOps.createAllDaosIfNotExsit();
        steeringWheelData= 999;



        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                options = modelOps.getAllControllerOptions();
                for (ControllerOption co: options) {
                    if (co.getValue() != null) {

                        int i = co.getValue();
                        if (i - 3 <= steeringWheelData && steeringWheelData <= i + 3) {
                            Options id = co.getId();
                            switch (id){
                                case OPT0:
                                    Log.i(tag, "#power"+steeringWheelData );
                                    break;
                                case OPT1:
                                    Log.i(tag, "#SRC"+steeringWheelData );
                                    break;
                                case OPT2:
                                    Log.i(tag, "#gps"+steeringWheelData );
                                    break;
                                case OPT3:
                                    Log.i(tag, "#upVolume"+steeringWheelData );
                                    break;
                                case OPT4:
                                    Log.i(tag, "#downVolume" +steeringWheelData);
                                    break;
                                case OPT5:
                                    Log.i(tag, "#mute"+steeringWheelData );
                                    break;
                                case OPT6:
                                    Log.i(tag, "#play"+steeringWheelData );
                                    break;
                                case OPT7:
                                    Log.i(tag, "#back" +steeringWheelData);
                                    break;
                                case OPT8:
                                    Log.i(tag, "#next"+steeringWheelData );
                                    break;
                                case OPT10:
                                    Log.i(tag, "#<--" +steeringWheelData);
                                    break;
                                case OPT11:
                                    Log.i(tag, "#-->" +steeringWheelData);
                                    break;
                                case OPT12:
                                    Log.i(tag, "#answer"+steeringWheelData );
                                    break;
                                case OPT13:
                                    Log.i(tag, "#end"+steeringWheelData );
                                    break;

                            }
                            steeringWheelData = 999;


                        }
                    }
                    //Log.i(tag, "Dynamic Array Index #" + num.getId() );
                }
            }
        };
        performOnBackgroundThread(runnable);

    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {

        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (true){
                        sleep(100);
                        //Log.i(tag, "#RUN" );
                        if(!SteeringWheelControllerActivityIsRun){
                            runnable.run();
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        };

        t.start();
        return t;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
