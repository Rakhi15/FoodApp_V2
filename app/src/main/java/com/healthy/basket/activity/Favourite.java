package com.healthy.basket.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.healthy.basket.Adapter.favadapter;
import com.healthy.basket.Getset.favgetset;
import com.healthy.basket.R;
import com.healthy.basket.utils.JazzyListView;
import com.healthy.basket.utils.sqliteHelper;

import java.util.ArrayList;

import static com.healthy.basket.activity.MainActivity.changeStatsBarColor;
import static com.healthy.basket.activity.MainActivity.tf_opensense_regular;


public class Favourite extends AppCompatActivity {
    private ArrayList<favgetset> favlist;
    private ProgressDialog progressDialog;
    String id;
    sqliteHelper sqliteHelper;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_favourite);

        changeStatsBarColor(Favourite.this);


        initViews();

    }

    private void initViews() {
        favlist = new ArrayList<>();
        ((TextView) findViewById(R.id.txt_title)).setTypeface(tf_opensense_regular);
        ImageButton ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        new getList().execute();

    }


    private class getList extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(Favourite.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }


        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub


            favlist.clear();
            sqliteHelper = new sqliteHelper(Favourite.this);
            SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();


            int i = 1;

            try {

                Cursor cur = db1.rawQuery("select * from Favourite;", null);
                Log.e("favlisting", "" + ("select * from Favourite;"));
                Log.d("SIZWA", "" + cur.getCount());
                if (cur.getCount() != 0) {
                    if (cur.moveToFirst()) {
                        do {
                            favgetset obj = new favgetset();
                            String restaurent_id = cur.getString(cur.getColumnIndex("restaurent_id"));
                            String name = cur.getString(cur.getColumnIndex("name"));
                            String category = cur.getString(cur.getColumnIndex("category"));
                            String timing = cur.getString(cur.getColumnIndex("timing"));
                            String rating = cur.getString(cur.getColumnIndex("rating"));
                            String distance = cur.getString(cur.getColumnIndex("distance"));
                            String image = cur.getString(cur.getColumnIndex("image"));
                            String address = cur.getString(cur.getColumnIndex("address"));
                            obj.setName(name);
                            obj.setCategory((category));
                            obj.setTiming(timing);
                            obj.setRating(rating);
                            obj.setDistance(distance);
                            obj.setImage(image);
                            obj.setId(restaurent_id);
                            obj.setAddress(address);
                            favlist.add(obj);
                        } while (cur.moveToNext());
                    }
                }
                cur.close();
                db1.close();
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                JazzyListView list_fav = findViewById(R.id.listview);
                if (favlist.size() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.norecord), Toast.LENGTH_LONG).show();
                    list_fav.setVisibility(View.INVISIBLE);
                } else {
                    int start = 0;
                    final favgetset tempobj = favlist.get(start);
                    list_fav = findViewById(R.id.listview);
                    list_fav.setVisibility(View.VISIBLE);
                    favadapter adapter = new favadapter(Favourite.this, favlist);
                    list_fav.setAdapter(adapter);

                    list_fav.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // TODO Auto-generated method stub

                            Intent iv = new Intent(Favourite.this, DetailPage.class);
                            iv.putExtra("res_id", favlist.get(position).getId());
                            iv.putExtra("distance", favlist.get(position).getDistance());
                            Log.e("favouriteid", "" + favlist.get(position).getId());
                            startActivity(iv);
                        }
                    });
                }

            }

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new getList().execute();
    }
}
