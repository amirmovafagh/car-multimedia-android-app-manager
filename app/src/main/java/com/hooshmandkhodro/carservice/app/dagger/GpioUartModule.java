package com.hooshmandkhodro.carservice.app.dagger;

import com.hooshmandkhodro.carservice.app.GpioUart;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GpioUartModule {

    private GpioUart gpioUart;

    public GpioUartModule(GpioUart gpioUart) {
        this.gpioUart = gpioUart;
    }

    @Singleton
    @Provides
    GpioUart provideGpioUart() {
        return gpioUart;
    }
}
