package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAddUserFragment;

import android.content.Context;
import android.widget.Toast;

import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;


public class AddUserPresenter implements ProvidedPresenterOps, RequiredPresenterOps {

    private static AddUserPresenter addUserPresenter;

    private Context ctx;
    private final RequiredViewOps viewOps;
    private final ProvidedModelOps modelOps;

//    public static AddUserPresenter getInstance(Context ctx, RequiredViewOps viewOps) {
//        if (addUserPresenter == null)
//            addUserPresenter = new AddUserPresenter(ctx, viewOps);
//        return addUserPresenter;
//    }

    public AddUserPresenter(Context ctx, RequiredViewOps viewOps) {
        this.ctx = ctx;
        this.viewOps = viewOps;
        modelOps = AddUserModel.getInstance(ctx, this);
    }

    @Override
    public void clickOnBtnSave(String phone, String newUserFirstName, String newUserLastName) {

        Audience newAudience = new Audience(null, newUserFirstName, newUserLastName, null);
        PhoneNumber dbPhoneNumberObj = modelOps.retrivePhoneNumber(phone);
        //not noted
        ///////-------------------------------------------------------------------------------
        if (dbPhoneNumberObj.getAudience() != null){
            viewOps.showDialogBasedOnEnteredPhoneNumberHasAnAudience(dbPhoneNumberObj);
            return;
        }

        ///////-------------------------------------------------------------------------------

        int insertionResult = modelOps.createAudienceIfNotExsit(newAudience);
        if (insertionResult == AddUserModel.INSERTION_RESULT_AUDIENCE_CREATE){

            Toast.makeText(ctx, "audience info in new", Toast.LENGTH_SHORT).show();
            
            dbPhoneNumberObj.setAudience(newAudience);

            int updateResult = modelOps.updatePhoneNumber(dbPhoneNumberObj);
            if (updateResult > 0 )
                viewOps.showAudienceAddSuccessfullyMessage(newAudience);
            else 
                viewOps.showAudienceAddOccuredErrorMessage();

        }else if (insertionResult == AddUserModel.INSERTION_RESULT_AUDIENCE_EXSIT){

            Audience dbAudienceObj = modelOps.retriveAudience(newAudience.getFirstname(), newAudience.getLastname());
            viewOps.showDialogBasedOnAudienceInfoExsit(phone, dbAudienceObj);

        }else {

            Toast.makeText(ctx, "audience insertion occure error !!!", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void clickOnYesInAddExsitedAudienceInfoDialog(String phone, Audience exsistedAudience) {

        PhoneNumber dbPhoneNumberObj = modelOps.retrivePhoneNumber(phone);
        dbPhoneNumberObj.setAudience(exsistedAudience);

        modelOps.updatePhoneNumber(dbPhoneNumberObj);

        viewOps.showAudienceAddSuccessfullyMessage(exsistedAudience);
    }

}
