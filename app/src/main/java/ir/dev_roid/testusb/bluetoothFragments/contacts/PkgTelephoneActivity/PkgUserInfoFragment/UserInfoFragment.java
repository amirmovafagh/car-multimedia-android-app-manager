package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgUserInfoFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import ir.dev_roid.testusb.R;
import ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.InfluenceOfUserInfoToActivity;
import ir.dev_roid.testusb.TelephoneActivity;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.Audience;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.PhoneNumber;

public class UserInfoFragment extends Fragment {

    private TextView tvUserInfoFirstname;
    private TextView tvUserInfoLastname;
    private ListView lvUserInfoPhones;

    Context ctx;

    InfluenceOfUserInfoToActivity toActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.ctx = context;
        toActivity = (TelephoneActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_info_layout, container, false);

        Audience audience = (Audience) getArguments().getSerializable("audienceInfo");

        tvUserInfoFirstname = v.findViewById(R.id.et_change_audience_info_firstname);
        tvUserInfoFirstname.setText(audience.getFirstname());

        tvUserInfoLastname = v.findViewById(R.id.et_change_audience_info_lastname);
        tvUserInfoLastname.setText(audience.getLastname());

        lvUserInfoPhones = v.findViewById(R.id.rv_change_audience_info_phones);

        List<String> phones = new ArrayList<>();
        for (PhoneNumber pn : audience.getPhoneNumbers())
            phones.add(pn.getPhone());

        ListAdapter adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, phones);
        lvUserInfoPhones.setAdapter(adapter);

        return v;
    }
}
