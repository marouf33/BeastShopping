package com.maroufb.beastshopping.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;
import com.maroufb.beastshopping.infrastructure.Utils;
import com.squareup.otto.Bus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BaseActivity extends AppCompatActivity{
    protected BeastShoppingApplication application;
    protected Bus bus;
    protected FirebaseAuth auth;
    protected FirebaseAuth.AuthStateListener mAuthStateListener;
    protected String userEmail,userName;
    protected SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (BeastShoppingApplication) getApplication();
        bus = application.getBus();
        bus.register(this);

        mSharedPreferences = getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE);
        userName = mSharedPreferences.getString(Utils.USERNAME,"");
        userEmail = mSharedPreferences.getString(Utils.EMAIL,"");

        auth = FirebaseAuth.getInstance();
        if(!((this instanceof LoginActivity) || (this instanceof RegisterActivity) || (this instanceof SplashScreenActivity))){
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user == null){
                        logUserOut(false);
                    }
                }
            };

            if(userEmail.equals("")){
                logUserOut(true);
            }

        }
     //   printKeyHash();

    }

    private void logUserOut(boolean AuthSignOut){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Utils.EMAIL,null);
        editor.putString(Utils.USERNAME,null);
        if(AuthSignOut) {
            auth.signOut();
            LoginManager.getInstance().logOut();
        }
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);

        if(!((this instanceof LoginActivity) || (this instanceof RegisterActivity) || (this instanceof SplashScreenActivity))){
            auth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!((this instanceof LoginActivity) || (this instanceof RegisterActivity) || (this instanceof SplashScreenActivity))){
            auth.addAuthStateListener(mAuthStateListener);
        }
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("jk", "Exception(NameNotFoundException) : " + e);
        } catch (NoSuchAlgorithmException e) {
            Log.e("mkm", "Exception(NoSuchAlgorithmException) : " + e);
        }
    }
}
