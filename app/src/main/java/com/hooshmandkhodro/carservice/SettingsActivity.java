package com.hooshmandkhodro.carservice;

import android.content.ClipData;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.hooshmandkhodro.carservice.app.AudioValues;
import com.hooshmandkhodro.carservice.app.PrefManager;
import com.hooshmandkhodro.carservice.app.ToolBar_ResideMenu;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;


import com.hooshmandkhodro.carservice.app.GpioUart;
import com.hooshmandkhodro.carservice.app.dagger.App;

import javax.inject.Inject;


public class SettingsActivity extends AppCompatActivity implements View.OnTouchListener,
        View.OnDragListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    @Inject
    PrefManager prefManager;

    private ImageView audioBalanceImg;
    private GpioUart gpioUart;
    private BoxedVertical basSeekbar, trebleSeekbar, gainSeekbar;
    private ViewGroup relativeLayout = null;
    private Rect relativeLayoutRect = null;
    private boolean isCoordinatesInit_Level1 = false;

    private ImageButton balanceUpBtn, balanceDownBtn, balanceRightBtn, balanceLeftBtn;
    private Handler handlerSetSpeakersData;
    private SwitchCompat loudSwitch, amplifire, soundLimitSwitch;
    private Button resetBalance, frontBalance, driverBalance, rearBalance;
    /**
     * -----Previuos config 1280*672
     * private double X_axislength =  230.0;
     * private double Y_axislength = 310.0;
     * private int startLeftMargin = 100;
     * private int startTopMargin = 50;
     * public static float defaultX = 249 - 32;
     * public static float defaultY = 215 - 32;
     * private int imgSize = 32;
     * private int step = 25;
     */
    private double X_axislength = 180.0;
    private double Y_axislength = 250.0;
    private int startLeftMargin = 70;
    private int endRightMargin = 230;
    private int startTopMargin = 10;
    private int endBottomMargin = 250;
    public static float defaultX = 195 - 32;
    public static float defaultY = 170 - 32;
    private int imgSize = 32;
    private int step = 20;
    ToolBar_ResideMenu toolBarResideMenu;
    private AudioValues audioValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ((App)getApplicationContext()).getComponent().inject(this);

        initObjectsView();
//
        audioValues = new AudioValues(prefManager);

            gpioUart = new GpioUart(1);


        toolBarResideMenu = new ToolBar_ResideMenu(SettingsActivity.this, "Audio Settings", gpioUart, prefManager);
        toolBarResideMenu.resideMenuInit("Home", "Bluetooth", "Radio",
                R.drawable.icon_home, R.drawable.icon_home, R.drawable.icon_home, MainActivity.class
                , BluetoothActivity.class, RadioActivity.class, "Settings");
        basSeekbar.setValue(prefManager.getVolumeValue(5));
        trebleSeekbar.setValue(prefManager.getVolumeValue(6));
        gainSeekbar.setValue(prefManager.getVolumeValue(8));
        //checkResolution();
        initAudioEQsettings();
    }

    private boolean checkResolution() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        double x = Math.pow(width / displayMetrics.xdpi, 2);
        double y = Math.pow(height / displayMetrics.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        if (width != 1280 && height != 672) {
            defaultX = 220 - 32;
            defaultY = 222 - 32;
            return false;
        } else return true;

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /**
         * Sound Balance on touch
         * Front Left min value 223 max value 192
         * Front Right min value 255 max value 224
         * Rear Left min value 159 max value 128
         * Rear Right min value 191 max value 160
         * */


        boolean result = false;

        if (v.getId() == R.id.relative_layout)
            result = relativeLayoutOnTouch(v, event);
        else if (v.getId() == R.id.audio_balance_img)
            result = audBalanceObjectOnTouch(v, event);
        return result;
    }

    private boolean audBalanceObjectOnTouch(View v, MotionEvent event) {

        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
        v.startDrag(data, shadowBuilder, v, 0);

        return true;
    }

    private boolean relativeLayoutOnTouch(View v, MotionEvent event) {
        if (isCoordinatesInit_Level1 == false) {
            relativeLayoutRect = new Rect(0, 0, relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight());
            isCoordinatesInit_Level1 = true;
        }

        if (relativeLayoutRect.contains((int) event.getX(), (int) event.getY())) {
            audioBalanceImg.animate()
                    .x(event.getX() - imgSize)
                    .y(event.getY() - imgSize)
                    .setDuration(10)
                    .start();
            checkStateOfImageBalance(event.getX() - imgSize, event.getY() - imgSize);


        }

        return true;
    }

    private void checkStateOfImageBalance(float getX, float getY) {
        if (getX < startLeftMargin || getY < startTopMargin || getX > endRightMargin || getY > endBottomMargin) {
            audioBalanceImg.animate()
                    .x(defaultX) //if touch out of the target area the image return to center
                    .y(defaultY)
                    .setDuration(0)
                    .start();
            prefManager.setXYcordinates(defaultX, defaultY);

            speakersInit(100, 100, 100, 100);
        } else {
            calculateBalanceData(getX, getY);
            prefManager.setXYcordinates(getX, getY);
        }
    }

    private void changeStateOfImageBalanceWithDirectionalButtons(float getX, float getY) {
        if (getX < startLeftMargin || getY < startTopMargin || getX > endRightMargin || getY > endBottomMargin) {
            return;
        } else {
            audioBalanceImg.animate()
                    .x(getX)
                    .y(getY)
                    .setDuration(0)
                    .start();
            calculateBalanceData(getX, getY);
            prefManager.setXYcordinates(getX, getY);
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        View view = (View) event.getLocalState();


        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (view.getId() == R.id.audio_balance_img)
                    Log.i(TAG, "onDrag: " + "Started");

                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                if (view.getId() == R.id.audio_balance_img)
                    Log.i(TAG, "onDrag: " + "Entered");
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                if (view.getId() == R.id.audio_balance_img)
                    //Log.i(TAG, "onDrag: " + "Location_"+"x:"+event.getX()+" y: "+event.getY());


                    break;
            case DragEvent.ACTION_DRAG_EXITED:
                if (view.getId() == R.id.audio_balance_img)
                    Log.i(TAG, "onDrag: " + "Exited");
                break;
            case DragEvent.ACTION_DROP:
                if (view.getId() == R.id.audio_balance_img)
                    audioBalanceImg.animate()
                            .x(event.getX() - imgSize)
                            .y(event.getY() - imgSize)
                            .setDuration(0)
                            .start();
                checkStateOfImageBalance(event.getX() - imgSize, event.getY() - imgSize);


                break;
            case DragEvent.ACTION_DRAG_ENDED:
                if (view.getId() == R.id.audio_balance_img)
                    Log.i(TAG, "onDrag: " + "Ended");

                break;
            default:
                return false;
        }
        return true;
    }

    private void calculateBalanceData(double xtl, double ytl) {
        double xfl, yfl, xfr, yrl, result;
        int fl, fr, rr, rl;
        /**
         * meghdar mehvar X va Y ke tasvir dar AN harekat mikonad
         */
        xfl = (int) ((xtl) - startLeftMargin) * (11.0 / X_axislength);
        yfl = (int) ((ytl) - startTopMargin) * (11.0 / Y_axislength);
        xfr = (int) (10 - xfl);
        yrl = (int) (10 - yfl);

        Log.i(TAG, "X: " + xtl);
        Log.i(TAG, "Y: " + ytl);

        //Front left
        if (xfl <= 5 && yfl <= 5) {
            //FL = 100
            fl = 100;
        } else {
            fl = (int) (100 - (20 * (Math.max(xfl, yfl) - 5)));
        }

        Log.i(TAG, "FL: " + fl);

        //Front right
        if (xfr <= 5 && yfl <= 5) {
            fr = 100;
        } else {
            fr = (int) (100 - (20 * (Math.max(xfr, yfl) - 5)));
        }
        Log.i(TAG, "FR: " + fr);

        //Rear Left
        if (xfl <= 5 && yrl <= 5) {
            rl = 100;
        } else {
            rl = (int) (100 - (20 * (Math.max(xfl, yrl) - 5)));
        }
        Log.i(TAG, "RL: " + rl);

        //Rear Right
        if (xfr <= 5 && yrl <= 5) {
            rr = 100;
        } else {
            rr = (int) (100 - (20 * (Math.max(xfr, yrl) - 5)));
        }
        Log.i(TAG, "RR: " + rr);

        speakersInit(fl, fr, rl, rr);
    }

    private void speakersInit(int frontLeft, int frontRight, int rearLeft, int rearRight) {
        final int fl, fr, rr, rl;


        fl = checkPercent(frontLeft);
        fr = checkPercent(frontRight);
        rl = checkPercent(rearLeft);
        rr = checkPercent(rearRight);
        audioValues.setVolumeLeftFront(rl);
        audioValues.setVolumeRightFront(rr);
        audioValues.setVolumeLeftRear(fl);
        audioValues.setVolumeRightRear(fr);

        Log.d(TAG, " fl : " + fl + " fr : " + fr + " rl : " + rl + " rr : " + rr);
        sendData(audioValues.getAudioValues());
    }


    private int checkPercent(int value) {
        int volume = 31;
        if (value >= 0 && value <= 20) {
            volume = 0;
            return volume;
        } else if (value > 20 && value <= 40) {
            volume = 7;
            return volume;
        } else if (value > 40 && value <= 60) {
            volume = 14;
            return volume;
        } else if (value > 60 && value <= 80) {
            volume = 22;
            return volume;
        } else if (value > 80 && value <= 100) {
            volume = 31;
            return volume;
        }
        return volume;
    }

    private void initObjectsView() {
        amplifire = findViewById(R.id.amplifire_switch);
        loudSwitch = findViewById(R.id.loudness_switch);
        soundLimitSwitch = findViewById(R.id.sound_limit_switch);
        audioBalanceImg = findViewById(R.id.audio_balance_img);
        audioBalanceImg.setOnTouchListener(this);
        balanceUpBtn = findViewById(R.id.balance_up_imgbtn);
        balanceDownBtn = findViewById(R.id.balance_down_imgbtn);
        balanceRightBtn = findViewById(R.id.balance_right_imgbtn);
        balanceLeftBtn = findViewById(R.id.balance_left_imgbtn);


        //on click audio balance
        relativeLayout = findViewById(R.id.relative_layout);
        relativeLayout.setOnTouchListener(this);
        relativeLayout.setOnDragListener(this);
        basSeekbar = findViewById(R.id.base_vertical_seekbar);
        trebleSeekbar = findViewById(R.id.treble_vertical_seekbar);
        gainSeekbar = findViewById(R.id.gain_vertical_seekbar);

        resetBalance = findViewById(R.id.reset_button);
        driverBalance = findViewById(R.id.driver_button);
        frontBalance = findViewById(R.id.front_button);
        rearBalance = findViewById(R.id.rear_button);
        initButtons();
    }

    private void setSound() {

        if (prefManager.getAUXAudioIsActive()) {
            sendData(audioValues.auxMode());
            return;
        } else if (prefManager.getRadioIsRun()) {
            sendData(audioValues.radioMode());
            return;
        } else sendData(audioValues.androidBTMode());
    }


    private void initAudioEQsettings() {

        soundLimitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefManager.setVolumeValue(13,55);
                }else prefManager.setVolumeValue(13,63);
            }
        });

        amplifire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefManager.setAmplifireState(b);
                if (!b)
                    sendData("oth-amp-000?");
            }
        });

        loudSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                audioValues.loudState(b);
                setSound();

                if (prefManager.getDebugModeState())
                    Toast.makeText(SettingsActivity.this, "" + prefManager.getVolumeValue(11), Toast.LENGTH_SHORT).show();


            }
        });


        loudSwitch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                boolean i = prefManager.getDebugModeState();
                if (i) {
                    Toast.makeText(SettingsActivity.this, "Debug Off", Toast.LENGTH_SHORT).show();
                    prefManager.setDebugModeState(false);
                } else {
                    Toast.makeText(SettingsActivity.this, "Debug On", Toast.LENGTH_SHORT).show();
                    prefManager.setDebugModeState(true);
                }
                return false;
            }
        });
        basSeekbar.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedVertical, int i) {
                audioValues.setBaseValue(i);
                sendData(audioValues.getAudioValues());
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedVertical) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedVertical) {
                if (prefManager.getDebugModeState())
                    Toast.makeText(SettingsActivity.this, "" + prefManager.getVolumeValue(11), Toast.LENGTH_SHORT).show();

            }
        });

        trebleSeekbar.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedVertical, int i) {
                audioValues.setTrebleValue(i);
                sendData(audioValues.getAudioValues());
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedVertical) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedVertical) {
                if (prefManager.getDebugModeState())
                    Toast.makeText(SettingsActivity.this, "" + prefManager.getVolumeValue(11), Toast.LENGTH_SHORT).show();

            }
        });

        gainSeekbar.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedVertical, int i) {
                audioValues.setGainValue(i);
                setSound();
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedVertical) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedVertical) {
                if (prefManager.getDebugModeState())
                    Toast.makeText(SettingsActivity.this, "" + prefManager.getVolumeValue(11), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initButtons() {
        resetBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStateOfImageBalance(0, 0);
            }
        });
        resetBalance.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                checkStateOfImageBalance(0, 0);
                audioValues.loudState(false);
                loudSwitch.setChecked(false);
                basSeekbar.setValue(0);
                trebleSeekbar.setValue(0);
                gainSeekbar.setValue(0);
                return false;
            }
        });

        frontBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chacgeBalanceWithButtons(defaultX, startTopMargin);
            }
        });

        driverBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chacgeBalanceWithButtons(startLeftMargin, startTopMargin);
            }
        });

        rearBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chacgeBalanceWithButtons(defaultX, endBottomMargin - 12);
            }
        });

        balanceUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStateOfImageBalanceWithDirectionalButtons(prefManager.getXcordinate(), prefManager.getYcordinate() - step);
            }
        });
        balanceDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStateOfImageBalanceWithDirectionalButtons(prefManager.getXcordinate(), prefManager.getYcordinate() + step);
            }
        });
        balanceRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStateOfImageBalanceWithDirectionalButtons(prefManager.getXcordinate() - step, prefManager.getYcordinate());
            }
        });
        balanceLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStateOfImageBalanceWithDirectionalButtons(prefManager.getXcordinate() + step, prefManager.getYcordinate());
            }
        });

    }

    private void chacgeBalanceWithButtons(float getX, float getY) {

        audioBalanceImg.animate()
                .x(getX)
                .y(getY)
                .setDuration(0)
                .start();
        calculateBalanceData(getX, getY);
        prefManager.setXYcordinates(getX, getY);

    }

    private void sendData(final String data) {

        handlerSetSpeakersData = new Handler();
        handlerSetSpeakersData.postDelayed(new Runnable() {
            @Override
            public void run() {
                gpioUart.sendData(data);
                handlerSetSpeakersData.removeCallbacksAndMessages(this);
            }
        }, 100);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (prefManager.getVolumeValue(7) != 0)
            loudSwitch.setChecked(true);
        else
            loudSwitch.setChecked(false);

        if (prefManager.getAmplifireState())
            amplifire.setChecked(true);
        else
            amplifire.setChecked(false);

        if (prefManager.getVolumeValue(13) == 55)
            soundLimitSwitch.setChecked(true);
        else soundLimitSwitch.setChecked(false);


        //checkResolution();
        audioBalanceImg.animate()
                .x(prefManager.getXcordinate())
                .y(prefManager.getYcordinate())
                .setDuration(100)
                .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
    }
}
