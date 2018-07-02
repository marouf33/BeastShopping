package com.maroufb.beastshopping.views.ItemListViewHolder;

import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.enitites.ShoppingItem;
import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;
import com.maroufb.beastshopping.infrastructure.Utils;
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

    private boolean isBought;
    private View.OnClickListener mOnClickListener;

    public ShoppingItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);


    }

    public void setDeleteButtonListener(View.OnClickListener listener){
        deleteButton.setOnClickListener(listener);
        mOnClickListener = listener;
    }

    public void setChangeNameListener(View.OnLongClickListener listener){
        itemName.setOnLongClickListener(listener);
    }



    public void populate(ShoppingItem item,String currentUser){
        isBought = item.isBought();
        itemName.setText(item.getItemName());
        String buyer = currentUser.equals(item.getBoughtBy()) ? "You" : Utils.decodeEmail(item.getBoughtBy());
        if(isBought){
            boughtBy.setVisibility(View.VISIBLE);
            boughtByName.setText(buyer);
            deleteButton.setImageResource(R.mipmap.ic_done);
            itemName.setPaintFlags(itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            deleteButton.setBackgroundResource(R.drawable.button_background_straight_green);

        }else {
            boughtBy.setVisibility(View.GONE);
            boughtByName.setText("");
            deleteButton.setImageResource(R.mipmap.ic_delete);
            deleteButton.setBackgroundResource(R.drawable.button_background_straight_red);
            itemName.setPaintFlags(itemName.getPaintFlags() &(~ Paint.STRIKE_THRU_TEXT_FLAG));
        }

    }


}
