package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgChangeAudienceInfoFragment;

import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;

public interface ProvidedModelOps {
    void updatePhoneNumber(PhoneNumber pn);

    boolean isPhoneMatchingToAnotherAudience(String enteredPhone, int currentAudienceId);

    Audience getAudienceByPhone(String enteredPhone);

    PhoneNumber createPhoneNumberIfNotExsitThenMatchItToAudience(Integer idAudience, String phone);

    void removeAllPhoneNumbersThatMatchedToThisAudience(Integer idAudience);

    void updateAudienceFistnameAndLastname(Integer idAudience, String firstname, String lastname);

    Audience getAudienceById(Integer idAudience);

    Audience retriveAudienceExceptCurrentAudience(Integer idAudience, String firstname, String lastname);
}
