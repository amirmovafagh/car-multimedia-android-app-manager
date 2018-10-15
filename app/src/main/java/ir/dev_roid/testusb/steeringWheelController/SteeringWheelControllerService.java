package ir.dev_roid.testusb.steeringWheelController;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class SteeringWheelControllerService extends Service {
    private static final String tag = SteeringWheelControllerService.class.getSimpleName();
    private ProvidedModelOps modelOps;

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
