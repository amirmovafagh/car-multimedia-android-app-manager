package ir.dev_roid.testusb;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.special.ResideMenu.ResideMenu;

import com.warkiz.widget.IndicatorSeekBar;

import ir.dev_roid.testusb.app.AudioValues;
import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.CustomDialog;
import ir.dev_roid.testusb.app.MyAudioManager;
import ir.dev_roid.testusb.app.PrefManager;
import ir.dev_roid.testusb.app.ToolBar_ResideMenu;

public class RadioActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String tag = RadioActivity.class.getSimpleName();

    private ImageButton nextImgBtn, previousImgBtn;
    private TextView toolbarTextView, txtFrequency;
    private ResideMenu resideMenu;
    private IndicatorSeekBar frqSeekbar;
    private Button favRadioButton, favBtn1, favBtn2, favBtn3, favBtn4, favBtn5, favBtn6;
    private PrefManager prefManager;
    private CustomDialog dialog;
    private Float frequency;
    private ToolBar_ResideMenu toolBarResideMenu;
    private MyAudioManager audioManager;
    private AudioValues audioValues;

    private ConnectUsbService connectUsbService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        prefManager = new PrefManager(this);
        prefManager.setRadioIsRun(true);
        connectUsbService = new ConnectUsbService(RadioActivity.this);
        audioManager = new MyAudioManager(this);
        audioManager.pauseHeadUnitMusicPlayer();
        audioValues = new AudioValues(prefManager);
        sendData(audioValues.radioMode(), 0);
        //toolbarInit();
        initView();
        onLongClick();


        frequency = prefManager.getRadioFrequency();
        txtFrequency.setText(String.valueOf(frequency));
        frqSeekbar.setProgress(frequency);

        nextImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frequency += (float) 0.1;
                frqSeekbar.setProgress(frequency);


            }
        });

        previousImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frequency -= (float) 0.1;
                frqSeekbar.setProgress(frequency);
            }
        });

        frqSeekbar.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
                checkVoiceChannel();
                frequency = progressFloat;
                txtFrequency.setText(String.valueOf(progressFloat));
                String stringValue = String.valueOf(progressFloat * 10);
                stringValue = stringValue.replaceAll("\\.0", "");
                //stringValue=stringValue.replaceAll("\\.", "");
                connectUsbService.write("rad-frq-" + stringValue + "?");
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
                prefManager.setRadioFrequency(frequency);
            }
        });

    }

    /*private void toolbarInit() {
        myToolbar = findViewById(R.id.toolbar);
        toolbarTextView = findViewById(R.id.toolbar_text);
        menuIcon = myToolbar.findViewById(R.id.icon_menu);
        toolbarVolumeControl = findViewById(R.id.toolbar_volumeControlIcon);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Radio");

        dialog = new CustomDialog(RadioActivity.this, prefManager, connectUsbService);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

        toolbarVolumeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }*/

    @Override
    public void onClick(View view) {

        checkVoiceChannel();
        switch (view.getId()) {
            case R.id.fav_radio_btn1:

                if (!prefManager.getFavoriteRadioFrequency(0).equals("SAVE FREQUENCY")) {
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(0)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(0));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(0)));
                }
                break;
            case R.id.fav_radio_btn2:

                if (!prefManager.getFavoriteRadioFrequency(1).equals("SAVE FREQUENCY")) {
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(1)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(1));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(1)));
                }
                break;
            case R.id.fav_radio_btn3:

                if (!prefManager.getFavoriteRadioFrequency(2).equals("SAVE FREQUENCY")) {
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(2)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(2));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(2)));
                }
                break;
            case R.id.fav_radio_btn4:

                if (!prefManager.getFavoriteRadioFrequency(3).equals("SAVE FREQUENCY")) {
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(3)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(3));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(3)));
                }
                break;
            case R.id.fav_radio_btn5:

                if (!prefManager.getFavoriteRadioFrequency(4).equals("SAVE FREQUENCY")) {
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(4)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(4));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(4)));
                }
                break;
            case R.id.fav_radio_btn6:

                if (!prefManager.getFavoriteRadioFrequency(5).equals("SAVE FREQUENCY")) {
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(5)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(5));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(5)));
                }

                break;
        }
    }


    private void onLongClick() {


        favBtn1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                prefManager.setFavoriteRadioFrequency(0, txtFrequency.getText().toString());
                favBtn1.setText(prefManager.getFavoriteRadioFrequency(0));
                return false;

            }
        });

        favBtn2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                prefManager.setFavoriteRadioFrequency(1, txtFrequency.getText().toString());
                favBtn2.setText(prefManager.getFavoriteRadioFrequency(1));
                return false;
            }
        });

        favBtn3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                prefManager.setFavoriteRadioFrequency(2, txtFrequency.getText().toString());
                favBtn3.setText(prefManager.getFavoriteRadioFrequency(2));
                return false;
            }
        });

        favBtn4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                prefManager.setFavoriteRadioFrequency(3, txtFrequency.getText().toString());
                favBtn4.setText(prefManager.getFavoriteRadioFrequency(3));
                return false;
            }
        });

        favBtn5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                prefManager.setFavoriteRadioFrequency(4, txtFrequency.getText().toString());
                favBtn5.setText(prefManager.getFavoriteRadioFrequency(4));
                return false;
            }
        });

        favBtn6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                prefManager.setFavoriteRadioFrequency(5, txtFrequency.getText().toString());
                favBtn6.setText(prefManager.getFavoriteRadioFrequency(5));
                return false;
            }
        });
    }

    private void initView() {
        frqSeekbar = findViewById(R.id.seekbar_frequency);
        //indicatorSeekBar = new IndicatorSeekBar(this);

        txtFrequency = findViewById(R.id.txt_frequency);
        previousImgBtn = findViewById(R.id.previous_img_btn);
        nextImgBtn = findViewById(R.id.next_img_btn);

        //toolbarInit
        toolBarResideMenu = new ToolBar_ResideMenu(this, "Radio", connectUsbService, prefManager);
        //resideMenuInit
        toolBarResideMenu.resideMenuInit("Home", "Bluetooth", "Settings",
                R.drawable.icon_home, R.drawable.icon_home, R.drawable.icon_home,
                MainActivity.class, BluetoothActivity.class, SettingsActivity.class, "Radio");

        final int[] favBtnRadioIDs = {R.id.fav_radio_btn1, R.id.fav_radio_btn2, R.id.fav_radio_btn3, R.id.fav_radio_btn4,
                R.id.fav_radio_btn5, R.id.fav_radio_btn6};
        for (int i = 0; i < favBtnRadioIDs.length; i++) {
            favRadioButton = findViewById(favBtnRadioIDs[i]);
            favRadioButton.setText(prefManager.getFavoriteRadioFrequency(i));
            favRadioButton.setOnClickListener(this);
        }

        favBtn1 = findViewById(favBtnRadioIDs[0]);
        favBtn2 = findViewById(favBtnRadioIDs[1]);
        favBtn3 = findViewById(favBtnRadioIDs[2]);
        favBtn4 = findViewById(favBtnRadioIDs[3]);
        favBtn5 = findViewById(favBtnRadioIDs[4]);
        favBtn6 = findViewById(favBtnRadioIDs[5]);

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
        sendData(audioValues.radioMode(), 3000);
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
