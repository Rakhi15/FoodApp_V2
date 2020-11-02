package com.healthy.basket.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.healthy.basket.R;

import io.fabric.sdk.android.Fabric;

import static com.healthy.basket.activity.MainActivity.changeStatsBarColor;


public class Splash extends AppCompatActivity {
    private static final String MY_PREFS_NAME = "Fooddelivery";
    private boolean isDeliveryAccountActive = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView( R.layout.activity_splash_screen);
        changeStatsBarColor(Splash.this);


        int SPLASH_TIME_OUT = getResources().getInteger(R.integer.splash_time_out);
        new Handler().postDelayed(new Runnable() {

                                      /*
                                       * Showing splash screen with a timer. This will be useful when you
                                       * want to show case your app logo / company
                                       */

                                      @Override
                                      public void run() {
                                          SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                                          isDeliveryAccountActive = prefs.getBoolean("isDeliverAccountActive", false);
                                          if (isDeliveryAccountActive) {
                                              Intent iv = new Intent(Splash.this, DeliveryStatus.class);
                                              startActivity(iv);
                                              finish();
                                          } else {
                                                  Intent iv = new Intent(Splash.this, MainActivity.class);
                                                  startActivity(iv);
                                                  finish();

                                          }
                                      }
                                  },
                SPLASH_TIME_OUT);
    }

}

