package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAddUserFragment;

import android.content.Context;

import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.ModelDAOs;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddUserModel implements ProvidedModelOps{

    private final ModelDAOs modelDAOs;
    final static int INSERTION_RESULT_AUDIENCE_CREATE = 1;
    final static int INSERTION_RESULT_AUDIENCE_EXSIT = 0;
    private static AddUserModel addUserModel = null;

    private final RequiredPresenterOps presenterOps;
    public static AddUserModel getInstance(Context ctx, RequiredPresenterOps presenterOps) {
        if (addUserModel == null)
            addUserModel = new AddUserModel(ctx, presenterOps);
        return addUserModel;
    }

    private AddUserModel(Context ctx, RequiredPresenterOps presenterOps) {
        modelDAOs = ModelDAOs.getInstance(ctx);
        this.presenterOps = presenterOps;
    }

    @Override
    public int createAudienceIfNotExsit(Audience newAudience) {
        int insertionResult = -1;

        Map<String, Object> mapFields = new HashMap<>();

        mapFields.put(Audience.FIRSTNAME_FEILD_NAME, newAudience.getFirstname());
        mapFields.put(Audience.LASTNAME_FEILD_NAME, newAudience.getLastname());
        List<Audience> exsitAudiences = modelDAOs.getAudienceRuntimeExceptionDao().queryForFieldValues(mapFields);

        if (exsitAudiences.size() == 0){// audience name is new
            insertionResult = modelDAOs.getAudienceRuntimeExceptionDao().create(newAudience);
        }else if (exsitAudiences.size() == 1){
            insertionResult = 0;
        }

        return insertionResult;
    }

    @Override
    public PhoneNumber retrivePhoneNumber(String phone) {
        PhoneNumber ph = null;
        List<PhoneNumber> matchedWithPhone = modelDAOs.getPhoneNumberRuntimeExceptionDao()
                .queryForEq(PhoneNumber.PHONE_FEILD_NAME, phone);//because PHONE_FEILD_NAME is unique in DB
        if (matchedWithPhone.size() == 1){
            ph = matchedWithPhone.get(0);//user is in CallLog fragment now
        }else if (matchedWithPhone.size() == 0){
            //user is in AudienceInfo fragment now and want to create a new audience with new phonenumber
            PhoneNumber newPhoneNumber = new PhoneNumber();
            newPhoneNumber.setPhone(phone);
            modelDAOs.getPhoneNumberRuntimeExceptionDao().create(newPhoneNumber);
            ph = newPhoneNumber;
        }else {
            //error phone feild of PhoneNumber must be unique
        }
        return ph;
    }

    @Override
    public int updatePhoneNumber(PhoneNumber dbPhoneNumberObj) {
        return modelDAOs.getPhoneNumberRuntimeExceptionDao().update(dbPhoneNumberObj);
    }

    @Override
    public Audience retriveAudience(String firstname, String lastname) {
        Map<String, Object> mapFields = new HashMap<>();

        mapFields.put(Audience.FIRSTNAME_FEILD_NAME, firstname);
        mapFields.put(Audience.LASTNAME_FEILD_NAME, lastname);
        List<Audience> exsitAudiences = modelDAOs.getAudienceRuntimeExceptionDao().queryForFieldValues(mapFields);

        if (exsitAudiences.size() == 1)
            return exsitAudiences.get(0);

        return null;
    }
}
