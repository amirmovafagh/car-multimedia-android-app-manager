package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment;

import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;

public interface ProvidedPresenterOps {
    void reloadRecyclerView();

    void searchOnAudienceInfos(String searchText);

    void clickedOnYesInRemoveAudiencePermissionDialog(Audience audience);

    String searchOnIdPhoneNumber(int id);
}
