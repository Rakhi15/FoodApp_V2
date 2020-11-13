package com.healthy.basket.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.healthy.basket.AppClass;
import com.healthy.basket.Getset.myOrderGetSet;
import com.healthy.basket.Getset.orderDetailGetSet;
import com.healthy.basket.R;
import com.healthy.basket.utils.sqliteHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.healthy.basket.activity.MainActivity.changeStatsBarColor;
import static com.healthy.basket.activity.MainActivity.checkInternet;
import static com.healthy.basket.activity.MainActivity.showErrorDialog;
import static com.healthy.basket.activity.MainActivity.tf_opensense_medium;
import static com.healthy.basket.activity.MainActivity.tf_opensense_regular;


public class MyOrderPage extends AppCompatActivity {
    private ListView list_notification;
    private ArrayList<myOrderGetSet> data;
    private notificationAdapter adapter;
    AdRequest adRequest;
    AdView mAdView;
   sqliteHelper sqliteHelper;
    private InterstitialAd mInterstitialAd;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_myorder);
        getSupportActionBar().hide();
        changeStatsBarColor(MyOrderPage.this);

        initialization();

        if (checkInternet(MyOrderPage.this)) {
            AdShow();
            new GetDataAsyncTask().execute();
        } else showErrorDialog(MyOrderPage.this);


    }


    class GetDataAsyncTask extends AsyncTask<Void, Void, Integer> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MyOrderPage.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            gettingDataFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                if (data != null) {
                    adapter = new notificationAdapter(data);
                    list_notification.setAdapter(adapter);


                } else
                    Toast.makeText(MyOrderPage.this, R.string.noorder, Toast.LENGTH_SHORT).show();
            }


        }
    }


    private void gettingDataFromDatabase() {
        sqliteHelper = new sqliteHelper(MyOrderPage.this);
        SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
        Cursor cur;
        try {
            cur = db1.rawQuery("SELECT * FROM order_detail;", null);
            data = new ArrayList<>();
            Log.e("SIZWA", "" + cur.getCount());
            if (cur.getCount() != 0) {
                if (cur.moveToFirst()) {
                    do {
                        myOrderGetSet obj = new myOrderGetSet();
                        String txt_restaurantName = cur.getString(cur.getColumnIndex("restaurantName"));
                        String txt_restaurantAddress = cur.getString(cur.getColumnIndex("restaurantAddress"));
                        String txt_orderId = cur.getString(cur.getColumnIndex("orderId"));
                        String txt_orderAmount = cur.getString(cur.getColumnIndex("orderAmount"));
                        String txt_orderTime = cur.getString(cur.getColumnIndex("orderTime"));
                        try {
                            URL hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "/order_details.php?order_id=" + txt_orderId);
                            Log.e("URLmenu", "" + hp);
                            URLConnection ohpCon = hp.openConnection();
                            ohpCon.connect();
                            InputStream oinput = ohpCon.getInputStream();
                            Log.d("input", "" + oinput);
                            BufferedReader schedulerReader = new BufferedReader(new InputStreamReader(oinput));
                            String y;
                            y = schedulerReader.readLine();
                            StringBuilder totalBuilder = new StringBuilder();
                            while (y != null) {
                                totalBuilder.append(y);
                                y = schedulerReader.readLine();
                            }

                            JSONObject jobj = new JSONObject(totalBuilder.toString());
                            if (jobj.getString("success").equals("1")) {


                                JSONArray jo_array= jobj.getJSONArray("order_details");
                                JSONObject jo_order = jo_array.getJSONObject(0);
                                String is_scheduled = jo_order.getString("is_scheduled");
                                if(is_scheduled.equals("1")){
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    Date finalDay = (Date) sdf.parse(jo_order.getString("delivery_time"));
                                    Long daysLeft =
                                            (( finalDay.getTime()- System.currentTimeMillis()) / 1000)/86400;
                                    obj.setIs_scheduled(true);
                                    String deliveryDays = String.format("%d", daysLeft);
                                    obj.setScheduled_time(deliveryDays);
                                }

                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        obj.setResName(txt_restaurantName);
                        obj.setResAddress(txt_restaurantAddress);
                        obj.setOrder_id(txt_orderId);
                        obj.setOrder_dateTime(txt_orderTime);
                        obj.setOrder_total(txt_orderAmount);


                        data.add(obj);
                    } while (cur.moveToNext());
                }
            }
            cur.close();
            db1.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());

        }

    }

    private void initialization() {
        list_notification = findViewById(R.id.list_notification);
        ((TextView) findViewById(R.id.txt_title)).setTypeface(tf_opensense_regular);
        ImageButton ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        list_notification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(MyOrderPage.this, CompleteOrder.class);
                i.putExtra("orderid", adapter.dat.get(position).getOrder_id());
                i.putExtra("key", "notification");
                startActivity(i);
                if (getResources().getString(R.string.show_admob_ads).equals("yes")) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();

                    }
                }
            }
        });

    }

    private class notificationAdapter extends BaseAdapter {
        final ArrayList<myOrderGetSet> dat;
        private LayoutInflater inflater = null;

        notificationAdapter(ArrayList<myOrderGetSet> dat) {
            this.dat = dat;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return dat.size();
        }

        @Override
        public Object getItem(int position) {
            return dat.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if (vi == null)
                vi = inflater.inflate(R.layout.cell_order, parent, false);


            TextView txt_name = vi.findViewById(R.id.txt_name);
            txt_name.setText(dat.get(position).getResName());
            txt_name.setTypeface(tf_opensense_medium);
            TextView txt_address = vi.findViewById(R.id.txt_address);
            txt_address.setText(dat.get(position).getResAddress());
            txt_address.setTypeface(tf_opensense_regular);

            TextView txt_res_name = vi.findViewById(R.id.txt_res_name);
            txt_res_name.setText(dat.get(position).getResName().substring(0, 1));

            TextView txt_order_date = vi.findViewById(R.id.txt_order_date);
            if(dat.get(position).isIs_scheduled()) {

                int deliveryDueDate =Integer.parseInt(dat.get(position).getScheduled_time());
                if(deliveryDueDate<0){
                    txt_order_date.setText("");
                }
                else if(dat.get(position).getScheduled_time().equals("0"))
                    txt_order_date.setText("Arriving Today");
                else
                    txt_order_date.setText("Arriving In "+dat.get(position).getScheduled_time()+ " Day(s)");
            }else
                txt_order_date.setText("Order Date: " + dat.get(position).getOrder_dateTime());

            TextView txt_total_tittle = vi.findViewById(R.id.txt_total_tittle);
            TextView txt_total = vi.findViewById(R.id.txt_total);
            txt_total.setText(AppClass.preferences.getCurrency() + " " + dat.get(position).getOrder_total());


            txt_res_name.setTypeface(tf_opensense_regular);
            txt_order_date.setTypeface(tf_opensense_regular);
            txt_total_tittle.setTypeface(tf_opensense_regular);
            txt_total.setTypeface(tf_opensense_regular);


            return vi;
        }
    }

    public void AdShow() {

        if (getResources().getString(R.string.show_admob_ads).equals("yes")) {
            adRequest = new AdRequest.Builder().build();
            mAdView = (AdView) findViewById(R.id.adView);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int i) {

                    mAdView.loadAd(adRequest);

                }
            });
        }
        if (getResources().getString(R.string.show_admob_ads).equals("yes")) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_insertitial_id));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    Log.d("InterstitialAd", "onAdFailedToLoad");
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                    Log.d("InterstitialAd", "onAdLeftApplication");
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Log.d("InterstitialAd", "onAdOpened");
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.d("InterstitialAd", "onAdLoaded");
                }
            });
        }


    }

}
