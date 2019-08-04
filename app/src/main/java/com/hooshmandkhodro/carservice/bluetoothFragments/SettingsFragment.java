package com.hooshmandkhodro.carservice.bluetoothFragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.hooshmandkhodro.carservice.R;
import com.hooshmandkhodro.carservice.app.BluetoothService;
import com.skyfishjy.library.RippleBackground;


import com.hooshmandkhodro.carservice.app.GpioUart;
import com.hooshmandkhodro.carservice.app.PrefManager;

import static com.hooshmandkhodro.carservice.BluetoothActivity.connectGpioUartBt;

import static com.hooshmandkhodro.carservice.UsbService.threadStatus;


public class SettingsFragment extends Fragment {
    private static final String log = SettingsFragment.class.getName();
    private View view;
    private RippleBackground rippleBackground;
    private ImageView imageView, connectedPhoneImg;
    private Button enterPairBtn, cancelPairBtn;
    private GpioUart gpioUart;
    private ToggleButton autoAnswerTbtn, autoConnectTbtn;
    private BluetoothService bluetoothService;
    private Handler handler;
    private Runnable runnable;
    private PrefManager prefManager;


    public SettingsFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gpioUart = connectGpioUartBt;
        bluetoothService = new BluetoothService(getActivity());
        connectionStatus();
        prefManager= new PrefManager(getContext());



        initSetOnClick();
    }

    public void connectionStatus(){
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                String status = bluetoothService.checkStatus();
                if(status == "ready" || status == "connecting"){
                    startRipple();
                    connectedPhoneImg.setVisibility(View.GONE);
                    handler.postDelayed(this,2000);
                }else if(status == "connected" || status == "outCall" || status == "inCall" || status == "onCall"){
                    startRipple();
                    connectedPhoneImg.setVisibility(View.VISIBLE);
                    handler.postDelayed(this,10000);
                }else {
                    connectedPhoneImg.setVisibility(View.GONE);
                    stopRipple();
                    handler.postDelayed(this,2000);

                    Log.i(log, "Bluetooth module error");
                }

            }
        }, 1500);

    }

    public void startRipple(){
        if(!rippleBackground.isRippleAnimationRunning()){
            rippleBackground.startRippleAnimation();
        }
    }

    public void stopRipple(){
        if(rippleBackground.isRippleAnimationRunning()){
            rippleBackground.stopRippleAnimation();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_settings, container, false);
        enterPairBtn = (Button)view.findViewById(R.id.btn_enter_pairing);
        cancelPairBtn = (Button)view.findViewById(R.id.btn_cancel_pairing);
        rippleBackground=(RippleBackground)view.findViewById(R.id.content);
        autoConnectTbtn = (ToggleButton) view.findViewById(R.id.tbtn_auto_connect);
        autoAnswerTbtn = (ToggleButton) view.findViewById(R.id.tbtn_auto_answer);
        imageView=(ImageView)view.findViewById(R.id.centerImage);
        connectedPhoneImg= (ImageView)view.findViewById(R.id.connected_phone);
        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void initSetOnClick(){
        enterPairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpioUart.sendData("blt-stn-pon?");
                connectionStatus();
                threadStatus = true;


            }
        });

        cancelPairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpioUart.sendData("blt-stn-pof?");
                handler.removeCallbacksAndMessages(null);
                rippleBackground.stopRippleAnimation();
                prefManager.setBluetoothPlayerState(false);



            }
        });

        autoConnectTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    gpioUart.sendData("blt-stn-eac?");
                }else {
                    gpioUart.sendData("blt-stn-dac?");
                }
            }
        });

        autoAnswerTbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    gpioUart.sendData("blt-stn-ean?");
                }else {
                    gpioUart.sendData("blt-stn-dan?");
                }
            }
        });


    }



}
