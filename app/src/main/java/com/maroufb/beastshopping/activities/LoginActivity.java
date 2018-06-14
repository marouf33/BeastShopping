package com.maroufb.beastshopping.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.infrastructure.Utils;
import com.maroufb.beastshopping.services.AccountServices;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity  extends  BaseActivity{

    @BindView(R.id.activity_login_linear_layout)
    LinearLayout mLinearLayout;

    @BindView(R.id.activitiy_login_RegisterButton)
    Button registerButton;

    @BindView(R.id.activitiy_login_loginButton)
    Button loginButton;

    @BindView(R.id.activity_login_userEmail)
    EditText userEmail;

    @BindView(R.id.activity_login_userPassword)
    EditText userPassword;

    @BindView(R.id.login_progressBar)
    private ProgressBar mProgressBar;

    @BindView(R.id.activity_login_facebook_button)
    LoginButton facebookButton;

    private CallbackManager mCllbackManager;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mLinearLayout.setBackgroundResource(R.drawable.background_screen_two)        ;
        mProgressBar.setVisibility(View.GONE);

        mSharedPreferences = getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE);
    }

    @OnClick(R.id.activitiy_login_RegisterButton)
    public void setRegisterButton(){
        startActivity(new Intent(this,RegisterActivity.class));
        finish();
    }

    @OnClick(R.id.activitiy_login_loginButton)
    public void setLoginButton(){
        bus.post(new AccountServices.LoginUserRequest(userEmail.getText().toString(),userPassword.getText().toString(),mProgressBar,mSharedPreferences ));
    }

    @Subscribe
    public void LogUserIn(AccountServices.LogUserInResponse response){
        if(!response.didSucceed()){
            userEmail.setError(response.getPropertyError("email"));
            userPassword.setError(response.getPropertyError("password"));
        }
    }

    @OnClick(R.id.activity_login_facebook_button)
    public void setFacebookButton(){
        mCllbackManager = CallbackManager.Factory.create();
        facebookButton.setReadPermissions("email","public_profile");

        facebookButton.registerCallback(mCllbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try{
                            String email = object.getString("email");
                            String name = object.getString("name");
                            bus.post(new AccountServices.LogUserInFacebookRequest(loginResult.getAccessToken(),mProgressBar,name,email,mSharedPreferences));
                        }   catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplication(),"An Unknown error occurred.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplication(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCllbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
