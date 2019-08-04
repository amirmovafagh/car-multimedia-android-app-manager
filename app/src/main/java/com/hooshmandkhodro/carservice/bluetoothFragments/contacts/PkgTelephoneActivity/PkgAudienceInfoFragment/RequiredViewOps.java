package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment;


import java.util.List;

import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;

public interface RequiredViewOps {
    void showPreparedAudienceInfos(List<Audience> audiences);

    void showAudienceRemoveSuccessfullyMessage(int removedRow);

    void showAudienceNotRemoveMessage();

}
