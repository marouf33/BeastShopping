package com.maroufb.beastshopping.infrastructure;

public class Utils {
    public static final String FIRE_BASE_URL = "https://beastshopping-7220a.firebaseio.com/";
    public static final String FIRE_BASE_USER_REFERENCE = FIRE_BASE_URL + "users/";

    public static String encodeEmail(String userEmail){
        return userEmail.replace(".",",");
    }

}