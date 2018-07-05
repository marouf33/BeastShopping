package com.maroufb.beastshopping.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.enitites.ShoppingItem;
import com.maroufb.beastshopping.enitites.User;
import com.maroufb.beastshopping.enitites.Users;
import com.maroufb.beastshopping.infrastructure.Utils;
import com.maroufb.beastshopping.services.GetUsersService;
import com.maroufb.beastshopping.views.AddFriendView.AddFriendViewHolder;
import com.maroufb.beastshopping.views.ItemListViewHolder.ShoppingItemViewHolder;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

public class AddFriendActivity extends BaseActivity {

    FirebaseRecyclerAdapter mAdapter;

    private DatabaseReference friendsReference;
    private ValueEventListener mValueEventListener;
    private Users currentUserFriends;

    public static Intent newInstance(Context context, ArrayList<String> shoppingListInfo){
        Intent intent = new Intent(context,AddFriendActivity.class);
        intent.putStringArrayListExtra(ListDetailsActivity.SHOPPING_LIST_DETAILS,shoppingListInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = findViewById(R.id.activity_add_friend_listRecyclerView);

        friendsReference = FirebaseDatabase.getInstance().getReference().child("usersFriends")
                .child(userEmail);
        bus.post(new GetUsersService.GetUsersFriendsRequest(friendsReference));

        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(userReference, User.class)
                        .build();



        mAdapter = new FirebaseRecyclerAdapter<User,AddFriendViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull final AddFriendViewHolder holder, int position, @NonNull final User model) {
                holder.populate(model);

                if(isFriend(currentUserFriends.getUsersFriends(),model)){
                    holder.userImageView.setImageResource(R.mipmap.ic_done);
                }else{
                    holder.userImageView.setImageResource(R.mipmap.ic_pluss);
                }

                holder.userImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(userEmail.equals(Utils.encodeEmail(model.getEmail()))){
                            Toast.makeText(getApplicationContext(),"You can not add yourself", Toast.LENGTH_LONG).show();

                        }else{
                            final DatabaseReference friendsReference = FirebaseDatabase.getInstance().getReference().child("usersFriends")
                                    .child(userEmail).child("usersFriends").child(Utils.encodeEmail(model.getEmail()));


                            if(isFriend(currentUserFriends.getUsersFriends(),model)){
                                friendsReference.removeValue();
                                holder.userImageView.setImageResource(R.mipmap.ic_pluss);
                            }else {
                                friendsReference.setValue(model);

                                holder.userImageView.setImageResource(R.mipmap.ic_done);
                            }

                        }
                    }
                });
            }

            @NonNull
            @Override
            public AddFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_user, parent, false);
                AddFriendViewHolder addFriendViewHolder = new AddFriendViewHolder(view);
                return addFriendViewHolder;
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.stopListening();
        friendsReference.removeEventListener(mValueEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return true;
    }

    @Override
    public void onBackPressed() {

        ArrayList<String> sshoppingListInfo = new ArrayList<>();
        sshoppingListInfo.add(getIntent().getStringArrayListExtra(ListDetailsActivity.SHOPPING_LIST_DETAILS).get(0));
        sshoppingListInfo.add(getIntent().getStringArrayListExtra(ListDetailsActivity.SHOPPING_LIST_DETAILS).get(1));
        sshoppingListInfo.add(getIntent().getStringArrayListExtra(ListDetailsActivity.SHOPPING_LIST_DETAILS).get(2));
        startActivity(ShareListActivity.newInstance(getApplicationContext(),sshoppingListInfo));
        finish();
    }

    @Subscribe
    public void getCurrentUsersFriends(GetUsersService.GetUsersFriendsResponse response){
        mValueEventListener = response.mValueEventListener;

        if(response.mUsersFriends != null){
            currentUserFriends = response.mUsersFriends;
        }else{
            currentUserFriends = new Users();
        }
    }

    private boolean isFriend(HashMap<String,User> userFriends, User user){
        return userFriends!=null && userFriends.size()!=0
                && userFriends.containsKey(Utils.encodeEmail(user.getEmail()));
    }

}
