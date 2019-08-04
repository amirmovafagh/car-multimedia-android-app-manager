package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneLogFragment;


import java.util.List;

import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.CallLog;

public interface ProvidedModelOps {
    List<CallLog> retriveAllCallLogs();

    List<CallLog> searchOnCallLogsByName(String searchText);

    List<CallLog> searchOnCallLogsByPhone(String searchText);
}
