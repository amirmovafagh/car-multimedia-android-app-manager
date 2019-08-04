package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAddUserFragment;


import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;

public interface ProvidedPresenterOps {
    void clickOnBtnSave(String phone, String newUserFirstName, String newUserLastName);

    void clickOnYesInAddExsitedAudienceInfoDialog(String phone, Audience exsistedAudience);
}
