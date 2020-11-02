package com.healthy.basket.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public static final String
            PREF_NAME = "DELIVERYAPP",
            TOKEN = "TOKEN",
            USERID = "USERID",
            USER_NAME = "USER_NAME",
            COUNTRY_CODE = "COUNTRY_CODE",
            PHONE_NUMBER = "PHONE_NUMBER",
            PASSWORD = "PASSWORD",
            EMAIL = "EMAIL",
            PROFILE_PIC = "PROFILE_PIC",
            FIRST_NAME = "FIRST_NAME",
            LAST_NAME = "LAST_NAME",
            ROSTER = "ROSTER",
            ROSTER_GLOBAL = "ROSTER_GLOBAL",
            ROOM = "ROOM",
            IS_REMEMBERME = "IS_REMEMBERME",
            PROFILE_PIC_PLAIN = "PROFILE_PIC_PLAIN",
            CURRENCY = "CURRENCY";


    public Preferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, 0);
    }

    public void clearPrefs() {
        editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public void storeDataIntoPreFerance(String key, String value) {
        editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getValueFromPreferance(String key) {
        return prefs.getString(key, "");
    }

    public String getCurrency(){
        return prefs.getString(Preferences.CURRENCY,"₹");
    }


}
