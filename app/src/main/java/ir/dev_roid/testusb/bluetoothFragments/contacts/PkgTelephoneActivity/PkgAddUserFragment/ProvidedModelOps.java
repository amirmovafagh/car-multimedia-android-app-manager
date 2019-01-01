package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAddUserFragment;


import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;

public interface ProvidedModelOps {

    int createAudienceIfNotExsit(Audience newAudience);

    PhoneNumber retrivePhoneNumber(String phone);

    int updatePhoneNumber(PhoneNumber dbPhoneNumberObj);

    Audience retriveAudience(String firstname, String lastname);
}
