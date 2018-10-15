package ir.dev_roid.testusb.steeringWheelController;


import ir.dev_roid.testusb.steeringWheelController.Pojo.Options;

public interface ProvidedPresenterOps {
    void activityStart();

    void clickedOnRefreshItem();

    void clickedOnOptionsTouchDown(Options itemTouchDown);

    void clickedOnOptionsTouchUp(Options itemTouchUp);



}
