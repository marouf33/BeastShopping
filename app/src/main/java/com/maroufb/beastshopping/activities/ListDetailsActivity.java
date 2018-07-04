package com.maroufb.beastshopping.activities;

import android.app.DialogFragment;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.dialog.AddItemDialogFragment;
import com.maroufb.beastshopping.dialog.ChangeItemNameDialogFragment;
import com.maroufb.beastshopping.dialog.ChangeListNameDialogFragment;
import com.maroufb.beastshopping.dialog.DeleteItemDialogFragment;
import com.maroufb.beastshopping.dialog.DeleteListDialogFragment;
import com.maroufb.beastshopping.enitites.ShoppingItem;
import com.maroufb.beastshopping.enitites.ShoppingList;
import com.maroufb.beastshopping.infrastructure.Utils;
import com.maroufb.beastshopping.services.ItemService;
import com.maroufb.beastshopping.services.ShoppingListService;
import com.maroufb.beastshopping.views.ItemListViewHolder.ShoppingItemViewHolder;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListDetailsActivity extends  BaseActivity{

    public static final String SHOPPING_LIST_DETAILS = "SHOPPING_LIST_DETAILS";

    private String mShoppingId;
    private String mShoppingName;
    private String mShoppingOwner;

    private DatabaseReference mShoppingListReference;

    private ValueEventListener mShoppingListListener;

    private ShoppingList mCurrentShoppingList;

    @BindView(R.id.activity_list_details_progressBar)
    ProgressBar mProgressBar;


    RecyclerView mRecyclerView;
    FirebaseRecyclerAdapter mAdapter;

    public static Intent newInstance(Context context, ArrayList<String> shoppingListInfo){
        Intent intent = new Intent(context,ListDetailsActivity.class);
        intent.putStringArrayListExtra(SHOPPING_LIST_DETAILS,shoppingListInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);
        mShoppingId = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(0);
        mShoppingName = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(1);
        mShoppingOwner = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(2);

        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.GONE);
        mShoppingListReference =  FirebaseDatabase.getInstance().getReference().child("userShoppingList")
                .child(userEmail).child(mShoppingId);
        bus.post(new ShoppingListService.GetCurrentShoppingListRequest(mShoppingListReference));

        getSupportActionBar().setTitle(mShoppingName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.activity_list_details_listRecyclerView);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("shoppingListItems").child(mShoppingId);

        FirebaseRecyclerOptions<ShoppingItem> options =
                new FirebaseRecyclerOptions.Builder<ShoppingItem>()
                        .setQuery(reference, ShoppingItem.class)
                        .build();
        mAdapter = new FirebaseRecyclerAdapter<ShoppingItem,ShoppingItemViewHolder>(options){

            @NonNull
            @Override
            public ShoppingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_view, parent, false);
                ShoppingItemViewHolder shoppingItemViewHolder = new ShoppingItemViewHolder(view);
                return shoppingItemViewHolder;

            }

            @Override
            protected void onBindViewHolder(@NonNull final ShoppingItemViewHolder holder, int position, @NonNull final ShoppingItem model) {
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!model.isBought()) {
                            DialogFragment dialogFragment = DeleteItemDialogFragment.newInstance(mShoppingId, model.getId());
                            dialogFragment.show(getFragmentManager(), DeleteItemDialogFragment.class.getSimpleName());
                        }
                    }
                };

                View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ArrayList<String> extraInfo = new ArrayList<>();
                        extraInfo.add(model.getId());
                        extraInfo.add(mShoppingId);
                        extraInfo.add(userEmail);
                        extraInfo.add(model.getItemName());
                        DialogFragment dialogFragment = ChangeItemNameDialogFragment.newInstance(extraInfo);
                        dialogFragment.show(getFragmentManager(),ChangeItemNameDialogFragment.class.getSimpleName());
                        return true;
                    }
                };

                holder.setChangeNameListener(longClickListener);
                holder.setDeleteButtonListener(listener);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if((!model.isBought()) || (userEmail.equals(model.getBoughtBy()) && model.isBought())) {
                            String buyer = "";
                            if (!model.isBought()) {
                                buyer = userEmail;
                            }

                            bus.post(new ItemService.ChangeItemBoughtStatusRequest(model.getId(), mShoppingId, model.getOwnerEmail(), buyer, !model.isBought()));
                        }
                    }
                });
                holder.populate(model,userEmail);


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
        getMenuInflater().inflate(R.menu.menu_list_details,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_change_list_name:
                ArrayList<String> shoppingListInfo = new ArrayList<>();
                shoppingListInfo.add(mShoppingId);
                shoppingListInfo.add(mShoppingName);
                DialogFragment dialogFragment = ChangeListNameDialogFragment.newInstance(shoppingListInfo);
                dialogFragment.show(getFragmentManager(),ChangeListNameDialogFragment.class.getSimpleName());
                return  true;
            case R.id.action_delete_list:
                if(userEmail.equals(Utils.encodeEmail(userEmail))){
                    DialogFragment ddialogFragment = DeleteListDialogFragment.newInstance(mShoppingId,false,true);
                    ddialogFragment.show(getFragmentManager(),DeleteListDialogFragment.class.getSimpleName());

                    return true;
                }else{
                    Toast.makeText(getApplicationContext(),"Only the owner can delete a list",Toast.LENGTH_LONG).show();
                    return true;
                }
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_share_list:



                ArrayList<String> sshoppingListInfo = new ArrayList<>();
                sshoppingListInfo.add(mShoppingId);
                sshoppingListInfo.add(mShoppingName);
                sshoppingListInfo.add(mShoppingOwner);
                startActivity(ShareListActivity.newInstance(getApplicationContext(),sshoppingListInfo));


        }
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    @Subscribe
    public void getCurrentShoppingList(ShoppingListService.GetCurrentShoppingListResponse response){
        mShoppingListListener = response.mValueEventListener;
        mCurrentShoppingList = response.mShoppingList;
        getSupportActionBar().setTitle(mCurrentShoppingList.getListName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShoppingListReference.removeEventListener(mShoppingListListener);
    }

    @OnClick(R.id.activity_list_details_FAB)
    public void setFloatingActionButton(){
        ArrayList<String> shoppingItemInfo = new ArrayList<>();
        shoppingItemInfo.add(mShoppingId);
        DialogFragment dialogFragment = AddItemDialogFragment.newInstance(shoppingItemInfo);
        dialogFragment.show(getFragmentManager(),AddItemDialogFragment.class.getSimpleName());
    }


}
