package com.maroufb.beastshopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.activities.MainActivity;
import com.maroufb.beastshopping.enitites.SharedFriends;
import com.maroufb.beastshopping.enitites.User;
import com.maroufb.beastshopping.infrastructure.Utils;
import com.maroufb.beastshopping.services.GetUsersService;
import com.maroufb.beastshopping.services.ShoppingListService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class DeleteListDialogFragment extends BaseDialog implements View.OnClickListener{

    public static final String EXTRA_SHOPPING_LIST_ID = "EXTRA_SHOPPING_LIST_ID";
    public static final String EXTRA_BOOLEAN = "EXTRA_BOOLEAN";
    public static final String FINISH_PARENT = "FINISH_PARENT";


    private String mShoppingListId;
    private boolean mIsLongClicked;
    private boolean mFinishParent;

    private ValueEventListener mShareWIthListener;
    private SharedFriends mSharedWith;
    private DatabaseReference mShareWithReference;

    public static DeleteListDialogFragment newInstance(String shoppingListId, boolean isLongClicked, boolean finishParentActivity){
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_SHOPPING_LIST_ID,shoppingListId);
        arguments.putBoolean(EXTRA_BOOLEAN,isLongClicked);
        arguments.putBoolean(FINISH_PARENT,finishParentActivity);

        DeleteListDialogFragment dialogFragment = new DeleteListDialogFragment();
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShoppingListId = getArguments().getString(EXTRA_SHOPPING_LIST_ID);
        mIsLongClicked = getArguments().getBoolean(EXTRA_BOOLEAN);
        mFinishParent = getArguments().getBoolean(FINISH_PARENT);
        mShareWithReference = FirebaseDatabase.getInstance().getReference().child("sharedWith")
                .child(mShoppingListId);
        bus.post(new GetUsersService.GetSharedWithFriendsRequest(mShareWithReference));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_delete_list,null);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
               .setView(rootView)
               .setPositiveButton("Confirm",null)
               .setNegativeButton("Cancel",null)
               .setTitle("Delete Shopping List?")
               .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        return dialog;

    }

    @Override
    public void onClick(View v) {

        dismiss();

        deleteAllShoppingLists(mSharedWith.getSharedWith(),bus,mShoppingListId,userEmail);
        if(mFinishParent){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Subscribe
    public void getUsersSharedWith(GetUsersService.GetSharedWithFriendsResponse response){
        mShareWIthListener = response.mValueEventListener;

        if (response.mSharedFriends!=null){
            mSharedWith = response.mSharedFriends;
        } else{
            mSharedWith = new SharedFriends();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mShareWithReference.removeEventListener(mShareWIthListener);
    }

    public static void deleteAllShoppingLists(HashMap<String,User> usersSharedWith, Bus bus
            , String shoppingListId, String ownerEmail){
        if (usersSharedWith !=null && !usersSharedWith.isEmpty()){
            for(User user: usersSharedWith.values()){
                if (usersSharedWith.containsKey(Utils.encodeEmail(user.getEmail())))
                {

                    final DatabaseReference friendListsReference = FirebaseDatabase.getInstance().getReference().child("userShoppingList")
                            .child(Utils.encodeEmail(user.getEmail())).child(shoppingListId);

                    Map newListData = new HashMap();
                    newListData.put("listName","CListIsAboutToGetDeleted");
                    friendListsReference.updateChildren(newListData);
                    friendListsReference.removeValue();

                }
            }
        }
        bus.post(new ShoppingListService.DeleteShoppingListRequest(ownerEmail,shoppingListId));
    }
}
