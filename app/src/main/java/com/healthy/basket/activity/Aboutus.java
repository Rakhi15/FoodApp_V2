package com.healthy.basket.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.healthy.basket.R;

import static com.healthy.basket.activity.MainActivity.changeStatsBarColor;
import static com.healthy.basket.activity.MainActivity.tf_opensense_regular;


public class Aboutus extends Activity {

    private ImageButton ib_back;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_aboutus);
        setAboutUscontent();
        changeStatsBarColor(Aboutus.this);
        ib_back = findViewById(R.id.ib_back);
         ib_back.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 onBackPressed();
             }
         });
        ((TextView)findViewById(R.id.txt_header)).setTypeface(tf_opensense_regular);


    }

    private void setAboutUscontent() {
        WebView web = findViewById(R.id.web);
        web.loadUrl("file:///android_asset/"+getString(R.string.aboutus_filename));

    }

}
