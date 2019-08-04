package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneLogFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.hooshmandkhodro.carservice.R;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.CallLog;

import java.util.ArrayList;
import java.util.List;


import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.InfluenceOfPhoneLogToActivityOps;
import com.hooshmandkhodro.carservice.TelephoneActivity;

public class PhoneLogFragment extends Fragment implements RequiredViewOps
        , InfluenceOfActivityToPhoneLogOps {
    private static final String TAG = PhoneLogFragment.class.getName();

    private Context ctx;
    private ProvidedPresenterOps presenterOps;
    RecyclerViewAdapter recyclerViewAdapter;
    private InfluenceOfPhoneLogToActivityOps toActivityOps;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = getContext();
        presenterOps = new PhoneLogPresenter(ctx, this);
        toActivityOps = ((TelephoneActivity)getActivity());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_phone_log, container, false);

        RecyclerView rvPhoneLog = v.findViewById(R.id.rv_phone_log);
        List<CallLog> logs = new ArrayList<>();
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), logs, new RecyclerViewAdapter.NotifyFragment() {
            @Override
            public void clickOnUnknownImageUser(CallLog callLog) {
                toActivityOps.showAddUserFragment(callLog.getPhoneNumber());
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        rvPhoneLog.setLayoutManager(layoutManager);
        rvPhoneLog.setAdapter(recyclerViewAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ctx,
                layoutManager.getOrientation());
        rvPhoneLog.addItemDecoration(dividerItemDecoration);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenterOps.logFragmentStart();
    }

    public static PhoneLogFragment newInstance() {

        Bundle args = new Bundle();
        
        PhoneLogFragment fragment = new PhoneLogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void showPreparedCallLogs(List<CallLog> preparedLogs) {
        recyclerViewAdapter.updateRecyclerView(preparedLogs);

    }



    @Override
    public void fillRecyclerView() {
        presenterOps.reloadRecyclerView();
    }

    @Override
    public void searchActived(String searchText) {

        presenterOps.searchOnPhoneLogs(searchText);
    }


}
