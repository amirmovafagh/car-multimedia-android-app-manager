package ir.dev_roid.testusb.bluetoothFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;

import ir.dev_roid.testusb.BluetoothActivity;
import ir.dev_roid.testusb.R;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Adapter.CallInfoAdapter;
import ir.dev_roid.testusb.bluetoothFragments.contacts.AsyncTask.GetCallInfoTask;
import ir.dev_roid.testusb.bluetoothFragments.contacts.Pojo.CallInfo;


public class LogFragment extends Fragment implements Serializable {

    private RecyclerView recyclerView;


    public LogFragment() {
        // Required empty public constructor

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_log, container, false);




        recyclerView  = (RecyclerView) view.findViewById(R.id.rv_callinfo);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        CallInfoAdapter callInfoAdapter = new CallInfoAdapter(getActivity(), new ArrayList<CallInfo>(), recyclerView);


        GetCallInfoTask callInfos = new GetCallInfoTask(getContext(), recyclerView);
        callInfos.execute();
        recyclerView.setAdapter(callInfoAdapter);



        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onDetach() {
        super.onDetach();


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


}
