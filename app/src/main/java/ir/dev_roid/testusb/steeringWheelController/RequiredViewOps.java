package ir.dev_roid.testusb.steeringWheelController;

import ir.dev_roid.testusb.steeringWheelController.Pojo.ControllerOption;

import java.util.List;

public interface RequiredViewOps {
    void reloadRecyclerView(List<ControllerOption> options);
}
