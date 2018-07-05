package com.maroufb.beastshopping.live;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.maroufb.beastshopping.activities.LoginActivity;
import com.maroufb.beastshopping.activities.MainActivity;
import com.maroufb.beastshopping.enitites.User;
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
                                                    User newUser = new User(request.userEmail,request.userName,timeJoined,false);
                                                    reference.setValue(newUser);

                                                    Toast.makeText(mApplication.getApplicationContext(),"Please Check Your Email", Toast.LENGTH_LONG).show();
                                                    request.mProgressBar.setVisibility(View.GONE);

                                                    Intent intent = new Intent(mApplication.getApplicationContext(), LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    mApplication.startActivity(intent);
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
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                                .child(Utils.encodeEmail(request.userEMail));
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if(user != null){
                                    reference.child("hasLoggedInWithPassword").setValue(true);
                                    SharedPreferences sharedPreferences = request.mSharedPreferences;
                                    sharedPreferences.edit().putString(Utils.EMAIL,Utils.encodeEmail((user.getEmail()))).apply();
                                    sharedPreferences.edit().putString(Utils.USERNAME,user.getName()).apply();

                                    request.mProgressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(mApplication.getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    mApplication.startActivity(intent);
                                }else{
                                    request.mProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(mApplication.getApplicationContext(),"Failed to connect to server: Please try again",Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                request.mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(mApplication.getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

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
                                User newUser = new User(request.userEmail,request.userName,timeJoined,true);
                                reference.setValue(newUser);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            request.mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(mApplication.getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                    SharedPreferences sharedPreferences = request.mSharedPreferences;
                    sharedPreferences.edit().putString(Utils.EMAIL,Utils.encodeEmail((request.userEmail))).apply();
                    sharedPreferences.edit().putString(Utils.USERNAME,request.userName).apply();

                    request.mProgressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(mApplication.getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    mApplication.startActivity(intent);
                }
            }
        });
    }
}
