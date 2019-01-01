package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment;


import java.util.List;

import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;

public interface RequiredViewOps {
    void showPreparedAudienceInfos(List<Audience> audiences);

    void showAudienceRemoveSuccessfullyMessage(int removedRow);

    void showAudienceNotRemoveMessage();

}
