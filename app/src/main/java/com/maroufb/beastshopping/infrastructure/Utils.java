package com.maroufb.beastshopping.infrastructure;



public class Utils {

    public static final String MY_PREFERENCE = "NY_PREFERENCE";
    public static final String EMAIL = "EMAIL";
    public static final String USERNAME = "USERNAME";


    public static final String LIST_ORDER_PREFERENCE = "LIST_ORDER_PREFERENCE";
    public static final String ORDER_BY_KEY = "orderByPushKey";

    public static String encodeEmail(String userEmail){
        return userEmail == null ? "" :userEmail.replace(".",",");
    }

    public static String decodeEmail(String userEmail) { return userEmail == null ? "" : userEmail.replace(",","."); }

}
