package ir.dev_roid.testusb.steeringWheelController;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.widget.Toast;

import ir.dev_roid.testusb.steeringWheelController.Pojo.ControllerOption;
import ir.dev_roid.testusb.steeringWheelController.Pojo.Options;

import java.util.List;

import static ir.dev_roid.testusb.MyHandler.steeringWheelData;
import static ir.dev_roid.testusb.MyHandler.steeringWheelDataStatus;

public class SteeringWheelControllerPresenter implements RequiredPresenterOps, ProvidedPresenterOps {
    private static final String tag = SteeringWheelControllerPresenter.class.getSimpleName();
    private static SteeringWheelControllerPresenter steeringWheelControllerPresenter;
    public static Boolean SteeringWheelControllerActivityIsRun = false;

    private Context ctx;
    private final RequiredViewOps viewOps;
    private final ProvidedModelOps modelOps;


    public static SteeringWheelControllerPresenter getInstance(Context ctx, RequiredViewOps viewOps) {
        if (steeringWheelControllerPresenter == null)
            steeringWheelControllerPresenter = new SteeringWheelControllerPresenter(ctx, viewOps);
        return steeringWheelControllerPresenter;
    }

    public SteeringWheelControllerPresenter(Context ctx, RequiredViewOps viewOps) {
        this.ctx = ctx;
        this.viewOps = viewOps;
        modelOps = SteeringWheelControllerModel.getInstance(ctx, this);
    }



    @Override
    public void activityStart() {
        modelOps.createAllDaosIfNotExsit();
        List<ControllerOption> options = modelOps.getAllControllerOptions();
        viewOps.reloadRecyclerView(options);
        SteeringWheelControllerActivityIsRun = true;
    }

    @Override
    public void clickedOnRefreshItem() {

        List<ControllerOption> options = modelOps.getAllControllerOptions();
        for (ControllerOption co : options)
            co.setValue(null);
        modelOps.updateControllerOptions(options);

        options = modelOps.getAllControllerOptions();
        viewOps.reloadRecyclerView(options);



    }

    @Override
    public void clickedOnOptionsTouchDown(Options itemTouchDown) {

        if(steeringWheelDataStatus){
            List<ControllerOption> options = modelOps.checkValueBetween(String.valueOf((steeringWheelData - 3)) , String.valueOf((steeringWheelData + 3)));

            for (ControllerOption co : options) {
                Log.i(tag, " " + co.getValue());
                Toast.makeText(ctx, "خطا در مقدار دهی دوباره سعی کنید", Toast.LENGTH_SHORT).show();
                return;

            }

            options = modelOps.getAllControllerOptions();
            for (ControllerOption co : options) {
                if (co.getId() == itemTouchDown) {
                    if (co.getValue() != null) {
                        Log.i(tag, " " + 2);
                        int i = co.getValue();
                        if (i - 3 <= steeringWheelData && steeringWheelData <= i + 3) {

                            Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if(steeringWheelData !=999){
                        co.setValue(steeringWheelData);
                        modelOps.updateControllerOptions(options);

                        options = modelOps.getAllControllerOptions();
                        viewOps.reloadRecyclerView(options);

                    }


                }
            }
        }else Toast.makeText(ctx, "لطفا دکمه فرمان را نگه دارید", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void clickedOnOptionsTouchUp(Options itemTouchUp) {
        steeringWheelData = 999;
    }


}
