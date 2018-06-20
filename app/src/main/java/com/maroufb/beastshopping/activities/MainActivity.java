package com.maroufb.beastshopping.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.login.LoginManager;
import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.infrastructure.Utils;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.activity_main_progressBar)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.activity_main_progressBar);
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
}
