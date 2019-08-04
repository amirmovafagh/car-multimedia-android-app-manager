package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment;

public interface ProvidedPresenterOps {
    void clickedOnCallMadeButton(String dialedPhone);

    void clickedOnCallRecivedButton(String dialedPhone);

    void clickedOnCallMissedButton(String dialedPhone);

    String ifThereInContacts (String number);
}
