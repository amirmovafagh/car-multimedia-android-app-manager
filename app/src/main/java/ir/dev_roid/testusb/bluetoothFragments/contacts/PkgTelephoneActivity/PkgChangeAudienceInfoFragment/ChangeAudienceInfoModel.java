package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgChangeAudienceInfoFragment;

import android.content.Context;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import ir.dev_roid.testusb.bluetoothFragments.contacts.ModelDAOs;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;


public class ChangeAudienceInfoModel implements ProvidedModelOps{

    private final ModelDAOs modelDAOs;

    private static ChangeAudienceInfoModel changeAudienceInfoModel = null;

    private final RequiredPresenterOps presenterOps;
    public static ChangeAudienceInfoModel getInstance(Context ctx, RequiredPresenterOps presenterOps) {
        if (changeAudienceInfoModel == null)
            changeAudienceInfoModel = new ChangeAudienceInfoModel(ctx, presenterOps);
        return changeAudienceInfoModel;
    }

    private ChangeAudienceInfoModel(Context ctx, RequiredPresenterOps presenterOps) {
        modelDAOs = ModelDAOs.getInstance(ctx);
        this.presenterOps = presenterOps;
    }

    @Override
    public void updatePhoneNumber(PhoneNumber pn) {
        modelDAOs.getPhoneNumberRuntimeExceptionDao().update(pn);
    }

    @Override
    public boolean isPhoneMatchingToAnotherAudience(String enteredPhone, int currentAudienceId) {

        List<PhoneNumber> phoneNumbers = modelDAOs.getPhoneNumberRuntimeExceptionDao()
                        .queryForEq(PhoneNumber.PHONE_FEILD_NAME, enteredPhone);

        if (phoneNumbers.size() == 0){
            //phone is new
            return false;
        }else {
            if (phoneNumbers.get(0).getAudience() == null){
                return false;
            }else if (phoneNumbers.get(0).getAudience().getIdAudience() == currentAudienceId){
                //phone is for current audience
                return false;
            }else {
                return true;
            }
        }

    }

    @Override
    public Audience getAudienceByPhone(String enteredPhone) {

        List<PhoneNumber> phoneNumbers = modelDAOs.getPhoneNumberRuntimeExceptionDao()
                .queryForEq(PhoneNumber.PHONE_FEILD_NAME, enteredPhone);

        if (phoneNumbers.size() == 0){
            return null;
        }else {
            return phoneNumbers.get(0).getAudience();
        }
    }

    @Override
    public PhoneNumber createPhoneNumberIfNotExsitThenMatchItToAudience(Integer idAudience, String phone) {

        List<PhoneNumber> phoneNumbers = modelDAOs.getPhoneNumberRuntimeExceptionDao()
                .queryForEq(PhoneNumber.PHONE_FEILD_NAME, phone);

        PhoneNumber newPhoneNumber;

        if (phoneNumbers.size() == 0){

            newPhoneNumber = new PhoneNumber();
            newPhoneNumber.setPhone(phone);

            Audience audience = modelDAOs.getAudienceRuntimeExceptionDao().queryForId(idAudience);
            newPhoneNumber.setAudience(audience);

            modelDAOs.getPhoneNumberRuntimeExceptionDao().create(newPhoneNumber);

        }else {
            newPhoneNumber = phoneNumbers.get(0);


            Audience audience = modelDAOs.getAudienceRuntimeExceptionDao().queryForId(idAudience);
            newPhoneNumber.setAudience(audience);

            modelDAOs.getPhoneNumberRuntimeExceptionDao().update(newPhoneNumber);
        }
        return newPhoneNumber;
    }

    @Override
    public void removeAllPhoneNumbersThatMatchedToThisAudience(Integer idAudience) {
        List<PhoneNumber> matchedPhonesToThisAudience = modelDAOs.getPhoneNumberRuntimeExceptionDao()
                .queryForEq(PhoneNumber.PHONE_FEILD_AUDIENCE_ID, idAudience);
        for (PhoneNumber pn : matchedPhonesToThisAudience)
        {
            pn.setAudience(null);
            modelDAOs.getPhoneNumberRuntimeExceptionDao().update(pn);
        }
    }

    @Override
    public void updateAudienceFistnameAndLastname(Integer idAudience, String firstname, String lastname) {
        Audience audience = modelDAOs.getAudienceRuntimeExceptionDao().queryForId(idAudience);
        audience.setFirstname(firstname);
        audience.setLastname(lastname);
        modelDAOs.getAudienceRuntimeExceptionDao().update(audience);
    }

    @Override
    public Audience getAudienceById(Integer idAudience) {
        return modelDAOs.getAudienceRuntimeExceptionDao().queryForId(idAudience);
    }

    @Override
    public Audience retriveAudienceExceptCurrentAudience(Integer idAudience, String firstname, String lastname) {
        Map<String, Object> mapFields = new HashMap<>();

        mapFields.put(Audience.FIRSTNAME_FEILD_NAME, firstname);
        mapFields.put(Audience.LASTNAME_FEILD_NAME, lastname);
        List<Audience> exsitAudiences = modelDAOs.getAudienceRuntimeExceptionDao().queryForFieldValues(mapFields);

        if (exsitAudiences.size() == 1)
            if (!exsitAudiences.get(0).getIdAudience().equals(idAudience))
                return exsitAudiences.get(0);

        return null;
    }

}
