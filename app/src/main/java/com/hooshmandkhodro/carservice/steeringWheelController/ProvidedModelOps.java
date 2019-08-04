package com.hooshmandkhodro.carservice.steeringWheelController;

import com.hooshmandkhodro.carservice.steeringWheelController.Pojo.ControllerOption;

import java.util.List;

public interface ProvidedModelOps {
    void createAllDaosIfNotExsit();

    List<ControllerOption> getAllControllerOptions();
    List<ControllerOption> checkIfEQ(String fieldName, Object value);
    List<ControllerOption> checkValueBetween(String min, String max);

    void updateControllerOptions(List<ControllerOption> options);
}
