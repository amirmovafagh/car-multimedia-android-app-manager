package com.hooshmandkhodro.carservice.app.dagger;


import android.content.SharedPreferences;

import com.hooshmandkhodro.carservice.AUXActivity;
import com.hooshmandkhodro.carservice.BluetoothActivity;
import com.hooshmandkhodro.carservice.MainActivity;
import com.hooshmandkhodro.carservice.RadioActivity;
import com.hooshmandkhodro.carservice.SettingsActivity;
import com.hooshmandkhodro.carservice.TvChannelActivity;
import com.hooshmandkhodro.carservice.UsbService;
import com.hooshmandkhodro.carservice.app.Receiver;
import com.hooshmandkhodro.carservice.bluetoothFragments.MediaFragment;
import com.hooshmandkhodro.carservice.bluetoothFragments.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, SharedPreferencesModule.class, GpioUartModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(UsbService usbService);

    void inject(RadioActivity radioActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(AUXActivity auxActivity);

    void inject(BluetoothActivity bluetoothActivity);

    void inject(MediaFragment mediaFragment);

    void inject(TvChannelActivity tvChannelActivity);

    void inject(SettingsFragment settingsFragment);

    void inject(Receiver receiver);


}
