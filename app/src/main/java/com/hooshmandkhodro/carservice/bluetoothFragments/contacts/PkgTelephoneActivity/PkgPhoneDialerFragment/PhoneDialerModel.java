package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment;

import android.content.Context;

import java.util.List;

import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.ModelDAOs;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.CallLog;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber;

public class PhoneDialerModel implements ProvidedModelOps {

    private final ModelDAOs modelDAOs;

    private static PhoneDialerModel phoneDialerModel = null;

    private final RequiredPresenterOps presenterOps;
    public static PhoneDialerModel getInstance(Context ctx, RequiredPresenterOps presenterOps) {
        if (phoneDialerModel == null)
            phoneDialerModel = new PhoneDialerModel(ctx, presenterOps);
        return phoneDialerModel;
    }

    private PhoneDialerModel(Context ctx, RequiredPresenterOps presenterOps) {
        modelDAOs = ModelDAOs.getInstance(ctx);
        this.presenterOps = presenterOps;
    }

    @Override
    public void createPhoneNumberIfNotExsit(PhoneNumber phoneNumber) {
        int phoneNumberId = -1;
        List<PhoneNumber> phoneNumbers =
                modelDAOs.getPhoneNumberRuntimeExceptionDao().queryForEq(PhoneNumber.PHONE_FEILD_NAME, phoneNumber.getPhone());

        if (phoneNumbers.size() == 0){
            //phoneNumber not exist
            modelDAOs.getPhoneNumberRuntimeExceptionDao().create(phoneNumber);
        }else {
            phoneNumber.setIdPhoneNumber(phoneNumbers.get(0).getIdPhoneNumber());
        }

        return ;
    }

    @Override
    public int insertCallLog(CallLog callLog) {
        return modelDAOs.getCallInfoRuntimeExceptionDao().create(callLog);
    }

    @Override
    public String searchNumberInContacts(String number) {
        try{
            List<PhoneNumber> allPhoneNumbers = modelDAOs.getPhoneNumberRuntimeExceptionDao().queryForEq("phone",number);
            for (PhoneNumber aph : allPhoneNumbers){

                return aph.getAudience().getFirstname() +" "+ aph.getAudience().getLastname();
            }

        }
        catch (Exception e){}
        return "non";
    }
}
