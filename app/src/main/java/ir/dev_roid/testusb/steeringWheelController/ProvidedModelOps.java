package ir.dev_roid.testusb.steeringWheelController;

import ir.dev_roid.testusb.steeringWheelController.Pojo.ControllerOption;

import java.util.List;

public interface ProvidedModelOps {
    void createAllDaosIfNotExsit();

    List<ControllerOption> getAllControllerOptions();
    List<ControllerOption> checkIfEQ(String fieldName, Object value);
    List<ControllerOption> readFieldValue(String low, String high);

    void updateControllerOptions(List<ControllerOption> options);
}
