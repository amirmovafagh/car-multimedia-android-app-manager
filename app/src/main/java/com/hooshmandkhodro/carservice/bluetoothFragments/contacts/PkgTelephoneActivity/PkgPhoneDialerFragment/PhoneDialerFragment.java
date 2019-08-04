package com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hooshmandkhodro.carservice.R;
import com.skyfishjy.library.RippleBackground;

import java.util.Timer;
import java.util.TimerTask;



import com.hooshmandkhodro.carservice.app.GpioUart;
import com.hooshmandkhodro.carservice.app.MyAudioManager;

import static com.hooshmandkhodro.carservice.MyHandler.buffer;
import static com.hooshmandkhodro.carservice.TelephoneActivity.connectGpioUart;
import static com.hooshmandkhodro.carservice.UsbService.threadStatus;
import static com.hooshmandkhodro.carservice.UsbService.handlerDelay;


public class PhoneDialerFragment extends Fragment implements RequiredViewOps, View.OnClickListener {
    private static final String tag = PhoneDialerFragment.class.getSimpleName();
    public static boolean dialFragmentIsRun = false;
    private Context ctx;
    private ProvidedPresenterOps presenterOps;

    private View view, dialpadView;
    private RippleBackground rippleBackground;
    private ImageView imageView;
    private Button numPadBtn;
    private TextView txtNumber, txtCallSituation, txtCallTimer;
    private ImageButton upVolume, downVolume, mute, btConnection, backSpaceBtn, imgBtnDialPad;
    private boolean checkCall, isVisible = false;
    private long startTime = 0;
    private GpioUart gpioUart;
    private Handler callStatusHandler, handlerData, handlerMakeCall;
    private Runnable runnableCallStatus;
    boolean checkCallGetNumber = false;
    boolean checkTimer = false;
    private boolean incomingCallStatus = false;
    private MaterialCardView cardViewControllers;
    private boolean outCallStatus = false;
    private boolean onCallStatus = false;
    private MyAudioManager audioManager;

    public PhoneDialerFragment() {
        dialFragmentIsRun = true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        gpioUart = connectGpioUart;
        dialFragmentIsRun = true;
        ctx = getContext();
        presenterOps = new PhoneDialerPresenter(ctx, this);
        audioManager = new MyAudioManager(context);
        if (audioManager.isMusicPlay()) {
            audioManager.pauseHeadUnitMusicPlayer();
        }
    }

    //runs without a timer by reposting this handler at the end of the runnable
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            txtCallTimer.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    private void startTimer() {
        if (!checkTimer) {
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            checkTimer = true;
        }

    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        txtCallTimer.setText("0:00");
        checkTimer = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialFragmentIsRun = true;
        isVisible = false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        timerHandler = new Handler();
        handlerMakeCall = new Handler();
        audioManager = new MyAudioManager(getContext());

        audioManager.pauseHeadUnitMusicPlayer();
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            String receivedNumber = bundle.getString("num", ""); // Key, default value
            txtNumber.setText(receivedNumber);
            makeCall();
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dial, container, false);
        dialpadView = view.findViewById(R.id.dialpad_view);
        rippleBackground = view.findViewById(R.id.content);
        imageView = view.findViewById(R.id.centerImage);
        txtNumber = view.findViewById(R.id.txt_number);
        txtCallSituation = (TextView) view.findViewById(R.id.txt_call_situation);
        txtCallTimer = (TextView) view.findViewById(R.id.txt_call_timer);
        upVolume = (ImageButton) view.findViewById(R.id.ib_up_volume);
        upVolume.setOnClickListener(this);
        downVolume = (ImageButton) view.findViewById(R.id.ib_down_volume);
        downVolume.setOnClickListener(this);
        mute = (ImageButton) view.findViewById(R.id.ib_mute);
        mute.setOnClickListener(this);
        btConnection = (ImageButton) view.findViewById(R.id.ib_bluetooth_connection_call);
        btConnection.setOnClickListener(this);
        cardViewControllers = view.findViewById(R.id.card_view_controllers);
        invisibleObj();

        final int[] dialpadBtnsID = {R.id.btn_star, R.id.btn_sharp, R.id.zero, R.id.one, R.id.two,
                R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine};

        for (int i = 0; i < dialpadBtnsID.length; i++) {
            numPadBtn = view.findViewById(dialpadBtnsID[i]);
            numPadBtn.setOnClickListener(this);
        }

        final int[] imgBtnID = {R.id.btn_end_call, R.id.btn_call, R.id.backspace};
        for (int i = 0; i < imgBtnID.length; i++) {
            imgBtnDialPad = view.findViewById(imgBtnID[i]);
            imgBtnDialPad.setOnClickListener(this);
        }

        backSpaceBtn = (ImageButton) view.findViewById(R.id.backspace);
        checkCallStatus();

        return view;

    }

    private void saveInComingCall(String number) {

        presenterOps.clickedOnCallRecivedButton(number);
    }

    private void saveOutGoingCall(String number) {

        presenterOps.clickedOnCallMadeButton(number);
    }

    private void saveMissCall(String number) {
        presenterOps.clickedOnCallMissedButton(number);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_star:
                if (!checkCall) {
                    txtNumber.append("*");
                }
                break;
            case R.id.btn_sharp:
                if (!checkCall) {
                    txtNumber.append("#");
                }
                break;
            case R.id.zero:
                if (!checkCall) {
                    txtNumber.append("0");
                }
                break;
            case R.id.one:
                if (!checkCall) {
                    txtNumber.append("1");
                }
                break;
            case R.id.two:
                if (!checkCall) {
                    txtNumber.append("2");
                }
                break;
            case R.id.three:
                if (!checkCall) {
                    txtNumber.append("3");
                }
                break;
            case R.id.four:
                if (!checkCall) {
                    txtNumber.append("4");
                }
                break;
            case R.id.five:
                if (!checkCall) {
                    txtNumber.append("5");
                }
                break;
            case R.id.six:
                if (!checkCall) {
                    txtNumber.append("6");
                }
                break;
            case R.id.seven:
                if (!checkCall) {
                    txtNumber.append("7");
                }
                break;
            case R.id.eight:
                if (!checkCall) {
                    txtNumber.append("8");
                }
                break;
            case R.id.nine:
                if (!checkCall) {
                    txtNumber.append("9");
                }
                break;
            case R.id.btn_call:
                if (incomingCallStatus) {
                    gpioUart.sendData("blt-cll-ans");
                    incomingCallStatus = false;
                } else

                    makeCall();

                break;
            case R.id.btn_end_call:
                if (incomingCallStatus) {
                    gpioUart.sendData("blt-cll-rjt");
                    incomingCallStatus = false;
                } else
                    endCall();
                break;
            case R.id.backspace:

                String s = txtNumber.getText().toString();
                if (!s.isEmpty() && !checkCall) {
                    s = s.substring(0, s.length() - 1);
                    txtNumber.setText(s);


                }

                break;

            case R.id.ib_down_volume:
                sendCommand("blt-mus-vdn");
                Log.i(tag, "down");
                break;
            case R.id.ib_up_volume:
                sendCommand("blt-mus-vup");
                break;
            case R.id.ib_mute:
                sendCommand("blt-mus-mut");
                break;
            case R.id.ib_bluetooth_connection_call:
                sendCommand("blt-cll-atr");

        }
        backSpaceBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String s = txtNumber.getText().toString();
                if (!s.isEmpty() && !checkCall) {
                    txtNumber.setText(null);
                }

                return false;
            }
        });
    }

    //enable ripple circle
    public void startRipple() {
        if (!rippleBackground.isRippleAnimationRunning()) {
            rippleBackground.startRippleAnimation();
        }
    }

    //disable ripple circle
    public void stopRipple() {
        if (rippleBackground.isRippleAnimationRunning()) {
            rippleBackground.stopRippleAnimation();
        }
    }

    private void delayTimer(final String data) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                gpioUart.sendData(data);
            }
        }, 100);
    }

    private void setNameContactNumberCalling() {
        String contactName = presenterOps.ifThereInContacts(txtNumber.getText().toString());
        if (contactName != "non") {
            txtNumber.setText(contactName);
        }
    }

    public Boolean makeCall() {

        /**
         * in function barghari tamas anjam mishavad
         * */

        if (!txtNumber.getText().toString().isEmpty() && !checkCall) {
            threadStatus = false;//connectUsbService.disableCheckCallStatus(); //turn off usb service checker bluetooth
            stopTimer();
            handlerMakeCall.postDelayed(new Runnable() {
                @Override
                public void run() {
                    delayTimer("blt-cll-" + txtNumber.getText().toString() + "?");
                }
            }, 100);


            handlerMakeCall.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (buffer.equalsIgnoreCase("IR")) {
                        //this.SaveCallInfoLog(phone.getText().toString(), new CallType(2,CallType.Type.OUTPUT));
                        //Toast.makeText(getActivity(), "output call", Toast.LENGTH_SHORT).show();
                        //startTimer();
                        //BtnCallOutputOnClick();
                        //saveOutGoingCall(txtNumber.getText().toString());
                        visibleObj("Calling out...");
                        saveOutGoingCall(txtNumber.getText().toString());

                        setNameContactNumberCalling();
                        startRipple();
                        startTimer();
                        outCallStatus = true;
                        threadStatus = true;//connectUsbService.enableCheckCallStatus(); //turn on usb service checker bluetooth
                    } else {
                        threadStatus = true;//connectUsbService.enableCheckCallStatus();
                        Toast.makeText(getActivity(), "خطا در خواندن اطلاعات بلوتوث", Toast.LENGTH_SHORT).show();
                        Log.i(tag, "outgoing call read buffer ERROR");
                        outCallStatus = false;
                        endCall();
                    }
                }
            }, 400);
            //checkEndCallStatus();
            checkCall = true;
        } else {
            Toast.makeText(getActivity(), "Please Enter Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void endCall() {

        gpioUart.sendData("blt-cll-end?");
        threadStatus = true;//connectUsbService.enableCheckCallStatus();
        checkCall = false;
        stopRipple();
        txtCallSituation.setText("");
        txtNumber.setText("");
        stopTimer();
        invisibleObj();

    }

    public void endCall2() {
        //connectUsbService.write("blt-cll-end?");
        threadStatus = true;//connectUsbService.enableCheckCallStatus();
        checkCall = false;
        rippleBackground.stopRippleAnimation();
        txtCallSituation.setText("");
        txtNumber.setText("");
        stopTimer();
        invisibleObj();

    }

    private void checkCallStatus() {
        /**
         * in function bA ejraye activity ba method on create farakhani shode
         * ba handler vazeiat mokaleme ke az noe tamas vorudi, khoruji, payan tamas ra
         * moshakhas mikonad
         */

        callStatusHandler = new Handler();
        runnableCallStatus = new Runnable() {
            @Override
            public void run() {
                handlerDelay = 2500;//connectUsbService.changeDelay(2500);
                String number = "";
                try {
                    number = String.valueOf(buffer.charAt(0));
                } catch (Exception e) {
                }


                if (number.equalsIgnoreCase("0") || number.equalsIgnoreCase("+")) {

                    String pnumber = buffer;
                    if (pnumber.length() > 4) {
                        //dar surati ke buffer ersali az mcu daray harf k bud an ra hazf konad
                            /*char lastChar = pnumber.charAt(pnumber.length()) ;
                            if( lastChar == 'k' ){
                                pnumber = pnumber.trim();
                                pnumber = pnumber.substring(0, pnumber.length()-2);
                                txtNumber.setText(pnumber);
                            }else {

                            }*/
                        pnumber = pnumber.trim();
                        setNameContactNumberCalling();
                        txtNumber.setText(pnumber);
                        if (!incomingCallStatus)
                            saveInComingCall(pnumber);
                    }

                    visibleObj("Incoming Call...");
                    startRipple();

                    incomingCallStatus = true;
                    checkCallGetNumber = true;

                }

                if (buffer.equalsIgnoreCase("MG4")) {
                    buffer = "";
                    visibleObj("Outgoing Call...");
                    startRipple();
                    stopTimer();
                    outCallStatus = true;

                    checkCallGetNumber = true;
                    Log.i(tag, "Outgoing Call...");
                }
                if (buffer.equalsIgnoreCase("MG5")) {
                    buffer = "";
                }
                //dar soorati ke tamas az noe vorudi ya khoruji bargharar shavad in shart ejra mishavad
                if (checkCallGetNumber) {
                    if (buffer.equalsIgnoreCase("MG6")) {
                        buffer = "";
                        startTimer();
                        txtCallSituation.setText("On Call...");
                        incomingCallStatus = false;
                        outCallStatus = false;
                    } else if (buffer.equalsIgnoreCase("MG3")) {
                        buffer = "";
                        Toast.makeText(getActivity(), "end call", Toast.LENGTH_SHORT).show();
                        Log.i(tag, "end call");
                        endCall2();
                        stopTimer();
                        incomingCallStatus = false;
                        outCallStatus = false;
                        checkCallGetNumber = false;
                    } else if (buffer.equalsIgnoreCase("MG2") || buffer.equalsIgnoreCase("MG1")) {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        buffer = "";
                        Log.i(tag, "Error");
                        endCall2();
                        stopTimer();
                        incomingCallStatus = false;
                        outCallStatus = false;
                        checkCallGetNumber = false;
                    }

                }
                callStatusHandler.postDelayed(runnableCallStatus, 1000);
            }
        };
        //incomingCallHandler.postDelayed(runnableIncomingCall, 0);
    }

    private void sendCommand(final String data) {
        threadStatus = false;//connectUsbService.disableCheckCallStatus();
        handlerData = new Handler();
        handlerData.postDelayed(new Runnable() {
            @Override
            public void run() {
                gpioUart.sendData(data + "?");
                threadStatus = true;//connectUsbService.enableCheckCallStatus();
            }
        }, 100);
    }

    private void invisibleObj() {
        txtCallSituation.setVisibility(View.INVISIBLE);
        txtCallTimer.setVisibility(View.INVISIBLE);
        rippleBackground.setVisibility(View.INVISIBLE);
        cardViewControllers.setVisibility(View.INVISIBLE);
        mute.setVisibility(View.INVISIBLE);
        upVolume.setVisibility(View.INVISIBLE);
        downVolume.setVisibility(View.INVISIBLE);
        btConnection.setVisibility(View.INVISIBLE);
    }

    private void visibleObj(String CallSituation) {
        txtCallSituation.setVisibility(View.VISIBLE);
        txtCallSituation.setText(CallSituation);
        txtCallTimer.setVisibility(View.VISIBLE);
        rippleBackground.setVisibility(View.VISIBLE);
        cardViewControllers.setVisibility(View.VISIBLE);
        mute.setVisibility(View.VISIBLE);
        upVolume.setVisibility(View.VISIBLE);
        downVolume.setVisibility(View.VISIBLE);
        btConnection.setVisibility(View.VISIBLE);
    }


    public static PhoneDialerFragment newInstance() {

        Bundle args = new Bundle();

        PhoneDialerFragment fragment = new PhoneDialerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void showCallLogInsertSuccessfullyMessage() {
        //Toast.makeText(ctx, "callLog insert with successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCallLogNotInsertMessage() {
        //Toast.makeText(ctx, "callLog not insert", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        dialFragmentIsRun = true;
        callStatusHandler.postDelayed(runnableCallStatus, 0);
        if (audioManager.isMusicPlay()) {
            audioManager.pauseHeadUnitMusicPlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerDelay = 1000;//connectUsbService.changeDelay(1000);
        dialFragmentIsRun = false;
        callStatusHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = this.getArguments();
//        connectUsbService.changeDelay(2500);
        if (bundle != null) {
            String receivedNumber = bundle.getString("num", ""); // Key, default value
            txtNumber.setText(receivedNumber);
            makeCall();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        handlerDelay = 500; //connectUsbService.changeDelay(500);
        dialFragmentIsRun = false;
        callStatusHandler.removeCallbacksAndMessages(null);
    }
}
