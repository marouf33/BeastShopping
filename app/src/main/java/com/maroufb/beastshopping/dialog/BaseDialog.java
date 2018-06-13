package com.maroufb.beastshopping.dialog;

import android.app.DialogFragment;
import android.os.Bundle;

import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;
import com.squareup.otto.Bus;

public class BaseDialog extends DialogFragment {
    protected BeastShoppingApplication application;
    protected Bus bus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (BeastShoppingApplication) getActivity().getApplication();
        bus = application.getBus();
        bus.register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bus.unregister(this);
    }
}
