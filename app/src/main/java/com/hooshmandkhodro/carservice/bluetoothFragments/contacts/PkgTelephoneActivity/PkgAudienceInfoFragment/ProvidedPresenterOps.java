package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment;

import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;

public interface ProvidedPresenterOps {
    void reloadRecyclerView();

    void searchOnAudienceInfos(String searchText);

    void clickedOnYesInRemoveAudiencePermissionDialog(Audience audience);

    String searchOnIdPhoneNumber(int id);
}
