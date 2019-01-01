package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneLogFragment;

import android.content.Context;

import java.util.List;

import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallLog;


public class PhoneLogPresenter implements ProvidedPresenterOps, RequiredPresenterOps {

    private static PhoneLogPresenter phoneLogPresenter;

    private Context ctx;
    private final RequiredViewOps viewOps;
    private final ProvidedModelOps modelOps;

    public static PhoneLogPresenter getInstance(Context ctx, RequiredViewOps viewOps) {
        if (phoneLogPresenter == null)
            phoneLogPresenter = new PhoneLogPresenter(ctx, viewOps);
        return phoneLogPresenter;
    }

    public PhoneLogPresenter(Context ctx, RequiredViewOps viewOps) {
        this.ctx = ctx;
        this.viewOps = viewOps;
        modelOps = PhoneLogModel.getInstance(ctx, this);
    }

    @Override
    public void logFragmentStart() {
        List<CallLog> allLogs = modelOps.retriveAllCallLogs();
        viewOps.showPreparedCallLogs(allLogs);
    }

    @Override
    public void reloadRecyclerView() {
        List<CallLog> allLogs = modelOps.retriveAllCallLogs();
        viewOps.showPreparedCallLogs(allLogs);
    }

    @Override
    public void searchOnPhoneLogs(String searchText) {
        List<CallLog> logs = modelOps.searchOnCallLogsByName(searchText);
        logs.addAll(modelOps.searchOnCallLogsByPhone(searchText));
        viewOps.showPreparedCallLogs(logs);
    }

}
