package ir.dev_roid.testusb;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.special.ResideMenu.ResideMenu;

import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ir.dev_roid.testusb.app.AudioValues;
import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.CustomDialog;
import ir.dev_roid.testusb.app.MyAudioManager;
import ir.dev_roid.testusb.app.PrefManager;
import ir.dev_roid.testusb.app.RadioValues;
import ir.dev_roid.testusb.app.ToolBar_ResideMenu;
import ir.dev_roid.testusb.radio.DatabaseHelper;
import ir.dev_roid.testusb.radio.RadioChannelAM;
import ir.dev_roid.testusb.radio.RadioChannelFM;
import ir.dev_roid.testusb.radio.recyclerViewAutoSearch.ChannelFrequency;
import ir.dev_roid.testusb.radio.recyclerViewAutoSearch.ChannelFrequencyAdapter;

import static ir.dev_roid.testusb.MyHandler.buffer;

public class RadioActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String tag = RadioActivity.class.getSimpleName();
    private List<ChannelFrequency> channelFrequenciesList = new ArrayList<>();
    private ChannelFrequency channelFrequency;
    private List<RadioChannelFM> channelsFM;
    private List<RadioChannelAM> channelsAM;
    private RecyclerView recyclerView;
    private ChannelFrequencyAdapter frequencyAdapter;
    private ProgressDialog autoSearchDialog;


    private ImageButton nextImgBtn, previousImgBtn;
    private ImageView modeSwitch,autoSearch,soundGain;
    private TextView toolbarTextView, txtFrequency;
    private ResideMenu resideMenu;
    private IndicatorSeekBar frqSeekbar;
    private Button favRadioButton, favBtn1, favBtn2, favBtn3, favBtn4, favBtn5, favBtn6;
    private PrefManager prefManager;
    private CustomDialog dialog;
    private Float frequencyFM;
    private int frequencyAM;
    private ToolBar_ResideMenu toolBarResideMenu;
    private MyAudioManager audioManager;
    private AudioValues audioValues;
    private RadioValues radioValues;
    private boolean AMFMmode;
    private DatabaseHelper databaseHelper;
    final int[] favBtnRadioIDs = {R.id.fav_radio_btn1, R.id.fav_radio_btn2, R.id.fav_radio_btn3, R.id.fav_radio_btn4,
            R.id.fav_radio_btn5, R.id.fav_radio_btn6};

    private ConnectUsbService connectUsbService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        databaseHelper = DatabaseHelper.getInstance(this);
        autoSearchDialog = new ProgressDialog(RadioActivity.this,R.style.AppCompatAlertDialogStyle);


        prefManager = new PrefManager(this);
        radioValues = new RadioValues(prefManager);
        getRadioMode();
        prefManager.setRadioIsRun(true);
        connectUsbService = new ConnectUsbService(RadioActivity.this);
        audioManager = new MyAudioManager(this);
        audioManager.pauseHeadUnitMusicPlayer();
        audioValues = new AudioValues(prefManager);
        sendData(audioValues.radioMode(), 0);
        initView();
        onLongClick();

        setRecyclerView();



        nextImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!AMFMmode){
                    frequencyAM += 1;
                    frqSeekbar.setProgress(frequencyAM);
                }else {
                    frequencyFM += (float) 0.1;
                    frqSeekbar.setProgress(frequencyFM);
                }

            }
        });

        previousImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!AMFMmode){
                    frequencyAM -= 1;
                    frqSeekbar.setProgress(frequencyAM);
                }else {
                    frequencyFM -= (float) 0.1;
                    frqSeekbar.setProgress(frequencyFM);
                }
            }
        });

        frqSeekbar.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
                checkVoiceChannel();
                if(!AMFMmode){
                    //AM
                    frequencyAM = progress;
                    txtFrequency.setText(String.valueOf(frequencyAM));
                    radioValues.setFrequencyAM(frequencyAM);
                }else {
                    //FM
                    frequencyFM = progressFloat;
                    txtFrequency.setText(String.valueOf(frequencyFM));
                    radioValues.setFrequencyFM(frequencyFM);
                }


                //Toast.makeText(RadioActivity.this, ""+stringValue, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                connectUsbService.write(radioValues.getRadioManualSearchValues());
            }
        });

    }

    private void getRadioMode() {
        if(radioValues.getMode()==0){
            //AM
            AMFMmode = false;
        }else {
            //FM
            AMFMmode=true;
        }
    }

    private void setRecyclerView() {
        frequencyAdapter = new ChannelFrequencyAdapter(channelFrequenciesList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(frequencyAdapter);

        if(!AMFMmode){
            readAMtableSetInRec();
        }else {
            readFMtableSetInRec();
        }

    }

    private void readFMtableSetInRec() {
        if(!channelFrequenciesList.isEmpty()){channelFrequenciesList.clear();}

        channelsFM = databaseHelper.getAllRadioChannelsFM(); //read frequenies from FM table
        for (RadioChannelFM rc : channelsFM) {
            float i = Float.parseFloat(rc.getFrq());
            channelFrequency = new ChannelFrequency(String.valueOf(i / 20));
            channelFrequenciesList.add(channelFrequency);
        }
        frequencyAdapter.notifyDataSetChanged();
    }

    private void readAMtableSetInRec() {
        if(!channelFrequenciesList.isEmpty()){channelFrequenciesList.clear();}

        channelsAM = databaseHelper.getAllRadioChannelsAM(); //read frequenies from AM table
        for (RadioChannelAM rc : channelsAM) {
            float i = Float.parseFloat(rc.getFrq());
            channelFrequency = new ChannelFrequency(String.valueOf(i / 20));
            channelFrequenciesList.add(channelFrequency);
        }
        frequencyAdapter.notifyDataSetChanged();
    }

    private void favBtnFunction(int AM, int FM) {
        if (!AMFMmode) {
            //AM
            if (!prefManager.getFavoriteRadioFrequency(AM).equals("SAVE FREQUENCY")) {
                frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(AM)));
                txtFrequency.setText(prefManager.getFavoriteRadioFrequency(AM));
                prefManager.setRadioFrequencyAM(Integer.valueOf(prefManager.getFavoriteRadioFrequency(AM)));
            } else
                Toast.makeText(this, R.string.onClick_fav_radio_btn_channel_per, Toast.LENGTH_SHORT).show();
        } else {
            if (!prefManager.getFavoriteRadioFrequency(FM).equals("SAVE FREQUENCY")) {
                //FM
                frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(FM)));
                txtFrequency.setText(prefManager.getFavoriteRadioFrequency(FM));
                prefManager.setRadioFrequencyFM(Float.valueOf(prefManager.getFavoriteRadioFrequency(FM)));
            } else
                Toast.makeText(this, R.string.onClick_fav_radio_btn_channel_per, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {

        checkVoiceChannel();
        switch (view.getId()) {
            case R.id.am_fm_mode_image_view:
                changeMode();
                break;
            case R.id.auto_search_image_view:
                autoSearchFunction();
                break;
            case R.id.radio_sound_gain_image_view:
                soundGainFunction();
                break;
            case R.id.fav_radio_btn1:
                favBtnFunction(6, 0);

                break;
            case R.id.fav_radio_btn2:

                favBtnFunction(7, 1);
                break;
            case R.id.fav_radio_btn3:

                favBtnFunction(8, 2);
                break;
            case R.id.fav_radio_btn4:

                favBtnFunction(9, 3);
                break;
            case R.id.fav_radio_btn5:

                favBtnFunction(10, 4);
                break;
            case R.id.fav_radio_btn6:

                favBtnFunction(11, 5);

                break;
        }
    }

    private void soundGainFunction() {
        if(radioValues.getSoundGain()==0){
            radioValues.setSoundGain(1);
            soundGain.setImageResource(R.drawable.radio_sound_gain_on_96);
        }else {
            radioValues.setSoundGain(0);
            soundGain.setImageResource(R.drawable.radio_sound_gain_off_96);
        }
        connectUsbService.write(radioValues.getRadioManualSearchValues());
    }

    private void changeMode(){
        if (!AMFMmode) {
            radioValues.setMode(1);
            getRadioMode();
            setFrequencySeekbarMode(88,108,true);

            modeSwitch.setImageResource(R.drawable.fm_mode_96);
            readFMtableSetInRec();
            //FM
            for (int i = 0; i < 6; i++) {
                favRadioButton = findViewById(favBtnRadioIDs[i]);
                favRadioButton.setText(prefManager.getFavoriteRadioFrequency(i));
            }


        } else {
            //AM
            radioValues.setMode(0);
            getRadioMode();
            setFrequencySeekbarMode(540,1720,false);

            modeSwitch.setImageResource(R.drawable.am_mode_96);

            readAMtableSetInRec();
            for (int i = 0; i < 6; i++) {
                favRadioButton = findViewById(favBtnRadioIDs[i]);
                favRadioButton.setText(prefManager.getFavoriteRadioFrequency(i+6));
            }

        }

    }

    private void autoSearchFunction() {
        connectUsbService.write(radioValues.getRadioAutoSearchValues());
        autoSearchDialog.setMessage("SEARCHING, please wait...");
        autoSearchDialog.setCancelable(false);
        autoSearchDialog.show();
        final Handler handler = new Handler();
        if(radioValues.getMode()==0){
            //AM
            databaseHelper.removeAllRadioChannelsAM();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (buffer.contains("rad-")) {

                        //Toast.makeText(RadioActivity.this, buffer.substring(4), Toast.LENGTH_SHORT).show();
                        databaseHelper.insertRadioChannelsAM(buffer.substring(4));
                    }
                    Log.i("amir","runAM");
                    handler.postDelayed(this, 100);
                }
            }, 0);
        }else {
            //FM
            databaseHelper.removeAllRadioChannelsFM();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (buffer.contains("rad-") && Float.parseFloat(buffer.substring(4))!= 0) {

                        //Toast.makeText(RadioActivity.this, buffer.substring(4), Toast.LENGTH_SHORT).show();
                        databaseHelper.insertRadioChannelsFM(buffer.substring(4));
                    }
                    Log.i("amir","runFM");
                    handler.postDelayed(this, 10);
                }
            }, 0);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.removeMessages(0);
                setRecyclerView();
                autoSearchDialog.cancel();
            }
        },15000);

        /*Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {


            }
        }, 7000);*/
    }


    private void onLongClick() {


        favBtn1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                favBtnSaveFunction(6, 0,favBtn1);
                return false;

            }
        });

        favBtn2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                favBtnSaveFunction(7, 1,favBtn2);
                return false;
            }
        });

        favBtn3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                favBtnSaveFunction(8, 2,favBtn3);
                return false;
            }
        });

        favBtn4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                favBtnSaveFunction(9, 3,favBtn4);
                return false;
            }
        });

        favBtn5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                favBtnSaveFunction(10, 4,favBtn5);
                return false;
            }
        });

        favBtn6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                favBtnSaveFunction(11, 5,favBtn6);
                return false;
            }
        });
    }

    private void favBtnSaveFunction(int AM, int FM, Button favBtn) {
        if (radioValues.getMode() == 0) {
            //AM
            prefManager.setFavoriteRadioFrequency(AM, txtFrequency.getText().toString());
            favBtn.setText(prefManager.getFavoriteRadioFrequency(AM));
        } else {
            prefManager.setFavoriteRadioFrequency(FM, txtFrequency.getText().toString());
            favBtn.setText(prefManager.getFavoriteRadioFrequency(FM));
        }
        Toast.makeText(this, R.string.onLongClick_fav_radio_btn_channel_per, Toast.LENGTH_SHORT).show();
    }


    private void initView() {
        frqSeekbar = findViewById(R.id.seekbar_frequency);
        //indicatorSeekBar = new IndicatorSeekBar(this);

        recyclerView = findViewById(R.id.recycler_view);

        txtFrequency = findViewById(R.id.txt_frequency);
        previousImgBtn = findViewById(R.id.previous_img_btn);
        nextImgBtn = findViewById(R.id.next_img_btn);

        modeSwitch = findViewById(R.id.am_fm_mode_image_view);
        modeSwitch.setOnClickListener(this);
        soundGain = findViewById(R.id.radio_sound_gain_image_view);
        soundGain.setOnClickListener(this);
        autoSearch = findViewById(R.id.auto_search_image_view);
        autoSearch.setOnClickListener(this);

        //toolbarInit
        toolBarResideMenu = new ToolBar_ResideMenu(this, "Radio", connectUsbService, prefManager);
        //resideMenuInit
        toolBarResideMenu.resideMenuInit("Home", "Bluetooth", "Settings",
                R.drawable.icon_home, R.drawable.icon_home, R.drawable.icon_home,
                MainActivity.class, BluetoothActivity.class, SettingsActivity.class, "Radio");


        if (!AMFMmode) {
            setFrequencySeekbarMode(540,1720,false);
            modeSwitch.setImageResource(R.drawable.am_mode_96);

            //AM
            for (int i = 0; i < favBtnRadioIDs.length; i++) {
                favRadioButton = findViewById(favBtnRadioIDs[i]);
                favRadioButton.setText(prefManager.getFavoriteRadioFrequency(i + 6));
                favRadioButton.setOnClickListener(this);
            }

        } else {
            //FM
            setFrequencySeekbarMode(88,108,true);

            modeSwitch.setImageResource(R.drawable.fm_mode_96);
            for (int i = 0; i < favBtnRadioIDs.length; i++) {
                favRadioButton = findViewById(favBtnRadioIDs[i]);
                favRadioButton.setText(prefManager.getFavoriteRadioFrequency(i));
                favRadioButton.setOnClickListener(this);
            }
        }
        if(radioValues.getSoundGain()==0){
            soundGain.setImageResource(R.drawable.radio_sound_gain_off_96);
        }else soundGain.setImageResource(R.drawable.radio_sound_gain_on_96);


        favBtn1 = findViewById(favBtnRadioIDs[0]);
        favBtn2 = findViewById(favBtnRadioIDs[1]);
        favBtn3 = findViewById(favBtnRadioIDs[2]);
        favBtn4 = findViewById(favBtnRadioIDs[3]);
        favBtn5 = findViewById(favBtnRadioIDs[4]);
        favBtn6 = findViewById(favBtnRadioIDs[5]);

    }


    private void setFrequencySeekbarMode(int min, int max, boolean b) {
        frqSeekbar.setMin(min);
        frqSeekbar.setMax(max);
        if(!b){

            frequencyAM = prefManager.getRadioFrequencyAM();
            txtFrequency.setText(String.valueOf(frequencyAM));
            frqSeekbar.setProgress(frequencyAM);
        }else {
            frequencyFM = prefManager.getRadioFrequencyFM();
            txtFrequency.setText(String.valueOf(frequencyFM));
            frqSeekbar.setProgress(frequencyFM);
        }

    }


    private void sendData(final String data, int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectUsbService.write(data);
            }
        }, delay);
    }

    private void checkVoiceChannel() {
        boolean bt = prefManager.getBluetoothPlayerState();
        boolean hu = prefManager.getHeadUnitAudioIsActive();
        if (hu || bt) {
            if (hu) {
                audioManager.pauseHeadUnitMusicPlayer();
            }

            sendData(audioValues.radioMode(), 100);
            prefManager.setBluetoothPlayerState(false);
            prefManager.setHeadUnitAudioIsActive(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        audioManager.pauseHeadUnitMusicPlayer();
        sendData(audioValues.radioMode(), 1500);
        sendData(radioValues.getRadioManualSearchValues(), 1000);
        Log.i(tag, "onStart");
        prefManager.setRadioIsRun(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        audioManager.pauseHeadUnitMusicPlayer();
        Log.i(tag, "onResume");
        prefManager.setRadioIsRun(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(tag, "onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(tag, "onStop");
        prefManager.setRadioIsRun(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendData(audioValues.androidBTMode(), 300);
        Log.i(tag, "onDESTROy");
        prefManager.setRadioIsRun(false);


        unbindService(connectUsbService.onDestroyUsb());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        prefManager.setRadioIsRun(false);
        Log.i(tag, "BACK");
        //this.finish();
    }
}
