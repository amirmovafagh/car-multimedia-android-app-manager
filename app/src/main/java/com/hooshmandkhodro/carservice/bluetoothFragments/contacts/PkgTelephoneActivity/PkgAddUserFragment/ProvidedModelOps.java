package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAddUserFragment;


import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber;

public interface ProvidedModelOps {

    int createAudienceIfNotExsit(Audience newAudience);

    PhoneNumber retrivePhoneNumber(String phone);

    int updatePhoneNumber(PhoneNumber dbPhoneNumberObj);

    Audience retriveAudience(String firstname, String lastname);
}
