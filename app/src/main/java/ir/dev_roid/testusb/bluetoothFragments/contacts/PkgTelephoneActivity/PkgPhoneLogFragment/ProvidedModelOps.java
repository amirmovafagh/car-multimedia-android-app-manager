package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneLogFragment;


import java.util.List;

import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallLog;

public interface ProvidedModelOps {
    List<CallLog> retriveAllCallLogs();

    List<CallLog> searchOnCallLogsByName(String searchText);

    List<CallLog> searchOnCallLogsByPhone(String searchText);
}
