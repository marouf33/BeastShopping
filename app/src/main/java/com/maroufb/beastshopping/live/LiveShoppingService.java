package com.maroufb.beastshopping.live;

import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.maroufb.beastshopping.enitites.ShoppingList;
import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;
import com.maroufb.beastshopping.infrastructure.Utils;
import com.maroufb.beastshopping.services.ShoppingListService;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

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

           final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("userShoppingList")
                   .child(request.ownerEmail).push();
           HashMap<String, Object> timestampedCreated = new HashMap<>();
           timestampedCreated.put("timestamp", ServerValue.TIMESTAMP);
           ShoppingList shoppingList = new ShoppingList(reference.getKey(),request.shoppingListName,
                   Utils.decodeEmail(request.ownerEmail),request.ownerName,timestampedCreated);
           reference.child("id").setValue(shoppingList.getId());
           reference.child("listName").setValue(shoppingList.getListName());
           reference.child("ownerEmail").setValue(shoppingList.getOwnerEmail());
           reference.child("ownerName").setValue(shoppingList.getOwnerName());
           reference.child("dateCreated").setValue(shoppingList.getDateCreated());
           reference.child("dateLastChanged").setValue(shoppingList.getDateLastChanged());


           Toast.makeText(mApplication.getApplicationContext(),"List has been created!",Toast.LENGTH_LONG).show();
       }

       bus.post(response);
    }

    @Subscribe
    public void DeleteShoppingList(ShoppingListService.DeleteShoppingListRequest request){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("userShoppingList")
                .child(request.ownerEmail).child(request.shoppingListId);
        reference.removeValue();
    }
}
