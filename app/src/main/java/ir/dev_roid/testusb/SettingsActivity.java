package ir.dev_roid.testusb;

import android.content.ClipData;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;


import com.special.ResideMenu.ResideMenu;

import ir.dev_roid.testusb.app.ConnectUsbService;
import ir.dev_roid.testusb.app.PrefManager;
import ir.dev_roid.testusb.app.ToolBar_ResideMenu;


public class SettingsActivity extends AppCompatActivity implements View.OnTouchListener,
        View.OnDragListener {

private static final String TAG = SettingsActivity.class.getSimpleName();

    private ImageView audioBalanceImg;
    private ConnectUsbService connectUsbService;
    private SeekBar basSeekbar,trebleSeekbar, loudSeekbar;
    private int basValue, trebleValue;
    private ViewGroup relativeLayout = null;
    private Rect relativeLayoutRect = null;
    private boolean isCoordinatesInit_Level1 = false;
    private PrefManager pref;
    private int p;
    private int i = 0; //handler switch case
    private ImageButton b;
    private Handler handlerSetSpeakersData;

    private int startLeftMargin=100;
    private int endLeftMargin = 330;
    private int startTopMargin = 50;
    private int endTopMargin = 390;
    private int increaseRatioX = 20;
    private int increaseRatioY = 30;
    ToolBar_ResideMenu toolBarResideMenu;
    int[] chartWidth;
    int[] chartHeight;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initObjectsView();
        pref = new PrefManager(SettingsActivity.this);


        connectUsbService = new ConnectUsbService(SettingsActivity.this);

        toolBarResideMenu = new ToolBar_ResideMenu(SettingsActivity.this, "Audio Settings");
        toolBarResideMenu.resideMenuInit("Home", "Bluetooth", "Radio",
                R.drawable.icon_home, R.drawable.icon_home, R.drawable.icon_home, MainActivity.class
                , BluetoothActivity.class, RadioActivity.class, "Settings");
        basSeekbar.setProgress(pref.getVolumeValue(5));
        trebleSeekbar.setProgress(pref.getVolumeValue(6));

        chartWidth = new int[11];
        chartHeight = new int[11];
        for(int i=0 ; i<11 ; i++){
            chartWidth[i] = startLeftMargin + (increaseRatioX * (i+1));
            chartHeight[i] = startTopMargin + (increaseRatioY * (i+1));
            Log.d("chart","W: "+chartWidth[i]+" H: "+chartHeight[i]);

            audioBalanceImg.animate()
                    .x(pref.getXcordinate())
                    .y(pref.getYcordinate())
                    .setDuration(100)
                    .start();

        }




        basSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                p = progress;
                basValue= 112-progress;
                Log.i("bas",""+basValue);
                if(basValue<112){
                    connectUsbService.write("aud-bas-"+basValue+"?");
                }else connectUsbService.write("aud-bas-111?");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pref.setVolumeValue(5, p);
            }
        });

        trebleSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                p = progress;
                trebleValue= progress+96;

                if(trebleValue<112){
                    connectUsbService.write("aud-tbl-"+trebleValue+"?");
                }else connectUsbService.write("aud-tbl-111?");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pref.setVolumeValue(6, p);
            }
        });

        loudSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                p = progress;
                connectUsbService.write("aud-lod-"+progress+"?");
                //Toast.makeText(SettingsActivity.this, ""+progress, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pref.setVolumeValue(7, p);
            }
        });
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

    private void setSoundBalanceValue(int leftFront, int rightFront, int leftRear, int rightRear){
        connectUsbService.write("aud-vlf-"+(223 - leftFront)+"?");
        connectUsbService.write("aud-vrf-"+(255 - rightFront)+"?");
        connectUsbService.write("aud-vlr-"+(159 - leftRear)+"?");
        connectUsbService.write("aud-vrr-"+(191 - rightRear)+"?");
    }


    private boolean audBalanceObjectOnTouch(View v, MotionEvent event) {

        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
        v.startDrag(data, shadowBuilder, v, 0);

        return true;
    }

    private boolean relativeLayoutOnTouch(View v, MotionEvent event) {
        if (isCoordinatesInit_Level1 == false){
            relativeLayoutRect = new Rect(0,0,relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight());
            isCoordinatesInit_Level1 = true;
        }

        if (relativeLayoutRect.contains((int)event.getX(), (int)event.getY()))
        {
            audioBalanceImg.animate()
                    .x(event.getX()-32)
                    .y(event.getY()-32)
                    .setDuration(10)
                    .start();
            checkStateOfImageBalance(event.getX()-32, event.getY()-32);


            }

        return true;
    }

    private void checkStateOfImageBalance(float getX, float getY) {
        if(getX < 100 || getY < 50 || getX > 311 || getY > 361){
            audioBalanceImg.animate()
                    .x(220-32)
                    .y(222-32)
                    .setDuration(0)
                    .start();
            speakersInit(100,100,100,100);
        }else {
            calculateBalanceData(getX, getY);
            pref.setXYcordinates(getX, getY);
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        View view = (View) event.getLocalState();


        switch (event.getAction()){
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
                if (view.getId() == R.id.audio_balance_img )
                    audioBalanceImg.animate()
                            .x(event.getX()-32)
                            .y(event.getY()-32)
                            .setDuration(0)
                            .start();
                checkStateOfImageBalance(event.getX()-32, event.getY()-32);


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

    private void calculateBalanceData(double xtl ,double ytl){
        double  xfl, yfl, xfr, yrl, result;
        int fl, fr, rr, rl;

        xfl = (int)((xtl) - startLeftMargin)*(11.0/210.0);
        yfl = (int)((ytl) - startTopMargin)*(11.0/310.0);
        xfr = (int)(10 - xfl);
        yrl = (int)(10 - yfl);
        //yfr = yfl;
        //xrl = xfl;
        //xrr = xfr;
        //yrr = yrl;
        Log.i(TAG, "X: " + xtl);
        Log.i(TAG, "Y: " + ytl);

        //Front left
        if(xfl <= 5 && yfl <= 5 ){
            //FL = 100
            fl = 100;
        }else{
            fl =(int) (100 - (20*(Math.max(xfl, yfl) - 5)));
        }

        Log.i(TAG, "FL: " + fl);

        //Front right
        if(xfr <= 5 && yfl <= 5){
            fr = 100;
        }else {
            fr = (int)(100 - (20*(Math.max(xfr, yfl) - 5)));
        }
        Log.i(TAG, "FR: " + fr);

        //Rear Left
        if(xfl <= 5 && yrl <= 5){
            rl = 100;
        }else {
            rl =(int) (100 - (20*(Math.max(xfl, yrl) - 5)));
        }
        Log.i(TAG, "RL: " + rl);

        //Rear Right
        if(xfr <= 5 && yrl <= 5){
            rr = 100;
        }else {
            rr = (int)(100 - (20*(Math.max(xfr, yrl) - 5)));
        }
        Log.i(TAG, "RR: " + rr);

        speakersInit(fl, fr, rl, rr);
    }

    private void speakersInit(int frontLeft, int frontRight, int rearLeft, int rearRight){
        final int fl, fr, rr, rl;

        fl = checkPercent(frontLeft);
        pref.setVolumeValue(1, fl);
        fr = checkPercent(frontRight);
        pref.setVolumeValue(2, fr);
        rl = checkPercent(rearLeft);
        pref.setVolumeValue(3, rl);
        rr = checkPercent(rearRight);
        pref.setVolumeValue(4, rr);

        Log.d(TAG, " fl : "+fl+" fr : "+fr+" rl : "+rl+" rr : "+rr);
        handlerSetSpeakersData = new Handler();
        handlerSetSpeakersData.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,""+i);
                switch (i){
                    case 1:
                        connectUsbService.write("aud-vlf-"+(223-fl)+"?");
                        handlerWait(this,500);
                        break;
                    case 2:
                        connectUsbService.write("aud-vrf-"+(255-fr)+"?");
                        handlerWait(this,500);
                        break;
                    case 3:
                        connectUsbService.write("aud-vlr-"+(159-rl)+"?");
                        handlerWait(this,500);
                        break;
                    case 4:
                        connectUsbService.write("aud-vrr-"+(191-rl)+"?");
                        handlerSetSpeakersData.removeCallbacks(this);
                        i=0;
                        break;

                }
                i++;
            }
        }, 100);
    }

    private void handlerWait(Runnable r, int delay){
        handlerSetSpeakersData.postDelayed(r, delay);
    }

    private int checkPercent(int value) {
        int volume = 30;
        if(value >= 0 && value <= 20){
            volume = 0;
            return volume;
        }else if(value > 20 && value <= 40){
            volume = 7;
            return volume;
        }else if(value > 40 && value <= 60){
            volume = 14;
            return volume;
        }else if(value > 60 && value <= 80){
            volume = 22;
            return volume;
        }else if(value > 80 && value <= 100){
            volume = 30;
            return volume;
        }
        return volume;
    }

    private void initObjectsView(){

        audioBalanceImg = (ImageView)findViewById(R.id.audio_balance_img);
        audioBalanceImg.setOnTouchListener(this);
        b = (ImageButton) findViewById(R.id.btn_balance_up);
        //txt = (TextView)findViewById(R.id.txt) ;
        //on click audio balance
        relativeLayout = findViewById(R.id.relative_layout);
        relativeLayout.setOnTouchListener(this);
        relativeLayout.setOnDragListener(this);
        basSeekbar = (SeekBar)findViewById(R.id.bas_seekbar);
        trebleSeekbar = (SeekBar)findViewById(R.id.treble_seekbar);
        loudSeekbar = (SeekBar)findViewById(R.id.loud_seekbar);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
    }
}
