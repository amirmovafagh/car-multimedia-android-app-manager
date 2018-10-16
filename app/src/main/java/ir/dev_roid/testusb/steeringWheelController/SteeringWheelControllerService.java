package ir.dev_roid.testusb.steeringWheelController;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import io.reactivex.schedulers.Schedulers;
import ir.dev_roid.testusb.steeringWheelController.Pojo.ControllerOption;

import static ir.dev_roid.testusb.MyHandler.steeringWheelData;

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
        List<ControllerOption> options = modelOps.getAllControllerOptions();
        for (ControllerOption num: options) {

            Log.i(tag, "Dynamic Array Index #" + num.getId() );
        }

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
