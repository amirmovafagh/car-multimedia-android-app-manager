package com.hooshmandkhodro.carservice;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.TextView;

import com.hooshmandkhodro.carservice.steeringWheelController.ProvidedPresenterOps;
import com.hooshmandkhodro.carservice.steeringWheelController.RecyclerViewAdapter;
import com.hooshmandkhodro.carservice.steeringWheelController.RequiredViewOps;
import com.hooshmandkhodro.carservice.steeringWheelController.SteeringWheelControllerPresenter;


import com.hooshmandkhodro.carservice.steeringWheelController.Pojo.ControllerOption;
import com.hooshmandkhodro.carservice.steeringWheelController.Pojo.Options;


import java.util.ArrayList;
import java.util.List;

public class SteeringWheelContorllerActivity extends AppCompatActivity implements RequiredViewOps {

    ProvidedPresenterOps presenterOps = new SteeringWheelControllerPresenter(this, this);
    RecyclerViewAdapter rvAdapter;
    TextView tv1, tv2, tv3, tv4, tv5, tv6;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steering_wheel_contorller);

        tv1 = findViewById(R.id.tv_1);
        tv2 = findViewById(R.id.tv_2);
        tv3 = findViewById(R.id.tv_3);
        tv4 = findViewById(R.id.tv_4);
        tv5 = findViewById(R.id.tv_5);
        tv6 = findViewById(R.id.tv_6);
        RecyclerView rvActivity = findViewById(R.id.rv_activity);
        List<ControllerOption> data = new ArrayList<>();
        rvAdapter = new RecyclerViewAdapter(this, data, new RecyclerViewAdapter.NotifyActivity() {
            @Override
            public void clickedOnRefreshItem() {
                presenterOps.clickedOnRefreshItem();

            }

            @Override
            public void TouchDownItemOfRecyclerView(Options itemTouchDown) {

                presenterOps.clickedOnOptionsTouchDown(itemTouchDown);

            }

            @Override
            public void TouchUpItemOfRecyclerView(Options itemTouchUp) {

                presenterOps.clickedOnOptionsTouchUp(itemTouchUp);
            }
        });
        StaggeredGridLayoutManager glm = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        rvActivity.setLayoutManager(glm);
        rvActivity.setAdapter(rvAdapter);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                String data = String.valueOf(MyHandler.steeringWheelData);
                tv1.setText(data);
                tv2.setText(data);
                tv3.setText(data);
                tv4.setText(data);
                tv5.setText(data);
                tv6.setText(data);
                handler.postDelayed(this, 200);
            }
        };
        handler.postDelayed(runnable, 200);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenterOps.activityStart();

    }

    @Override
    public void reloadRecyclerView(List<ControllerOption> options) {
        rvAdapter.reloadRecylcerView(options);
    }

    private void startSteeringWheelControllerServic() {
        SteeringWheelControllerPresenter.SteeringWheelControllerActivityIsRun = false;
        MyHandler.steeringWheelData = 999;
    }

    private void stopSteeringWheelControllerServic() {
        SteeringWheelControllerPresenter.SteeringWheelControllerActivityIsRun = true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        startSteeringWheelControllerServic();

        handler.removeCallbacksAndMessages(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        startSteeringWheelControllerServic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startSteeringWheelControllerServic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopSteeringWheelControllerServic();
        if (runnable != null)
            handler.postDelayed(runnable, 200);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startSteeringWheelControllerServic();
    }
}
