package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAddUserFragment;


import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber;

public interface RequiredViewOps {
    void showAudienceAddSuccessfullyMessage(Audience newAudience);

    void showAudienceAddOccuredErrorMessage();

    void showDialogBasedOnAudienceInfoExsit(String phone, Audience exsistedAudience);

    void showDialogBasedOnEnteredPhoneNumberHasAnAudience(PhoneNumber dbPhoneNumberObj);
}
