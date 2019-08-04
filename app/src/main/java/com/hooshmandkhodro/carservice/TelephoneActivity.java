package com.hooshmandkhodro.carservice;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hooshmandkhodro.carservice.app.GpioUart;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.InfluenceOfAddUserToActivityOps;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.InfluenceOfAudienceInfoToActivity;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.InfluenceOfChangeAudienceInfoToActivity;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.InfluenceOfPhoneLogToActivityOps;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.InfluenceOfUserInfoToActivity;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAddUserFragment.AddUserFragment;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment.AudienceInfoFragment;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment.InfluenceOfActivityToAudienceInfoOps;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgChangeAudienceInfoFragment.ChangeAudienceInfoFragment;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneLogFragment.InfluenceOfActivityToPhoneLogOps;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneLogFragment.PhoneLogFragment;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.PkgUserInfoFragment.UserInfoFragment;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.ProvidedPresenterOps;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.RequiredViewOps;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.TelephonePresenter;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.PkgTelephoneActivity.ViewPagerAdapter;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.Audience;
import com.hooshmandkhodro.carservice.bluetoothFragments.contacts.Pojo.PhoneNumber;
import com.miguelcatalan.materialsearchview.MaterialSearchView;




public class TelephoneActivity extends AppCompatActivity implements RequiredViewOps
        , InfluenceOfPhoneLogToActivityOps, InfluenceOfAddUserToActivityOps
        , InfluenceOfAudienceInfoToActivity, InfluenceOfUserInfoToActivity
        , InfluenceOfChangeAudienceInfoToActivity {

    MaterialSearchView searchView;
    MenuItem addAudienceItem;
    MenuItem searchItem;
    ViewPagerAdapter viewPagerAdapter;
    Fragment currentFragment;

    ProvidedPresenterOps presenterOps = new TelephonePresenter(this, this);

    public static GpioUart connectGpioUart;
    private AddUserFragment addUserFragment;
    private InfluenceOfActivityToPhoneLogOps toPhoneLogOps;
    private InfluenceOfActivityToAudienceInfoOps toAudienceInfoOps;
    private UserInfoFragment userInfoFragment;
    private ChangeAudienceInfoFragment changeAudienceInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyHandler.telephoneActivityIsOpen = true;
        setContentView(R.layout.activity_telephone);

            connectGpioUart  = new GpioUart(1);


        Toolbar tbTelephoneActivity = findViewById(R.id.tb_telephone_activity);
        setSupportActionBar(tbTelephoneActivity);

        searchView = findViewById(R.id.sv_search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (currentFragment instanceof PhoneLogFragment)
                    toPhoneLogOps.searchActived(newText);
                else if (currentFragment instanceof AudienceInfoFragment)
                    toAudienceInfoOps.searchActived(newText);
                return true;
            }
        });

        ViewPager vpTelephoneActivity = findViewById(R.id.vp_telephone_activity);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        vpTelephoneActivity.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (viewPagerAdapter.getPageTitle(position).equals(ViewPagerAdapter.TITLE_AUDIENCE_INFO)){
                    addAudienceItem.setVisible(true);
                }else {
                    addAudienceItem.setVisible(false);
                }

                if (viewPagerAdapter.getPageTitle(position).equals(ViewPagerAdapter.TITLE_PHONE_DIALER)){
                    searchView.closeSearch();
                    searchItem.setVisible(false);
                }else {
                    searchItem.setVisible(true);
                }

                currentFragment = viewPagerAdapter.getItem(position);
                if (currentFragment instanceof PhoneLogFragment)
                    toPhoneLogOps.fillRecyclerView();
                else if (currentFragment instanceof AudienceInfoFragment)
                    toAudienceInfoOps.fillRecyclerView();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpTelephoneActivity.setAdapter(viewPagerAdapter);

        TabLayout tlTelephoneActivity = findViewById(R.id.tl_telephone_activity);
        tlTelephoneActivity.setupWithViewPager(vpTelephoneActivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyHandler.telephoneActivityIsOpen = true;
        currentFragment = viewPagerAdapter.getItem(ViewPagerAdapter.TITLE_PHONE_LOG);
        presenterOps.activityStart();
        toPhoneLogOps = (PhoneLogFragment) viewPagerAdapter.getItem(ViewPagerAdapter.TITLE_PHONE_LOG);
        toAudienceInfoOps = (AudienceInfoFragment) viewPagerAdapter.getItem(ViewPagerAdapter.TITLE_AUDIENCE_INFO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.telephone_activity_toolbar_menu, menu);
        searchItem = menu.findItem(R.id.mi_search);
        searchView.setMenuItem(searchItem);

        addAudienceItem = menu.findItem(R.id.mi_add_audience);
        addAudienceItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showAddUserFragment(null);
                return true;
            }
        });

        return true;
    }

    @Override
    public void showAddUserFragment(PhoneNumber phoneNumber) {
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        addUserFragment = new AddUserFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("phoneNumber", phoneNumber);
        addUserFragment.setArguments(bundle);
        ft.add(R.id.fl_fragment_container, addUserFragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void closeAddUserFragment() {
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(addUserFragment);
        ft.commit();
        addUserFragment = null;
    }
    
    @Override
    public void closeAddUserFragmentAndUpdatePhoneLogRecyclerView() {
        this.closeAddUserFragment();
        if (currentFragment instanceof PhoneLogFragment)
            toPhoneLogOps.fillRecyclerView();
        else if (currentFragment instanceof AudienceInfoFragment)
            toAudienceInfoOps.fillRecyclerView();
        searchView.closeSearch();
    }

    @Override
    public void showUserInfoFragment(Audience audience) {
        searchItem.setVisible(false);
        addAudienceItem.setVisible(false);

        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        userInfoFragment = new UserInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("audienceInfo", audience);
        userInfoFragment.setArguments(bundle);
        ft.add(R.id.fl_fragment_container, userInfoFragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    public void showChangeAudienceInfoFragment(Audience audience) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        changeAudienceInfoFragment = new ChangeAudienceInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("audienceInfo", audience);
        changeAudienceInfoFragment.setArguments(bundle);
        ft.add(R.id.fl_fragment_container, changeAudienceInfoFragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void closeUserInfoFragment() {
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(userInfoFragment);
        ft.commit();
        userInfoFragment = null;
    }

    @Override
    public void onBackPressed() {

        if(userInfoFragment != null){
            closeUserInfoFragment();
        }else if (changeAudienceInfoFragment != null){
            closeChangeAudienceInfoFragment();
        }else {
            finish();
            super.onBackPressed();
        }

    }

    @Override
    public void closeChangeAudienceInfoFragment() {
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(changeAudienceInfoFragment);
        ft.commit();
        changeAudienceInfoFragment = null;
    }

    @Override
    public void closeChangeAudienceInfoFragmentAndUpdateRecyclerView() {
        closeChangeAudienceInfoFragment();
        toAudienceInfoOps.fillRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyHandler.telephoneActivityIsOpen = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyHandler.telephoneActivityIsOpen = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyHandler.telephoneActivityIsOpen = false;

    }
}
