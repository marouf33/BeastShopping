package com.maroufb.beastshopping.live;

import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.maroufb.beastshopping.enitites.ShoppingItem;
import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;
import com.maroufb.beastshopping.services.ItemService;
import com.maroufb.beastshopping.services.ShoppingListService;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class LiveItemService extends BaseLiveService {
    public LiveItemService(BeastShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void AddShoppingItem(ItemService.AddShoppingItemRequest request){
        ItemService.AddShoppingItemResponse response = new ItemService.AddShoppingItemResponse();
        if(request.itemName.isEmpty()){
            response.setPropertyErrors("itemName","Shopping item must have a name");
        }

        if(response.didSucceed()){
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("shoppingListItems")
                    .child(request.shoppingList).push();
            final DatabaseReference listReference = FirebaseDatabase.getInstance().getReference().child("userShoppingList")
                    .child(request.ownerEmail).child(request.shoppingList);

            ShoppingItem shoppingItem = new ShoppingItem(reference.getKey(),request.itemName,request.ownerEmail,"",false);
            reference.child("id").setValue(shoppingItem.getId());
            reference.child("itemName").setValue(shoppingItem.getItemName());
            reference.child("ownerEmail").setValue(shoppingItem.getOwnerEmail());
            reference.child("boughtBy").setValue(shoppingItem.getBoughtBy());
            reference.child("bought").setValue(shoppingItem.isBought());

            HashMap<String,Object> timeLastChanged = new HashMap<>();
            timeLastChanged.put("date", ServerValue.TIMESTAMP);

            Map newListData = new HashMap();
            newListData.put("dateLastChanged",timeLastChanged);
            listReference.updateChildren(newListData);



            Toast.makeText(mApplication.getApplicationContext(),"Item has been Added!",Toast.LENGTH_LONG);

        }

        bus.post(response);
    }

    @Subscribe
    public void DeleteShoppingItem(ItemService.DeleteItemRequest request){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("shoppingListItems")
                .child(request.listId).child(request.itemId);
        final  DatabaseReference listReference = FirebaseDatabase.getInstance().getReference().child("userShoppingList")
                .child(request.ownerEmail).child(request.listId);

        HashMap<String,Object> timeLastChanged = new HashMap<>();
        timeLastChanged.put("date", ServerValue.TIMESTAMP);

        Map newListData = new HashMap();
        newListData.put("dateLastChanged",timeLastChanged);
        reference.removeValue();
        listReference.updateChildren(newListData);
    }

    @Subscribe
    public void ChangeItemName(ItemService.ChangeItemNameRequest request){
        ItemService.ChangeItemNameResponse response = new ItemService.ChangeItemNameResponse();


        if(request.newItemName.isEmpty()){
            response.setPropertyErrors("listName", "Shopping Item must have a name");
        }

        if(response.didSucceed()){
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("shoppingListItems")
                    .child(request.shoppingListId).child(request.itemId);
            final  DatabaseReference listReference = FirebaseDatabase.getInstance().getReference().child("userShoppingList")
                    .child(request.userEmail).child(request.shoppingListId);

            HashMap<String,Object> timeLastChanged = new HashMap<>();
            timeLastChanged.put("date", ServerValue.TIMESTAMP);

            Map newListData = new HashMap(), changeName = new HashMap();
            newListData.put("dateLastChanged",timeLastChanged);
            changeName.put("itemName",request.newItemName);
            listReference.updateChildren(newListData);
            reference.updateChildren(changeName);
        }


        bus.post(response);
    }

    @Subscribe public void ChangeItemBougtStatus(ItemService.ChangeItemBoughtStatusRequest request){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("shoppingListItems")
                .child(request.shoppingListId).child(request.itemId);
        final  DatabaseReference listReference = FirebaseDatabase.getInstance().getReference().child("userShoppingList")
                .child(request.userEmail).child(request.shoppingListId);

        HashMap<String,Object> timeLastChanged = new HashMap<>();
        timeLastChanged.put("date", ServerValue.TIMESTAMP);
        Map newListData = new HashMap(), changeBought = new HashMap();
        newListData.put("dateLastChanged",timeLastChanged);
        changeBought.put("bought",request.isBought);
        changeBought.put("boughtBy",request.buyerEmail);
        listReference.updateChildren(newListData);
        reference.updateChildren(changeBought);

    }
}
