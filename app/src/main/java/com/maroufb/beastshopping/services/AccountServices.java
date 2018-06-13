package com.maroufb.beastshopping.services;

import android.app.ProgressDialog;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.maroufb.beastshopping.infrastructure.ServiceResponse;

public class AccountServices {
    private AccountServices() {
    }

    public static class RegisterUserRequest{
        public String userName;
        public String userEmail;
        public ProgressBar mProgressBar;

        public RegisterUserRequest(String userName, String userEmail, ProgressBar progressBar) {
            this.userName = userName;
            this.userEmail = userEmail;
            this.mProgressBar = progressBar;
        }

    }

    public static class RegisterUserResponse extends ServiceResponse{}

    public static class LoginUserRequest{
        public String userEMail;
        public String userPassword;
        public ProgressBar mProgressBar;

        public LoginUserRequest(String userEMail, String userPassword, ProgressBar progressBar) {
            this.userEMail = userEMail;
            this.userPassword = userPassword;
            mProgressBar = progressBar;
        }
    }

    public static class LogUserInResponse extends ServiceResponse{}

    public static class LogUserInFacebookRequest{
        public AccessToken mAccessToken;
        public ProgressBar mProgressBar;
        public String userName;
        public String userEmail;

        public LogUserInFacebookRequest(AccessToken accessToken, ProgressBar progressBar, String userName, String userEmail) {
            mAccessToken = accessToken;
            mProgressBar = progressBar;
            this.userName = userName;
            this.userEmail = userEmail;
        }
    }


}
