package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgChangeAudienceInfoFragment;


import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;

public interface RequiredViewOps {
    void showMessageDialogBasedOnNewEnteredPhoneMachedToAnotherAudience(String enteredPhone, Audience matchedAudience);

    void showMessageDialogBasedOnAudienceUpdateSuccessfully(Audience updatedAudience);

    void showMessageDialogBasedOnEnteredFirstNameAndLastNameExsit(Audience exsitedAudience);
}
