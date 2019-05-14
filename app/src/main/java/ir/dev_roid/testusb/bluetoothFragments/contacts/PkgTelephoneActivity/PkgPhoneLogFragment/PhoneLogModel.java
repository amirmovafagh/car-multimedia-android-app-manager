package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneLogFragment;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ir.dev_roid.testusb.bluetoothFragments.contacts.ModelDAOs;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallLog;

public class PhoneLogModel implements ProvidedModelOps {
    private static final String TAG = PhoneLogModel.class.getName();
    private final ModelDAOs modelDAOs;

    private static PhoneLogModel phoneLogModel = null;

    private final RequiredPresenterOps presenterOps;
    public static PhoneLogModel getInstance(Context ctx, RequiredPresenterOps presenterOps) {
        if (phoneLogModel == null)
            phoneLogModel = new PhoneLogModel(ctx, presenterOps);
        return phoneLogModel;
    }

    private PhoneLogModel(Context ctx, RequiredPresenterOps presenterOps) {
        modelDAOs = ModelDAOs.getInstance(ctx);
        this.presenterOps = presenterOps;
    }

    @Override
    public List<CallLog> retriveAllCallLogs() {
        try {
            return modelDAOs.getCallInfoRuntimeExceptionDao().queryForAll();
        }catch (Exception e){
            return null;
        }

    }

    @Override
    public List<CallLog> searchOnCallLogsByName(String searchText) {
        List<CallLog> logs = new ArrayList<>();
        List<CallLog> allLogs = modelDAOs.getCallInfoRuntimeExceptionDao().queryForAll();
        for (CallLog cl : allLogs)
        {
            Audience callLogAudience = cl.getPhoneNumber().getAudience();
            if (callLogAudience != null)
            {
                String audName = callLogAudience.getFirstname() + callLogAudience.getLastname();
                if (audName.contains(searchText))
                    logs.add(cl);
            }
        }
        return logs;
    }

    @Override
    public List<CallLog> searchOnCallLogsByPhone(String searchText) {
        List<CallLog> logs = new ArrayList<>();
        //check searchText be numeric if not return empty list
        for(int i=0;i<searchText.length();i++)
            if (!(searchText.charAt(i)>='0' && searchText.charAt(i)<='9'))
                return logs;


        List<CallLog> allLogs = modelDAOs.getCallInfoRuntimeExceptionDao().queryForAll();
        for (CallLog cl : allLogs)
            if (cl.getPhoneNumber().getPhone().contains(searchText))
                logs.add(cl);

        return logs;
    }
}
