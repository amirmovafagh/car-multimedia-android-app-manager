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
import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.CustomDialog;
import ir.dev_roid.testusb.app.PrefManager;
import ir.dev_roid.testusb.app.ToolBar_ResideMenu;

public class RadioActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar myToolbar;
    private ImageView menuIcon,toolbarVolumeControl;
    private ImageButton nextImgBtn, previousImgBtn;
    private TextView toolbarTextView, txtFrequency;
    private ResideMenu resideMenu;
    private IndicatorSeekBar frqSeekbar;
    private Button favRadioButton, favBtn1, favBtn2, favBtn3,favBtn4, favBtn5, favBtn6;
    private PrefManager prefManager;
    private CustomDialog dialog;
    private Float frequency;
    private ToolBar_ResideMenu toolBarResideMenu;


    private ConnectUsbService connectUsbService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        toolbarInit();
        prefManager = new PrefManager(this);
        initView();
        onLongClick();

        connectUsbService = new ConnectUsbService(RadioActivity.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectUsbService.write("mod-rad?");
            }
        },100);
        frequency = prefManager.getRadioFrequency();
        txtFrequency.setText(String.valueOf(frequency));
        frqSeekbar.setProgress(frequency);

        nextImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frequency += (float)0.1;
                frqSeekbar.setProgress(frequency);


            }
        });

        previousImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frequency -= (float)0.1;
                frqSeekbar.setProgress(frequency);
            }
        });

        frqSeekbar.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {

                frequency = progressFloat;
                txtFrequency.setText(String.valueOf(progressFloat));
                String stringValue=String.valueOf(progressFloat*10);
                stringValue=stringValue.replaceAll("\\.0", "");
                //stringValue=stringValue.replaceAll("\\.", "");

                //Toast.makeText(RadioActivity.this, ""+stringValue, Toast.LENGTH_SHORT).show();
                connectUsbService.write("rad-frq-"+stringValue+"?");
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

    private void toolbarInit (){
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTextView = (TextView) findViewById(R.id.toolbar_text);
        menuIcon = (ImageView) myToolbar.findViewById(R.id.icon_menu);
        toolbarVolumeControl = (ImageView) findViewById(R.id.toolbar_volumeControlIcon) ;
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText("Radio");

        dialog = new CustomDialog(RadioActivity.this);

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
    }

    @Override
    public void onClick(View view) {



        switch (view.getId()){
            case R.id.fav_radio_btn1:

                if(!prefManager.getFavoriteRadioFrequency(0).equals("SAVE FREQUENCY")){
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(0)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(0));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(0)));
                }
                break;
            case R.id.fav_radio_btn2:

                if(!prefManager.getFavoriteRadioFrequency(1).equals("SAVE FREQUENCY")){
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(1)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(1));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(1)));
                }
                break;
            case R.id.fav_radio_btn3:

                if(!prefManager.getFavoriteRadioFrequency(2).equals("SAVE FREQUENCY")){
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(2)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(2));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(2)));
                }
                break;
            case R.id.fav_radio_btn4:

                if(!prefManager.getFavoriteRadioFrequency(3).equals("SAVE FREQUENCY")){
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(3)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(3));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(3)));
                }
                break;
            case R.id.fav_radio_btn5:

                if(!prefManager.getFavoriteRadioFrequency(4).equals("SAVE FREQUENCY")){
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(4)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(4));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(4)));
                }
                break;
            case R.id.fav_radio_btn6:

                if(!prefManager.getFavoriteRadioFrequency(5).equals("SAVE FREQUENCY")){
                    frqSeekbar.setProgress(Float.valueOf(prefManager.getFavoriteRadioFrequency(5)));
                    txtFrequency.setText(prefManager.getFavoriteRadioFrequency(5));
                    prefManager.setRadioFrequency(Float.valueOf(prefManager.getFavoriteRadioFrequency(5)));
                }

                break;
        }
    }



    private void onLongClick(){


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

    private void initView(){
        frqSeekbar = (IndicatorSeekBar)findViewById(R.id.seekbar_frequency);
        //indicatorSeekBar = new IndicatorSeekBar(this);

        txtFrequency = (TextView)findViewById(R.id.txt_frequency);
        previousImgBtn = (ImageButton)findViewById(R.id.previous_img_btn);
        nextImgBtn = (ImageButton)findViewById(R.id.next_img_btn);

        //toolbarInit
        toolBarResideMenu = new ToolBar_ResideMenu(this, "Radio");
        //resideMenuInit
        toolBarResideMenu.resideMenuInit("Home", "Bluetooth", "Settings",
                R.drawable.icon_home, R.drawable.icon_home, R.drawable.icon_home,
                MainActivity.class, BluetoothActivity.class, SettingsActivity.class, "Radio");

        final int[] favBtnRadioIDs={R.id.fav_radio_btn1,R.id.fav_radio_btn2 ,R.id.fav_radio_btn3 ,R.id.fav_radio_btn4,
                R.id.fav_radio_btn5,R.id.fav_radio_btn6};
        for(int i=0; i < favBtnRadioIDs.length ; i++){
            favRadioButton = (Button)findViewById(favBtnRadioIDs[i]);
            favRadioButton.setText(prefManager.getFavoriteRadioFrequency(i));
            favRadioButton.setOnClickListener(this);
        }

        favBtn1 = (Button)findViewById(favBtnRadioIDs[0]);
        favBtn2 = (Button)findViewById(favBtnRadioIDs[1]);
        favBtn3 = (Button)findViewById(favBtnRadioIDs[2]);
        favBtn4 = (Button)findViewById(favBtnRadioIDs[3]);
        favBtn5 = (Button)findViewById(favBtnRadioIDs[4]);
        favBtn6 = (Button)findViewById(favBtnRadioIDs[5]);

    }


}
