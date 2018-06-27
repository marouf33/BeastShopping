package com.maroufb.beastshopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.services.ItemService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeItemNameDialogFragment extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.dialog_change_item_name_editText)
    EditText newItemName;

    public static final String EXTRA_INFO = "EXTRA_INFO";

    public static ChangeItemNameDialogFragment newInstance(ArrayList<String> extraInfo){
        Bundle args = new Bundle();
        args.putStringArrayList(EXTRA_INFO,extraInfo);
        ChangeItemNameDialogFragment dialogFragment = new ChangeItemNameDialogFragment();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_item_name,null);
        ButterKnife.bind(this,rootView);
        newItemName.setText(getArguments().getStringArrayList(EXTRA_INFO).get(3));
        //newListName.setText(getArguments().getStringArrayList(SHOPPING_LIST_EXTRA_INFO).get(1));
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton("Change name",null)
                .setNegativeButton("Cancel",null)
                .setTitle("Change Item Name?").show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(this);
        return alertDialog;
    }

    @Override
    public void onClick(View v) {
        bus.post(new ItemService.ChangeItemNameRequest(getArguments().getStringArrayList(EXTRA_INFO).get(0),
                getArguments().getStringArrayList(EXTRA_INFO).get(1),
                getArguments().getStringArrayList(EXTRA_INFO).get(2),
                newItemName.getText().toString()));
        dismiss();
    }

    @Subscribe
    public void ChangeItemName(ItemService.ChangeItemNameResponse response){
        if(!response.didSucceed()){
            newItemName.setError(response.getPropertyError("itemName"));
        }else{
            dismiss();
        }
    }
}
