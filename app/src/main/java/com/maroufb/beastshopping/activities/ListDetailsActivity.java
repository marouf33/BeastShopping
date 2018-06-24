package com.maroufb.beastshopping.activities;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.dialog.ChangeListNameDialogFragment;
import com.maroufb.beastshopping.enitites.ShoppingList;
import com.maroufb.beastshopping.services.ShoppingListService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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
            case android.R.id.home:
                onBackPressed();
                return true;

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
}
