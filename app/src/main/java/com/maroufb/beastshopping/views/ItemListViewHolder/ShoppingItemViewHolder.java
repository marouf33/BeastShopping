package com.maroufb.beastshopping.views.ItemListViewHolder;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.enitites.ShoppingItem;
import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;
import com.maroufb.beastshopping.services.ItemService;
import com.squareup.otto.Bus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingItemViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.list_item_view)
    public View layout;

    @BindView(R.id.list_shopping_item_itemName)
    public TextView itemName;

    @BindView(R.id.list_shoppingItem_boughtBy_name)
    TextView boughtByName;

    @BindView(R.id.list_shoppingItem_boughtBy)
    TextView boughtBy;

    @BindView(R.id.list_shoppingItem_delete)
    public ImageView deleteButton;



    public ShoppingItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);


    }

    public void setDeleteButtonListener(View.OnClickListener listener){
        deleteButton.setOnClickListener(listener);
    }

    public void setChangeNameListener(View.OnLongClickListener listener){
        itemName.setOnLongClickListener(listener);
    }

    public void populate(ShoppingItem item){
        itemName.setText(item.getItemName());
        if(item.getBoughtBy() != null && !item.getBoughtBy().isEmpty()){
            boughtBy.setVisibility(View.VISIBLE);
            boughtByName.setText(item.getBoughtBy());
        }else {
            boughtBy.setVisibility(View.GONE);
            boughtByName.setText("");
        }
    }
}
