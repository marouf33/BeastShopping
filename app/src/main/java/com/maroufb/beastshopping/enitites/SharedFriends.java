package com.maroufb.beastshopping.enitites;

import android.support.annotation.Nullable;

import java.util.HashMap;

public class SharedFriends {
    @Nullable
    private HashMap<String,User> sharedWith;

    public SharedFriends() {
    }

    public SharedFriends(HashMap<String, User> sharedWith) {
        this.sharedWith = sharedWith;
    }

    public HashMap<String, User> getSharedWith() {
        return sharedWith;
    }
}

