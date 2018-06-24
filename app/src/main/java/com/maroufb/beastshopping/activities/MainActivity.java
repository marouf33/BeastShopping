package com.maroufb.beastshopping.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.android.gms.common.util.Strings;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.dialog.AddListDialogFragment;
import com.maroufb.beastshopping.dialog.DeleteListDialogFragment;
import com.maroufb.beastshopping.enitites.ShoppingList;
import com.maroufb.beastshopping.infrastructure.Utils;
import com.maroufb.beastshopping.services.ShoppingListService;
import com.maroufb.beastshopping.views.ShoppingListViews.ShoppingListViewHolder;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        String sortOrder = sharedPreferences.getString(Utils.LIST_ORDER_PREFERENCE,Utils.ORDER_BY_KEY);

        Query sortQuery;

        if(sortOrder.equals(Utils.ORDER_BY_KEY)){
            sortQuery = reference.orderByKey();
        } else{
            sortQuery = reference.orderByChild(sortOrder);
        }

        FirebaseRecyclerOptions<ShoppingList> options =
                new FirebaseRecyclerOptions.Builder<ShoppingList>()
                        .setQuery(sortQuery, ShoppingList.class)
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
                holder.populate(model);
                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> shoppingListInfo = new ArrayList<>();
                        shoppingListInfo.add(model.getId());
                        shoppingListInfo.add(model.getListName());
                        shoppingListInfo.add(model.getOwnerEmail());
                        startActivity(ListDetailsActivity.newInstance(getApplicationContext(),shoppingListInfo));
                    }
                });

                holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(userEmail.equals(Utils.encodeEmail(model.getOwnerEmail()))){
                            DialogFragment dialogFragment = DeleteListDialogFragment.newInstance(model.getId(),true);
                            dialogFragment.show(getFragmentManager(),DeleteListDialogFragment.class.getSimpleName());
                            return true;
                        }else{
                            Toast.makeText(getApplicationContext(),"Only the owner can delete a list",Toast.LENGTH_LONG).show();
                            return true;
                        }
                    }
                });
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
            case R.id.action_sort:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(settingsIntent);
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
