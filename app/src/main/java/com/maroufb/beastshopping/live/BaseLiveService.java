package com.maroufb.beastshopping.live;

import com.google.firebase.auth.FirebaseAuth;
import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;
import com.squareup.otto.Bus;

public class BaseLiveService {
    protected Bus bus;
    protected BeastShoppingApplication mApplication;
    protected FirebaseAuth mAuth;

    public BaseLiveService(BeastShoppingApplication application) {
        mApplication = application;
        bus = application.getBus();
        bus.register(this);
        mAuth = FirebaseAuth.getInstance();
    }
}
