package com.hooshmandkhodro.carservice.steeringWheelController;


import com.hooshmandkhodro.carservice.steeringWheelController.Pojo.Options;

public interface ProvidedPresenterOps {
    void activityStart();

    void clickedOnRefreshItem();

    void clickedOnOptionsTouchDown(Options itemTouchDown);

    void clickedOnOptionsTouchUp(Options itemTouchUp);



}
