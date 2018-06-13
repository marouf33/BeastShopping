package com.maroufb.beastshopping.infrastructure;

import android.app.Application;

import com.facebook.appevents.AppEventsLogger;
import com.maroufb.beastshopping.live.Module;
import com.squareup.otto.Bus;

public class BeastShoppingApplication extends Application {

    private Bus bus;

    public BeastShoppingApplication(Bus bus) {
        this.bus = bus;
    }

    public BeastShoppingApplication() {

        bus = new Bus();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Module.Register(this);
        AppEventsLogger.activateApp(this);
    }

    public Bus getBus() {
        return bus;
    }
}
