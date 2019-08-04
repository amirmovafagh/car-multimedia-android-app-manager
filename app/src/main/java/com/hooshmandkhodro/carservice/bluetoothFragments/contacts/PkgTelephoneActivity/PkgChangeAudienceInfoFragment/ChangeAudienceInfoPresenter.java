package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgChangeAudienceInfoFragment;

import android.content.Context;

import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber;

public class ChangeAudienceInfoPresenter implements ProvidedPresenterOps, RequiredPresenterOps {

    private static ChangeAudienceInfoPresenter changeAudienceInfoPresenter;

    private Context ctx;
    private final RequiredViewOps viewOps;
    private final ProvidedModelOps modelOps;

    public static ChangeAudienceInfoPresenter getInstance(Context ctx, RequiredViewOps viewOps) {
        if (changeAudienceInfoPresenter == null)
            changeAudienceInfoPresenter = new ChangeAudienceInfoPresenter(ctx, viewOps);
        return changeAudienceInfoPresenter;
    }

    public ChangeAudienceInfoPresenter(Context ctx, RequiredViewOps viewOps) {
        this.ctx = ctx;
        this.viewOps = viewOps;
        modelOps = ChangeAudienceInfoModel.getInstance(ctx, this);
    }


    @Override
    public void saveChangedAudienceInfos(Audience newAudienceData) {

        Audience exsitedAudience = modelOps.retriveAudienceExceptCurrentAudience(
                newAudienceData.getIdAudience(),
                newAudienceData.getFirstname(), newAudienceData.getLastname()
        );
        if(exsitedAudience != null){
            viewOps.showMessageDialogBasedOnEnteredFirstNameAndLastNameExsit(exsitedAudience);
            return;
        }

        //1
        for (PhoneNumber pn : newAudienceData.getPhoneNumbers()){
            String EnteredPhone = pn.getPhone();
            int currentAudienceId = newAudienceData.getIdAudience();
            if (modelOps.isPhoneMatchingToAnotherAudience(EnteredPhone, currentAudienceId)) {

                Audience matchedAudience = modelOps.getAudienceByPhone(EnteredPhone);
                viewOps.showMessageDialogBasedOnNewEnteredPhoneMachedToAnotherAudience(EnteredPhone, matchedAudience);
                return;

            }
        }

        modelOps.removeAllPhoneNumbersThatMatchedToThisAudience(newAudienceData.getIdAudience());

        for (PhoneNumber ph : newAudienceData.getPhoneNumbers()){
            PhoneNumber updatePhoneNumber =
                    modelOps.createPhoneNumberIfNotExsitThenMatchItToAudience(newAudienceData.getIdAudience(), ph.getPhone());

        }

        modelOps.updateAudienceFistnameAndLastname(
                newAudienceData.getIdAudience(),
                newAudienceData.getFirstname(),
                newAudienceData.getLastname()
        );

        viewOps.showMessageDialogBasedOnAudienceUpdateSuccessfully(modelOps.getAudienceById(newAudienceData.getIdAudience()));
    }


}
