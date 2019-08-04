package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment;


import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.CallLog;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber;

public interface ProvidedModelOps {

    void createPhoneNumberIfNotExsit(PhoneNumber phoneNumber);

    int insertCallLog(CallLog callLog);

    String searchNumberInContacts(String number);


}
