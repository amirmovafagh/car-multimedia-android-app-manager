package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.ModelDAOs;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber;

import static com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber.PHONE_FEILD_AUDIENCE_ID;

public class AudienceInfoModel implements ProvidedModelOps {

    private final ModelDAOs modelDAOs;

    private static AudienceInfoModel audienceInfoModel = null;

    private final RequiredPresenterOps presenterOps;

    public static AudienceInfoModel getInstance(Context ctx, RequiredPresenterOps presenterOps) {
        if (audienceInfoModel == null)
            audienceInfoModel = new AudienceInfoModel(ctx, presenterOps);
        return audienceInfoModel;
    }

    private AudienceInfoModel(Context ctx, RequiredPresenterOps presenterOps) {
        modelDAOs = ModelDAOs.getInstance(ctx);
        this.presenterOps = presenterOps;
    }

    @Override
    public List<Audience> retriveAllAudiences() {
        return modelDAOs.getAudienceRuntimeExceptionDao().queryForAll();
    }

    @Override
    public List<Audience> searchOnAudiencesByName(String searchText) {
        List<Audience> match = new ArrayList<>();
        List<Audience> allAudiences = modelDAOs.getAudienceRuntimeExceptionDao().queryForAll();
        for (Audience aud : allAudiences) {
            String audName = aud.getFirstname() + aud.getLastname();
            if (audName.contains(searchText))
                match.add(aud);
        }
        return match;
    }


    @Override
    public int removeAnAudience(Audience audience) {
        return modelDAOs.getAudienceRuntimeExceptionDao().delete(audience);

    }

    @Override
    public String searchOnIdPhoneNumber(int id) {
        List<PhoneNumber> allPhoneNumbers = modelDAOs.getPhoneNumberRuntimeExceptionDao().queryForEq(PHONE_FEILD_AUDIENCE_ID, id);
        for (PhoneNumber apn : allPhoneNumbers) {
            return apn.getPhone();
        }
        return "non";
    }

}