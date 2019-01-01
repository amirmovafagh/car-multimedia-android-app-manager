package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity;

import android.content.Context;

public class TelephonePresenter implements  ProvidedPresenterOps, RequiredPresenterOps {

    private static TelephonePresenter telephonePresenter;

    private Context ctx;
    private final RequiredViewOps viewOps;
    private final ProvidedModelOps modelOps;

    public static TelephonePresenter getInstance(Context ctx, RequiredViewOps viewOps) {
        if (telephonePresenter == null)
            telephonePresenter = new TelephonePresenter(ctx, viewOps);
        return telephonePresenter;
    }

    public TelephonePresenter(Context ctx, RequiredViewOps viewOps) {
        this.ctx = ctx;
        this.viewOps = viewOps;
        modelOps = TelephoneModel.getInstance(ctx, this);
    }

    @Override
    public void activityStart() {
        modelOps.createAllDaosIfNotExist();
    }
}
