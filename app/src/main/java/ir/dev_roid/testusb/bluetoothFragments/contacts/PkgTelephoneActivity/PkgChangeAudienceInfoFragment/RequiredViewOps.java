package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgChangeAudienceInfoFragment;


import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;

public interface RequiredViewOps {
    void showMessageDialogBasedOnNewEnteredPhoneMachedToAnotherAudience(String enteredPhone, Audience matchedAudience);

    void showMessageDialogBasedOnAudienceUpdateSuccessfully(Audience updatedAudience);

    void showMessageDialogBasedOnEnteredFirstNameAndLastNameExsit(Audience exsitedAudience);
}
