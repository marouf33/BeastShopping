package com.maroufb.beastshopping.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.dialog.AddListDialogFragment;
import com.maroufb.beastshopping.enitites.ShoppingList;
import com.maroufb.beastshopping.infrastructure.Utils;
import com.maroufb.beastshopping.services.ShoppingListService;
import com.maroufb.beastshopping.views.ShoppingListViews.ShoppingListViewHolder;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.activity_main_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.activity_main_FAB)
    FloatingActionButton mFloatingActionButton;

    RecyclerView mRecyclerView;

    FirebaseRecyclerAdapter mAdapter;
    //FirebaseRec

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRecyclerView = findViewById(R.id.activity_main_listRecyclerView);
        mProgressBar.setVisibility(View.GONE);
        String toolBarName;

        if(userName.contains(" ")){
            toolBarName = userName.substring(0,userName.indexOf(" "))+ "'s Shopping List";
        }else{
            toolBarName = userName + "'s Shopping List";
        }

        getSupportActionBar().setTitle(toolBarName);

    }

    @Override
    protected void onResume() {
        super.onResume();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("userShoppingList").child(userEmail);

        FirebaseRecyclerOptions<ShoppingList> options =
                new FirebaseRecyclerOptions.Builder<ShoppingList>()
                        .setQuery(reference, ShoppingList.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<ShoppingList,ShoppingListViewHolder>(options) {

            @NonNull
            @Override
            public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_shopping_list, parent, false);
                ShoppingListViewHolder shoppingListViewHolder = new ShoppingListViewHolder(view);
                parent.setTag(shoppingListViewHolder);
                return shoppingListViewHolder;

            }

            @Override
            protected void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position, @NonNull final ShoppingList model) {
                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),model.getListName() + " was clicked", Toast.LENGTH_LONG).show();
                    }
                });
                holder.populate(model);

            }
        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch(item.getItemId()){
            case R.id.action_logout:
                SharedPreferences sharedPreferences = getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE);
                mProgressBar.setVisibility(View.VISIBLE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Utils.EMAIL,null);
                editor.putString(Utils.USERNAME,null);
                auth.signOut();
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
                mProgressBar.setVisibility(View.GONE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.activity_main_FAB)
    public void setFloatingActionButton(){
        DialogFragment dialogFragment = AddListDialogFragment.newInstance();
        dialogFragment.show(getFragmentManager(),AddListDialogFragment.class.getSimpleName());
    }


}
