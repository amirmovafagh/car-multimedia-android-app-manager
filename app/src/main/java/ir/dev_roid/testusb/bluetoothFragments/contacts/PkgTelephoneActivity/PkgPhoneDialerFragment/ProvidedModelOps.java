package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment;


import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallLog;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;

public interface ProvidedModelOps {

    void createPhoneNumberIfNotExsit(PhoneNumber phoneNumber);

    int insertCallLog(CallLog callLog);

    String searchNumberInContacts(String number);


}
