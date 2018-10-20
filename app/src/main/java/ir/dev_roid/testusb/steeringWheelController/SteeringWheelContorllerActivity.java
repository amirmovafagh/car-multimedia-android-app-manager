package ir.dev_roid.testusb.steeringWheelController;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import ir.dev_roid.testusb.R;
import ir.dev_roid.testusb.steeringWheelController.Pojo.ControllerOption;
import ir.dev_roid.testusb.steeringWheelController.Pojo.Options;



import java.util.ArrayList;
import java.util.List;

import static ir.dev_roid.testusb.MyHandler.steeringWheelData;
import static ir.dev_roid.testusb.steeringWheelController.SteeringWheelControllerPresenter.SteeringWheelControllerActivityIsRun;

public class SteeringWheelContorllerActivity extends AppCompatActivity implements RequiredViewOps {

    ProvidedPresenterOps presenterOps = new SteeringWheelControllerPresenter(this, this);
    RecyclerViewAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steering_wheel_contorller);

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

    private void startSteeringWheelControllerServic(){
        SteeringWheelControllerActivityIsRun = false;
        steeringWheelData = 999;
    }

    private void stopSteeringWheelControllerServic(){
        SteeringWheelControllerActivityIsRun = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        startSteeringWheelControllerServic();
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startSteeringWheelControllerServic();
    }
}
