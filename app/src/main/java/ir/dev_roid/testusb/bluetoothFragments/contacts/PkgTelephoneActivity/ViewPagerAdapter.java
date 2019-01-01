package ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgAudienceInfoFragment.AudienceInfoFragment;
import ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneDialerFragment.PhoneDialerFragment;
import ir.dev_roid.testusb.bluetoothFragments.contacts.PkgTelephoneActivity.PkgPhoneLogFragment.PhoneLogFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    public static final String TITLE_PHONE_LOG = "Log";
    public static final String TITLE_PHONE_DIALER = "Dialer";
    public static final String TITLE_AUDIENCE_INFO = "Contacts";


    private final PhoneDialerFragment phoneDialerFragment;
    private final PhoneLogFragment phoneLogFragment;
    private final AudienceInfoFragment audienceInfoFragment;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        phoneDialerFragment = PhoneDialerFragment.newInstance();
        phoneLogFragment = PhoneLogFragment.newInstance();
        audienceInfoFragment = AudienceInfoFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment current = null;
        switch (position){
            case 0:
                current = phoneDialerFragment;
                break;
            case 1:
                current = phoneLogFragment;
                break;
            case 2:
                current = audienceInfoFragment;
        }
        return current;
    }

    public Fragment getItem(String fragmentTitle){
        Fragment current = null;
        switch (fragmentTitle){
            case TITLE_AUDIENCE_INFO:
                current = audienceInfoFragment;
                break;
            case TITLE_PHONE_DIALER:
                current = phoneDialerFragment;
                break;
            case TITLE_PHONE_LOG:
                current = phoneLogFragment;
                break;
        }
        return current;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String currentTitle = "";
        switch (position){
            case 0:
                currentTitle = TITLE_PHONE_DIALER;
                break;
            case 1:
                currentTitle = TITLE_PHONE_LOG;
                break;
            case 2:
                currentTitle = TITLE_AUDIENCE_INFO;
        }
        return currentTitle;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
