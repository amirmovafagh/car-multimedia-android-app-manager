package com.hooshmandkhodro.carservice.app.dagger;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module  //to signal to Dagger to search within the available methods for possible instance providers
public class AppModule {

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    // Dagger will only look for methods annotated with @Provides
    @Provides //that informs Dagger that this method is the constructor for the Application return type
    @Singleton //annotation also signals to the Dagger compiler that the instance should be created only once in the application
    public Application providesApplication() {
        return application;
    }

}