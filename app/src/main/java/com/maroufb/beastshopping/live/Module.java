package com.maroufb.beastshopping.live;

import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;

public class Module {
    public static void Register(BeastShoppingApplication application){
        new LiveAccountServices(application);
        new LiveShoppingService(application);
        new LiveItemService(application);

    }
}
