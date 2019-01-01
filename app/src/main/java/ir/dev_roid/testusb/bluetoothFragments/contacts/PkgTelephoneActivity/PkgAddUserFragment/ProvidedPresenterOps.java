package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAddUserFragment;


import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;

public interface ProvidedPresenterOps {
    void clickOnBtnSave(String phone, String newUserFirstName, String newUserLastName);

    void clickOnYesInAddExsitedAudienceInfoDialog(String phone, Audience exsistedAudience);
}
