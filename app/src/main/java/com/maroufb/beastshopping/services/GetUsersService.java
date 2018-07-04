package com.maroufb.beastshopping.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastshopping.enitites.SharedFriends;
import com.maroufb.beastshopping.enitites.Users;

public class GetUsersService {

    private GetUsersService() {
    }

    public static class GetUsersFriendsRequest{
        public DatabaseReference mFirebase;

        public GetUsersFriendsRequest(DatabaseReference firebase) {
            mFirebase = firebase;
        }
    }

    public static class GetUsersFriendsResponse{
        public ValueEventListener mValueEventListener;
        public Users mUsersFriends;
    }

    public static class GetSharedWithFriendsRequest{
        public DatabaseReference mFirebase;

        public GetSharedWithFriendsRequest(DatabaseReference firebase) {
            mFirebase = firebase;
        }
    }

    public static class GetSharedWithFriendsResponse{
        public ValueEventListener mValueEventListener;
        public SharedFriends mSharedFriends;
    }
}
