package com.maroufb.beastshopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.services.ItemService;

public class DeleteItemDialogFragment extends BaseDialog implements View.OnClickListener {

    public final static String EXTRA_ITEM_LIST_ID = "EXTRA_ITEM_LIST_ID";
    public final static String EXTRA_ITEM_ITEM_ID = "EXTRA_ITEM_ITEM_ID";

    private String shoppingListID;
    private String shoppingItemID;

    public static DeleteItemDialogFragment newInstance(String shoppingListID,String shoppingItemID){
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_ITEM_LIST_ID,shoppingListID);
        arguments.putString(EXTRA_ITEM_ITEM_ID,shoppingItemID);

        DeleteItemDialogFragment deleteItemDialogFragment = new DeleteItemDialogFragment();
        deleteItemDialogFragment.setArguments(arguments);
        return deleteItemDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingItemID = getArguments().getString(EXTRA_ITEM_ITEM_ID);
        shoppingListID = getArguments().getString(EXTRA_ITEM_LIST_ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_delete_list,null);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton("Confirm",null)
                .setNegativeButton("Cancel",null)
                .setTitle("Delete Item?")
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        return dialog;
    }



    @Override
    public void onClick(View v) {
        bus.post(new ItemService.DeleteItemRequest(userEmail,shoppingListID,shoppingItemID));
        dismiss();
    }
}
