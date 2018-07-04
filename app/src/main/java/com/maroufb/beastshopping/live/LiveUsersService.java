package com.maroufb.beastshopping.live;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastshopping.enitites.Users;
import com.maroufb.beastshopping.infrastructure.BeastShoppingApplication;
import com.maroufb.beastshopping.services.GetUsersService;
import com.squareup.otto.Subscribe;

public class LiveUsersService extends BaseLiveService {
    public LiveUsersService(BeastShoppingApplication application) {
        super(application);
    }

    @Subscribe public void getUsersFriends(GetUsersService.GetUsersFriendsRequest request){
        final GetUsersService.GetUsersFriendsResponse response = new GetUsersService.GetUsersFriendsResponse();
        response.mValueEventListener = request.mFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                response.mUsersFriends = dataSnapshot.getValue(Users.class);
                bus.post(response);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mApplication.getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
