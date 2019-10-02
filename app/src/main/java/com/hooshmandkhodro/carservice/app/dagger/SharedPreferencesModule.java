package com.hooshmandkhodro.carservice.app.dagger;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
//to signal to Dagger to search within the available methods for possible instance providers.
public class SharedPreferencesModule {

    @Provides
    @Singleton
        // Application reference must come from AppModule.class
    SharedPreferences provideSharedPreferences(Application application) {
        //return  application.getSharedPreferences("PrefManager",Context.MODE_PRIVATE);
        return PreferenceManager.getDefaultSharedPreferences(application);
    }
}
