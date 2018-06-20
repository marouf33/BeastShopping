package com.maroufb.beastshopping.live;

import android.widget.Toast;

import com.maroufb.beastshopping.enitites.ShoppingList;
import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;
import com.maroufb.beastshopping.services.ShoppingListService;
import com.squareup.otto.Subscribe;

public class LiveShoppingService extends BaseLiveService {
    public LiveShoppingService(BeastShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void AddShoppingList(ShoppingListService.AddShoppingListRequest request){
       ShoppingListService.AddShoppingListRsponse response = new ShoppingListService.AddShoppingListRsponse();

       if(request.shoppingListName.isEmpty()){
           response.setPropertyErrors("listName", "Shopping List must have a name.");
       }

       if(response.didSucceed()){
           Toast.makeText(mApplication.getApplicationContext(),"List will be added shortly",Toast.LENGTH_LONG).show();
       }

       bus.post(response);
    }
}
