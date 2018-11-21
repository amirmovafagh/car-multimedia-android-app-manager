package ir.dev_roid.testusb.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import java.util.Objects;

import ir.dev_roid.testusb.BluetoothActivity;
import ir.dev_roid.testusb.MainActivity;
import ir.dev_roid.testusb.R;
import ir.dev_roid.testusb.RadioActivity;
import ir.dev_roid.testusb.SettingsActivity;

public class ToolBar_ResideMenu
{
    private ResideMenu resideMenu;
    private Toolbar myToolbar;
    private ImageView menuIcon,toolbarVolumeControl;
    private TextView toolbarTextView;
    private CustomDialog dialog;
    private Activity activity;



    public ToolBar_ResideMenu() {
        this.activity = activity;
    }

    public ToolBar_ResideMenu(final Activity activity, String toolbarTitle, ConnectUsbService connectUsbService, PrefManager prefManager)
    {
        this.activity = activity;
        myToolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        toolbarTextView = (TextView) activity.findViewById(R.id.toolbar_text);
        menuIcon = (ImageView) myToolbar.findViewById(R.id.icon_menu);
        toolbarVolumeControl = (ImageView) activity.findViewById(R.id.toolbar_volumeControlIcon) ;
        //setSupportActionBar(myToolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTextView.setText(toolbarTitle);

        dialog = new CustomDialog(activity,prefManager, connectUsbService);

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

    public void Title(String toolBarTitle){
        toolbarTextView.setText(toolBarTitle);
    }

    public void resideMenuInit(final String actName1, String actName2, String actName3,
                               int icon1, int icon2, int icon3, final Class actClass1,
                               final Class actClass2, final Class actClass3, final String activityName){

        resideMenu = new ResideMenu(activity);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(activity);

        final String titles[] = { actName1, actName2, actName3 };
        int icon[] = { icon1, icon2, icon3 };

        for (int i = 0; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(activity, icon[i], titles[i]);
            final int finalI = i;

            //click Event
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(BluetoothActivity.this,""+titles[finalI],Toast.LENGTH_SHORT).show();
                    if(Objects.equals(activityName,"Home")) {
                        switch (titles[finalI]) {
                            case "Bluetooth":
                                activity.startActivity(new Intent(activity, actClass1));
                                activity.finish();
                                break;
                            case "Radio":
                                activity.startActivity(new Intent(activity, actClass2));
                                activity.finish();
                                break;
                            case "Settings":
                                activity.startActivity(new Intent(activity, actClass3));
                                activity.finish();
                                break;

                        }
                    } else if(Objects.equals(activityName,"Bluetooth")){
                        switch (titles[finalI]) {
                            case "Home":
                                activity.startActivity(new Intent(activity, actClass1));
                                activity.finish();
                                break;
                            case "Radio":
                                activity.startActivity(new Intent(activity, actClass2));
                                activity.finish();
                                break;
                            case "Settings":
                                activity.startActivity(new Intent(activity, actClass3));
                                activity.finish();
                                break;

                        }
                    } else if(Objects.equals(activityName,"Radio")){
                        switch (titles[finalI]) {
                            case "Home":
                                activity.startActivity(new Intent(activity, actClass1));
                                activity.finish();
                                break;
                            case "Bluetooth":
                                activity.startActivity(new Intent(activity, actClass2));
                                activity.finish();
                                break;
                            case "Settings":
                                activity.startActivity(new Intent(activity, actClass3));
                                activity.finish();
                                break;

                        }
                    }else if(Objects.equals(activityName,"Settings")){
                        switch (titles[finalI]) {
                            case "Home":
                                activity.startActivity(new Intent(activity, actClass1));
                                activity.finish();
                                break;
                            case "Bluetooth":
                                activity.startActivity(new Intent(activity, actClass2));
                                activity.finish();
                                break;
                            case "Radio":
                                activity.startActivity(new Intent(activity, actClass3));
                                activity.finish();
                                break;

                        }
                    }
                }
            });

            //options direction
            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }

        //resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT); // or ResideMenu.DIRECTION_RIGHT
        //resideMenu.closeMenu();
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        //when menu open or close
        resideMenu.setMenuListener(new ResideMenu.OnMenuListener() {
            @Override
            public void openMenu() {
                //Toast.makeText(MainActivity.this, "Menu is opened!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closeMenu() {
                //Toast.makeText(MainActivity.this, "Menu is closed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
