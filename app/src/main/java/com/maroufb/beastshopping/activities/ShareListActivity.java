package com.maroufb.beastshopping.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.enitites.SharedFriends;
import com.maroufb.beastshopping.enitites.User;
import com.maroufb.beastshopping.infrastructure.Utils;
import com.maroufb.beastshopping.services.GetUsersService;
import com.maroufb.beastshopping.views.AddFriendView.AddFriendViewHolder;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

public class ShareListActivity extends  BaseActivity {

    FirebaseRecyclerAdapter mAdapter;
    private String mShoppingId;

    private DatabaseReference sharedListsReference;
    private ValueEventListener mValueEventListener;
    private SharedFriends sharedWithUserFriends;

    public static Intent newInstance(Context context, ArrayList<String> shoppingListInfo){
        Intent intent = new Intent(context,ShareListActivity.class);
        intent.putStringArrayListExtra(ListDetailsActivity.SHOPPING_LIST_DETAILS,shoppingListInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mShoppingId = getIntent().getStringArrayListExtra(ListDetailsActivity.SHOPPING_LIST_DETAILS).get(0);

        RecyclerView recyclerView = findViewById(R.id.activity_share_list_listRecyclerView);

        sharedListsReference = FirebaseDatabase.getInstance().getReference().child("sharedWith")
                .child(mShoppingId);
        bus.post(new GetUsersService.GetSharedWithFriendsRequest(sharedListsReference));
        final DatabaseReference friendsReference = FirebaseDatabase.getInstance().getReference().child("usersFriends")
                .child(userEmail).child("usersFriends");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(friendsReference, User.class)
                        .build();
        mAdapter = new FirebaseRecyclerAdapter<User,AddFriendViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull final AddFriendViewHolder holder, int position, @NonNull final User model) {
                holder.populate(model.getName());
                if(sharedWithFriend(sharedWithUserFriends.getSharedWith(),model)){
                    holder.userImageView.setImageResource(R.mipmap.ic_done);
                }else{
                    holder.userImageView.setImageResource(R.mipmap.ic_pluss);
                }

                holder.userImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final DatabaseReference shareReference = FirebaseDatabase.getInstance().getReference().child("sharedWith")
                                .child(mShoppingId).child("sharedWith").child(Utils.encodeEmail(model.getEmail()));

                        if(sharedWithFriend(sharedWithUserFriends.getSharedWith(),model)){
                            shareReference.removeValue();
                            holder.userImageView.setImageResource(R.mipmap.ic_pluss);
                        }else {
                            shareReference.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            holder.userImageView.setImageResource(R.mipmap.ic_done);
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
        sharedListsReference.removeEventListener(mValueEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_list,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_friend:


                ArrayList<String> sshoppingListInfo = new ArrayList<>();
                sshoppingListInfo.add(getIntent().getStringArrayListExtra(ListDetailsActivity.SHOPPING_LIST_DETAILS).get(0));
                sshoppingListInfo.add(getIntent().getStringArrayListExtra(ListDetailsActivity.SHOPPING_LIST_DETAILS).get(1));
                sshoppingListInfo.add(getIntent().getStringArrayListExtra(ListDetailsActivity.SHOPPING_LIST_DETAILS).get(2));
                startActivity(AddFriendActivity.newInstance(getApplicationContext(),sshoppingListInfo));
                return true;
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
        startActivity(ListDetailsActivity.newInstance(getApplicationContext(),sshoppingListInfo));
        finish();
    }

    private boolean sharedWithFriend(HashMap<String,User> userFriends, User friend){
        return userFriends!=null && userFriends.size()!=0
                && userFriends.containsKey(Utils.encodeEmail(friend.getEmail()));
    }

    @Subscribe
    public void getCurrentUsersFriends(GetUsersService.GetSharedWithFriendsResponse response){
        mValueEventListener = response.mValueEventListener;

        if(response.mSharedFriends != null){
            sharedWithUserFriends = response.mSharedFriends;
        }else{
            sharedWithUserFriends = new SharedFriends();
        }
    }
}
