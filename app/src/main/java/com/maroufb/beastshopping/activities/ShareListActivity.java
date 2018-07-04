package com.maroufb.beastshopping.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.maroufb.beastshopping.R;

import java.util.ArrayList;

public class ShareListActivity extends  BaseActivity {

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
}
