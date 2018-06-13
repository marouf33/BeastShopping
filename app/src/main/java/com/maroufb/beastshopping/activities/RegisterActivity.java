package com.maroufb.beastshopping.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.maroufb.beastshopping.R;
import com.maroufb.beastshopping.services.AccountServices;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity{

    @BindView(R.id.activity_register_linear_layout)
    LinearLayout mLinearLayout;

    @BindView(R.id.activity_register_loginButton)
    Button loginButton;

    @BindView(R.id.activity_register_usrEmail)
    EditText userEmail;

    @BindView(R.id.activity_register_userName)
    EditText userName;

    @BindView(R.id.activity_register_registerButton)
    Button registerButton;

    @BindView(R.id.register_progressBar)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mLinearLayout.setBackgroundResource(R.drawable.background_screen_two);
        mProgressBar.setVisibility(View.GONE);


    }

    @OnClick(R.id.activity_register_loginButton)
    public void setLoginButton(){
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    @OnClick(R.id.activity_register_registerButton)
    public void setRegisterButton(){
        bus.post(new AccountServices.RegisterUserRequest(userName.getText().toString(),
                                                         userEmail.getText().toString(),
                                                         mProgressBar));
    }

    @Subscribe
    public void RegisterUser(AccountServices.RegisterUserResponse response){
        if (!response.didSucceed()){
            userEmail.setError(response.getPropertyError("email"));
            userName.setError(response.getPropertyError("userName"));
        }
    }
}
