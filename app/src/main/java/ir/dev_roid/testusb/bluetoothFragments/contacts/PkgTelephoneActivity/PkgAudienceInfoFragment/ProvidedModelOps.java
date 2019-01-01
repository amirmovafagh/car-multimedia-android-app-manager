package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment;


import java.util.List;

import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;

public interface ProvidedModelOps {
    List<Audience> retriveAllAudiences();


    List<Audience> searchOnAudiencesByName(String searchText);

    int removeAnAudience(Audience audience);

    String searchOnIdPhoneNumber(int id);
}
