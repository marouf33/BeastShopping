package com.maroufb.beastshopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.services.ShoppingListService;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddListDialogFragment extends BaseDialog implements View.OnClickListener{

    @BindView(R.id.dialog_add_list_editText)
    EditText newListName;

    public static AddListDialogFragment newInstance(){
        return new AddListDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_add_list,null);
        ButterKnife.bind(this,rootView);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton("Create",null)
                .setNegativeButton("Cancel",null)
                .setTitle("Creat a list")
                .show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        return alertDialog;
    }

    @Override
    public void onClick(View v) {
        bus.post(new ShoppingListService.AddShoppingListRequest(newListName.getText().toString(),userName,userEmail));
    }

    @Subscribe
    public void AddShoppingList(ShoppingListService.AddShoppingListRsponse response){
        if(!response.didSucceed()){
            newListName.setError(response.getPropertyError("listName"));
        }
    }
}
