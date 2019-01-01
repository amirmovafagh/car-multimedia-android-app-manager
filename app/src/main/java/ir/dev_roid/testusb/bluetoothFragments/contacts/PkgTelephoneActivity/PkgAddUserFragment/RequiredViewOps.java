package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAddUserFragment;


import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;

public interface RequiredViewOps {
    void showAudienceAddSuccessfullyMessage(Audience newAudience);

    void showAudienceAddOccuredErrorMessage();

    void showDialogBasedOnAudienceInfoExsit(String phone, Audience exsistedAudience);

    void showDialogBasedOnEnteredPhoneNumberHasAnAudience(PhoneNumber dbPhoneNumberObj);
}
