package com.hooshmandkhodro.carservice.app.dagger;

import android.app.Application;

import com.hooshmandkhodro.carservice.app.GpioUart;

public class App extends Application {
    private AppComponent component;
//Instantiating the component
    @Override
    public void onCreate() {
        super.onCreate();

        // Dagger%COMPONENT_NAME%
        component = DaggerAppComponent.builder()
                // list of modules that are part of this component need to be created here too
                .appModule(new AppModule(this)) //This also corresponds to the name of your module: %component_name%Module
                .sharedPreferencesModule(new SharedPreferencesModule())
                .gpioUartModule(new GpioUartModule(new GpioUart(1)))
                .build();

        // If a Dagger 2 component does not have any constructor arguments for any of its modules,
        // then we can use .create() as a shortcut instead:
        //  mAppComponent = com.codepath.dagger.components.DaggerAppComponent.create();
    }

    public AppComponent getComponent() {
        return component;
    }
}
