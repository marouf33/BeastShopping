package com.maroufb.beastshopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.services.ItemService;
import com.maroufb.beastshopping.services.ShoppingListService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddItemDialogFragment extends BaseDialog implements View.OnClickListener{

    @BindView(R.id.dialog_add_item_editText)
    EditText newItemName;

    public final static String SHOPPING_ITEM_EXTRA_INFO = "SHOPPING_ITEM_EXTRA_INFO";
    private String shoppingListID;

    public static AddItemDialogFragment newInstance(ArrayList<String> shoppingListInfo){
        Bundle arguments = new Bundle();
        arguments.putStringArrayList(SHOPPING_ITEM_EXTRA_INFO,shoppingListInfo);
        AddItemDialogFragment dialogFragment = new AddItemDialogFragment();
        dialogFragment.setArguments(arguments);

        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingListID = getArguments().getStringArrayList(SHOPPING_ITEM_EXTRA_INFO).get(0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_add_item,null);
        ButterKnife.bind(this,rootView);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton("Add",null)
                .setNegativeButton("Cancel",null)
                .show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        return alertDialog;
    }

    @Override
    public void onClick(View v) {
        bus.post(new ItemService.AddShoppingItemRequest(newItemName.getText().toString(),userEmail,shoppingListID));

    }

    @Subscribe
    public void AddShoppingList(ItemService.AddShoppingItemResponse response){
        if(!response.didSucceed()){
            newItemName.setError(response.getPropertyError("itemName"));
        }else{
            dismiss();
        }
    }
}