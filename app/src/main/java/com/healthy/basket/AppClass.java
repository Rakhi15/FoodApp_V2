package com.healthy.basket;

import androidx.multidex.MultiDexApplication;

import com.healthy.basket.utils.Preferences;

public class AppClass extends MultiDexApplication {

    public static Preferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new Preferences(this);
    }
}
