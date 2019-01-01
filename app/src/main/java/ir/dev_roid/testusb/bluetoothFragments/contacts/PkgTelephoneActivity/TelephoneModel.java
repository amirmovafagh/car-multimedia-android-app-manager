package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity;

import android.content.Context;

import ir.dev_roid.testusb.bluetoothFragments.contacts.ModelDAOs;

public class TelephoneModel implements ProvidedModelOps {

    private static final String TAG = TelephoneModel.class.getName();

    private final ModelDAOs modelDAOs;

    private static TelephoneModel telephoneModel = null;

    private final RequiredPresenterOps presenterOps;

    public static TelephoneModel getInstance(Context ctx, RequiredPresenterOps presenterOps) {
        if (telephoneModel == null)
            telephoneModel = new TelephoneModel(ctx, presenterOps);
        return telephoneModel;
    }

    private TelephoneModel(Context ctx, RequiredPresenterOps presenterOps) {
        modelDAOs = ModelDAOs.getInstance(ctx);
        this.presenterOps = presenterOps;
    }

    @Override
    public void createAllDaosIfNotExist() {
        //با خوندن داده ای از دیتابیس
        //تمام دیتابیس ها در صورت ای که وجود نداشته باشند ایجاد میشوند.
        modelDAOs.getAudienceRuntimeExceptionDao();
        modelDAOs.getCallInfoRuntimeExceptionDao();
        modelDAOs.getCallTypeRuntimeExceptionDao().queryForAll();
        modelDAOs.getPhoneNumberRuntimeExceptionDao();
    }
}
