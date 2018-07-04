package com.maroufb.beastshopping.views.AddFriendView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.enitites.User;
import com.maroufb.beastshopping.infrastructure.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.list_user_name)
    TextView userEmail;

    @BindView(R.id.list_user_delete)
   public ImageView userImageView;

    public AddFriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(User user){
        userEmail.setText(user.getEmail());


    }

    public void populate(String name){
        userEmail.setText(name);
    }
}
