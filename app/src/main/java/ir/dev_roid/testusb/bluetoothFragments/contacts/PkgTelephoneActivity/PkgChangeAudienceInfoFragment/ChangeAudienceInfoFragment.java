package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgChangeAudienceInfoFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ir.dev_roid.testusb.R;
import ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.InfluenceOfChangeAudienceInfoToActivity;
import ir.dev_roid.testusb.TelephoneActivity;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;


public class ChangeAudienceInfoFragment extends Fragment implements RequiredViewOps {

    private Context ctx;
    private ProvidedPresenterOps presenterOps;
    private EditText etChangeAudienceInfoFirstname;
    private EditText etChangeAudienceInfoLastname;

    private InfluenceOfChangeAudienceInfoToActivity toActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = getContext();
        presenterOps = new ChangeAudienceInfoPresenter(ctx, this);
        toActivity = (TelephoneActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Audience data = (Audience) getArguments().getSerializable("audienceInfo");

        View v = inflater.inflate(R.layout.fragment_chage_audience_info, container,false);
        etChangeAudienceInfoFirstname = v.findViewById(R.id.et_change_audience_info_firstname);
        etChangeAudienceInfoLastname = v.findViewById(R.id.et_change_audience_info_lastname);

        etChangeAudienceInfoFirstname.setText(data.getFirstname());
        etChangeAudienceInfoLastname.setText(data.getLastname());


        RecyclerView rvChangeAudienceInfoPhones = v.findViewById(R.id.rv_change_audience_info_phones);
        RecyclerViewAdapter rvAdapter = new RecyclerViewAdapter(ctx, data);
        rvChangeAudienceInfoPhones.setAdapter(rvAdapter);
        rvChangeAudienceInfoPhones.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false));


        Button btnChangeAudienceInfoSave = v.findViewById(R.id.btn_change_audience_info_save);
        btnChangeAudienceInfoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                data.setFirstname(etChangeAudienceInfoFirstname.getText().toString());
                data.setLastname(etChangeAudienceInfoLastname.getText().toString());
                presenterOps.saveChangedAudienceInfos(data);

            }
        });

        Button btnChangeAudienceInfoCancel = v.findViewById(R.id.btn_change_audience_info_cancel);
        btnChangeAudienceInfoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivity.closeChangeAudienceInfoFragment();
            }
        });
        return v;
    }

    @Override
    public void showMessageDialogBasedOnNewEnteredPhoneMachedToAnotherAudience(String enteredPhone, Audience matchedAudience) {
        String message = "phoen " + enteredPhone + "matched to " +
                (matchedAudience.getFirstname() + " " + matchedAudience.getLastname());
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessageDialogBasedOnAudienceUpdateSuccessfully(Audience updatedAudience) {
        Toast.makeText(ctx, "audience udapte successfully", Toast.LENGTH_SHORT).show();
        Toast.makeText(ctx, updatedAudience.toString(), Toast.LENGTH_SHORT).show();
        toActivity.closeChangeAudienceInfoFragmentAndUpdateRecyclerView();
    }

    @Override
    public void showMessageDialogBasedOnEnteredFirstNameAndLastNameExsit(Audience exsitedAudience) {
        String Message = "Audience with name " +
                (exsitedAudience.getFirstname() + " " + exsitedAudience.getLastname())
                + "exsit ";
        Toast.makeText(ctx, Message, Toast.LENGTH_SHORT).show();
    }
}
