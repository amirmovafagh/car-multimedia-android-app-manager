package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment;

import android.content.Context;


import java.util.Date;

import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.CallLog;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.CallType;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber;

public class PhoneDialerPresenter implements ProvidedPresenterOps, RequiredPresenterOps {

    private static PhoneDialerPresenter phoneDialerPresenter;

    private Context ctx;
    private final RequiredViewOps viewOps;
    private final ProvidedModelOps modelOps;

    public static PhoneDialerPresenter getInstance(Context ctx, RequiredViewOps viewOps) {
        if (phoneDialerPresenter == null)
            phoneDialerPresenter = new PhoneDialerPresenter(ctx, viewOps);
        return phoneDialerPresenter;
    }

    public PhoneDialerPresenter(Context ctx, RequiredViewOps viewOps) {
        this.ctx = ctx;
        this.viewOps = viewOps;
        modelOps = PhoneDialerModel.getInstance(ctx, this);
    }

    @Override
    public void clickedOnCallMadeButton(String dialedPhone) {
        //create phone number
        PhoneNumber phoneNumber = new PhoneNumber(dialedPhone);
        modelOps.createPhoneNumberIfNotExsit(phoneNumber);

        //crate calllog
        CallLog callLog = new CallLog();
        callLog.setPhoneNumber(phoneNumber);
        callLog.setDate(new Date());

        callLog.setCallType(CallType.getOutputInstance());


        //show creation status
        int insertedStatus = modelOps.insertCallLog(callLog);

        if (insertedStatus == 1)
            viewOps.showCallLogInsertSuccessfullyMessage();
        else
            viewOps.showCallLogNotInsertMessage();
    }

    @Override
    public void clickedOnCallRecivedButton(String dialedPhone) {

        PhoneNumber phoneNumber = new PhoneNumber(dialedPhone);
        modelOps.createPhoneNumberIfNotExsit(phoneNumber);

        CallLog callLog = new CallLog();
        callLog.setPhoneNumber(phoneNumber);
        callLog.setDate(new Date());

        callLog.setCallType(CallType.getInputInstance());


        int insertedStatus = modelOps.insertCallLog(callLog);

        if (insertedStatus == 1)
            viewOps.showCallLogInsertSuccessfullyMessage();
        else
            viewOps.showCallLogNotInsertMessage();
    }

    @Override
    public void clickedOnCallMissedButton(String dialedPhone) {
        PhoneNumber phoneNumber = new PhoneNumber(dialedPhone);
        modelOps.createPhoneNumberIfNotExsit(phoneNumber);

        CallLog callLog = new CallLog();
        callLog.setPhoneNumber(phoneNumber);
        callLog.setDate(new Date());

        callLog.setCallType(CallType.getMissedInstance());


        int insertedStatus = modelOps.insertCallLog(callLog);

        if (insertedStatus == 1)
            viewOps.showCallLogInsertSuccessfullyMessage();
        else
            viewOps.showCallLogNotInsertMessage();
    }

    @Override
    public String ifThereInContacts(String number) {

        return modelOps.searchNumberInContacts(number);
    }
}
