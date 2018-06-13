package com.maroufb.beastshopping.live;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;
import com.maroufb.beastshopping.infrastructure.Utils;
import com.maroufb.beastshopping.services.AccountServices;
import com.squareup.otto.Subscribe;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

public class LiveAccountServices extends BaseLiveService {
    public LiveAccountServices(BeastShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void RegisterUser(final AccountServices.RegisterUserRequest request){
        AccountServices.RegisterUserResponse response = new AccountServices.RegisterUserResponse();

        if (request.userEmail.isEmpty()){
            response.setPropertyErrors("email", "Please put in your email.");
        }

        if(request.userName.isEmpty()){
            response.setPropertyErrors("userName", "Please put in your name.");
        }

        if(response.didSucceed()){
            request.mProgressBar.setVisibility(View.VISIBLE);
            SecureRandom random = new SecureRandom();
            final String randomPassword = new BigInteger(12,random).add(new BigInteger("1000000")).toString();

            mAuth.createUserWithEmailAndPassword(request.userEmail,randomPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                              request.mProgressBar.setVisibility(View.GONE);
                              Toast.makeText(mApplication.getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }else{
                                mAuth.sendPasswordResetEmail(request.userEmail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(!task.isSuccessful()){
                                                    request.mProgressBar.setVisibility(View.GONE);
                                                    Toast.makeText(mApplication.getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                                }else{
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                                                            .child(Utils.encodeEmail(request.userEmail));

                                                    HashMap<String, Object> timeJoined = new HashMap<>();
                                                    timeJoined.put("dateJoined", ServerValue.TIMESTAMP);

                                                    reference.child("email").setValue(request.userEmail);
                                                    reference.child("name").setValue(request.userName);
                                                    reference.child("hasLoggedInWithPassword").setValue(false);
                                                    reference.child("timeJoined").setValue(timeJoined);

                                                    Toast.makeText(mApplication.getApplicationContext(),"Please Check Your Email", Toast.LENGTH_LONG).show();
                                                    request.mProgressBar.setVisibility(View.GONE);

                                                }
                                            }
                                        });
                            }
                        }
                    });
        }

        bus.post(response);
    }

    @Subscribe
    public void LogInUser(final AccountServices.LoginUserRequest request){
        AccountServices.LogUserInResponse response = new AccountServices.LogUserInResponse();

        if(request.userEMail.isEmpty()){
            response.setPropertyErrors("email","Please enter your email.");
        }

        if(request.userPassword.isEmpty()){
            response.setPropertyErrors("password","please enter your password.");
        }

        if(response.didSucceed()){
            request.mProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(request.userEMail,request.userPassword)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        request.mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(mApplication.getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }else{
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                                .child(Utils.encodeEmail(request.userEMail));
                        reference.child("hasLoggedInWithPassword").setValue(true);
                        request.mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(mApplication.getApplicationContext(),"User has logged in!", Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
        bus.post(response);
    }

    @Subscribe
    public void FacebookLogin(final AccountServices.LogUserInFacebookRequest request){
        request.mProgressBar.setVisibility(View.VISIBLE);

        AuthCredential authCredential = FacebookAuthProvider.getCredential(request.mAccessToken.getToken());
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    request.mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(mApplication.getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }else{
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                            .child(Utils.encodeEmail(request.userEmail));
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() == null){
                                HashMap<String, Object> timeJoined = new HashMap<>();
                                timeJoined.put("dateJoined", ServerValue.TIMESTAMP);

                                reference.child("email").setValue(request.userEmail);
                                reference.child("name").setValue(request.userName);
                                reference.child("hasLoggedInWithPassword").setValue(true);
                                reference.child("timeJoined").setValue(timeJoined);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            request.mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(mApplication.getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                    request.mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(mApplication.getApplicationContext(),"User has logged in!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
