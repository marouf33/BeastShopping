package com.maroufb.beastshopping.live;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastshopping.enitites.ShoppingItem;
import com.maroufb.beastshopping.enitites.ShoppingList;
import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;
import com.maroufb.beastshopping.infrastructure.Utils;
import com.maroufb.beastshopping.services.ItemService;
import com.maroufb.beastshopping.services.ShoppingListService;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

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
           reference.setValue(shoppingList);

           Toast.makeText(mApplication.getApplicationContext(),"List has been created!",Toast.LENGTH_LONG).show();
       }

       bus.post(response);
    }

    @Subscribe
    public void DeleteShoppingList(ShoppingListService.DeleteShoppingListRequest request){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("userShoppingList")
                .child(request.ownerEmail).child(request.shoppingListId);
        final DatabaseReference dreference = FirebaseDatabase.getInstance().getReference().child("shoppingListItems")
                .child(request.shoppingListId);
        reference.removeValue();
        dreference.removeValue();
    }

    @Subscribe
    public void ChangeListName(ShoppingListService.ChangeListNameRequest request){
        ShoppingListService.ChangeListNameResponse response = new ShoppingListService.ChangeListNameResponse();
        if(request.newShoppingListName.isEmpty()){
            response.setPropertyErrors("listName", "Shopping list must have a name");
        }

        if(response.didSucceed()){
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("userShoppingList")
                    .child(request.shoppingListOwnerEmail).child(request.shoppingListId);
            HashMap<String,Object> timeLastChanged = new HashMap<>();
            timeLastChanged.put("date", ServerValue.TIMESTAMP);

            Map newListData = new HashMap();
            newListData.put("listName", request.newShoppingListName);
            newListData.put("dateLastChanged",timeLastChanged);
            reference.updateChildren(newListData);
        }

        bus.post(response);
    }

    @Subscribe
    public void getCurrentShoppingList(ShoppingListService.GetCurrentShoppingListRequest request){
        final ShoppingListService.GetCurrentShoppingListResponse response = new ShoppingListService.GetCurrentShoppingListResponse();
        response.mValueEventListener = request.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                response.mShoppingList = dataSnapshot.getValue(ShoppingList.class);
                if(response.mShoppingList!=null){
                    bus.post(response);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mApplication.getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe
    public void UpdateShoppingListTimeStamp(ShoppingListService.UpdateShoppingListTimeStampRequest request){
        HashMap<String,Object> timeLastChanged = new HashMap<>();
        timeLastChanged.put("date",ServerValue.TIMESTAMP);
        Map newListData = new HashMap();
        newListData.put("dateLastChanged",timeLastChanged);
        request.FirebaseReference.updateChildren(newListData);
    }


}
