package com.hooshmandkhodro.carservice.steeringWheelController;

import com.hooshmandkhodro.carservice.steeringWheelController.Pojo.ControllerOption;

import java.util.List;

public interface RequiredViewOps {
    void reloadRecyclerView(List<ControllerOption> options);
}
