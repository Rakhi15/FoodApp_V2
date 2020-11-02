package com.healthy.basket.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.healthy.basket.Adapter.ListViewHolderCart;
import com.healthy.basket.AppClass;
import com.healthy.basket.Getset.cartgetset;
import com.healthy.basket.R;
import com.healthy.basket.utils.sqliteHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.healthy.basket.activity.MainActivity.changeStatsBarColor;


public class Cart extends AppCompatActivity {
    private static ArrayList<cartgetset> cartlist;
    private ProgressDialog progressDialog;
    private String menuid;
    private String s1;
    private float total = 0;
    private float saved = 0;
    private TextView txt_finalans;
    private TextView txt_savedans;
    private TextView txt_delivery_charge;
    private int quantity;
    ImageButton ib_back;
    private static final String MyPREFERENCES = "Fooddelivery";
    sqliteHelper sqliteHelper;
    Button btn_cart;
    String delivery_charge;
    String minimum_order;
    String time;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        changeStatsBarColor(Cart.this);

        btn_cart = findViewById(R.id.btn_cart);
        gettingIntent();
        ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getData();
    }

    private void gettingIntent() {
        Intent iv = getIntent();
        String detail_id = iv.getStringExtra("detail_id");
        String restaurent_name = iv.getStringExtra("restaurent_name");
        delivery_charge = iv.getStringExtra("delivery_charge");
        minimum_order = iv.getStringExtra("minimum_order");
        time = iv.getStringExtra("time");
        Log.e("detail_id", "" + detail_id);
        Log.e("time", "" + time);
    }

    private void getData() {

        txt_finalans = findViewById(R.id.txt_finalans);
        txt_savedans = findViewById(R.id.txt_savedans);
        txt_delivery_charge = findViewById(R.id.txt_delivery_charge);
        ((TextView) findViewById(R.id.txt_title)).setTypeface(MainActivity.tf_opensense_medium);

        cartlist = new ArrayList<>();
        new getList().execute();
    }

    private class getList extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(Cart.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
            total = 0;
            saved = 0;
        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            cartlist.clear();
            sqliteHelper = new sqliteHelper(Cart.this);
            SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
            int i = 1;
            try {
                Cursor cur = db1.rawQuery("select * from cart where foodprice >=1;", null);
                Log.e("cartlisting", "" + ("select * numberOfRecords cart where foodprice <=0;"));
                Log.d("SIZWA", "" + cur.getCount());
                StringBuilder url = new StringBuilder("cartupdate.php?ids=");
                ArrayList<HashMap<String, String>> quantity = new ArrayList<>();
                if (cur.getCount() != 0) {
                    if (cur.moveToFirst()) {
                        do {
                                url.append(cur.getString(cur.getColumnIndex("menuid"))).append(",");
                                HashMap<String , String> hashMap = new HashMap<>();
                                hashMap.put("id", cur.getString(cur.getColumnIndex("menuid")));
                                hashMap.put("quantity", cur.getString(cur.getColumnIndex("foodprice")));
                                quantity.add(hashMap);

//                            cartgetset obj = new cartgetset();
//                            String resid = cur.getString(cur.getColumnIndex("resid"));
//                            String foodid = cur.getString(cur.getColumnIndex("foodname"));
//                            menuid = cur.getString(cur.getColumnIndex("menuid"));
//                            String foodname = cur.getString(cur.getColumnIndex("foodname"));
//                            String foodprice = cur.getString(cur.getColumnIndex("foodprice"));
//                            String fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
//                            String restcurrency = cur.getString(cur.getColumnIndex("restcurrency"));
//                            String foodpriceOld = cur.getString(cur.getColumnIndex("foodpriceOld"));
//                            String foodImage = cur.getString(cur.getColumnIndex("foodImage"));
//                            String kg = cur.getString(cur.getColumnIndex("kg"));
//                            restcurrency = restcurrency.replace(AppClass.preferences.getCurrency(), "");
//                            foodpriceOld = foodpriceOld.replace(AppClass.preferences.getCurrency(), "");

//                            obj.setResid(resid);
//                            obj.setFoodid(foodid);
//                            obj.setMenuid(menuid);
//                            obj.setFoodname(foodname);
//                            obj.setFoodprice(foodprice);
//                            obj.setFooddesc(fooddesc);
//                            obj.setRestcurrency(restcurrency);
//                            obj.setFoodpriceOld(foodpriceOld);
//                            obj.setFoodImage(foodImage);
//                            obj.setKg(kg);
//                            Log.e("Cart resid", resid);
//                            Log.e("Cart foodid", foodid);
//                            Log.e("Cart menuid", menuid);
//                            Log.e("Cart foodname", foodname);
//                            Log.e("Cart foodprice", foodprice);
//                            Log.e("Cart fooddesc", fooddesc);
//                            Log.e("Cart restcurrency", restcurrency);
//                            Log.e("Cart foodpriceOld", foodpriceOld);
//                            Log.e("Cart foodImage", foodImage);
//                            Log.e("Cart kg", kg);
//                            cartlist.add(obj);
//                            try {
//                                float quant = Float.parseFloat(foodprice);
//                                float single = Float.parseFloat(restcurrency);
//                                float singleOld = Float.parseFloat(foodpriceOld);
//                                Log.e("12345", "" + quant + single);
//                                Log.e("12345", "" + quant + singleOld);
//                                float totalsum = quant * single;
//                                float totalsaved = quant * singleOld;
//                                total = totalsum + total;
//                                saved = totalsaved + saved;
//                            } catch (NumberFormatException e) {
//                                Log.e("Error", e.getMessage());
//                            }
                        } while (cur.moveToNext());
                    }
                    url.deleteCharAt(url.length()-1);
                    updateCart(url.toString(), quantity);
                }
//                url.deleteCharAt(url.length()-1);
//                updateCart(url.toString(), quantity);
                cur.close();
                db1.close();
            } catch (Exception e) {
                Log.e("Cart Exception", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            ListView list_cart = findViewById(R.id.list_cart);
            Log.d("file", "" + cartlist.size());
            if (cartlist.size() == 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.norecord), Toast.LENGTH_LONG).show();
                list_cart.setVisibility(View.INVISIBLE);
                total = 0;
                saved = 0;
                txt_finalans.setText("");
                txt_savedans.setText("");
                txt_delivery_charge.setText("");
                btn_cart.setText("0");
            } else {
                list_cart = findViewById(R.id.list_cart);
                Button btncheckout = findViewById(R.id.btn_checkout);
                btncheckout.setTypeface(MainActivity.tf_opensense_regular);
                btncheckout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag = false;
                        for (int i=0; i<cartlist.size();i++){
                            if (cartlist.get(i).getIsActive().equals("1")) {
                                flag = false;
                            }
                            else {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            if (total >= Integer.parseInt(minimum_order)) {
                                String[] dates = time.split(" ");
                                DateFormat df = new SimpleDateFormat("HH:mm");
                                String currentTime = df.format(Calendar.getInstance().getTime());
//                            Log.e("opening Time", dates[0]);
//                            Log.e("closing Time", dates[2]);
//                            Log.e("current Time", currentTime);
                                if (!checkTime(dates[0], dates[2], currentTime)) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(Cart.this, R.style.CustomAlertDialog);
                                    dialog.setTitle("Alert");
                                    dialog.setMessage(R.string.res_open_msg);
                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            placeOrder();
                                        }
                                    });
                                    dialog.show();
                                } else {
                                    placeOrder();
                                }
                            } else
                                Toast.makeText(Cart.this, "Minimum Order " + getString(R.string.rs) + " " + minimum_order, Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(Cart.this, "Please Remove Out of stock items", Toast.LENGTH_LONG).show();
                    }
                });
                cartadapter1 adapter = new cartadapter1(Cart.this, cartlist, menuid, quantity);
                list_cart.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                list_cart.getAdapter().getCount();
                String totalcartitem = String.valueOf(list_cart.getAdapter().getCount());
                if (totalcartitem.equals("0")) {
                    btncheckout.setEnabled(false);
                    Toast.makeText(getApplicationContext(), R.string.cart_empty, Toast.LENGTH_SHORT).show();
                } else {
                    btncheckout.setEnabled(true);
                }
                list_cart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    }
                });
                double roundOff = (double) Math.round(total * 100) / 100;
                double delivery_chrg = Double.parseDouble(delivery_charge);
                double totalChrg = roundOff+delivery_chrg;
                txt_delivery_charge.setText(AppClass.preferences.getCurrency() + " " + delivery_charge);
                txt_finalans.setText(AppClass.preferences.getCurrency() + " " + totalChrg);
                txt_savedans.setText(AppClass.preferences.getCurrency() + " " + (saved-roundOff));
                btn_cart.setText(totalcartitem);
            }
        }

        void updateCart(String url, ArrayList<HashMap<String, String>> quantity){
            String user = getString(R.string.link) + getString(R.string.servicepath) +url;
            Log.e("Cart", user);
            try {
                URL hp = new URL(user.replace(" ", "%20"));
                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(input));
                String x;
                x = r.readLine();
                StringBuilder totalB = new StringBuilder();
                while (x != null) {
                    totalB.append(x);
                    x = r.readLine();
                }

                JSONArray jObject = new JSONArray(totalB.toString());
                JSONObject obj1 = jObject.getJSONObject(0);
                switch (obj1.getString("status")) {
                    case "Success":
                        JSONArray jsonArray = obj1.getJSONArray("Cart_List");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            cartgetset obj = new cartgetset();
                            JSONObject Obj = jsonArray.getJSONObject(i);
                            String resid = Obj.getString("res_id");
                            String foodid = Obj.getString("name");
                            menuid = Obj.getString("id");
                            String foodname = Obj.getString("name");
                            String foodprice = "1";
                            String fooddesc = Obj.getString("desc");
                            String restcurrency = Obj.getString("price");
                            String foodpriceOld = Obj.getString("oldprice");
                            String foodImage = Obj.getString("photo");
                            String IsActive = Obj.getString("IsActive");
                            String kg = "1";
                            for (int j=0;j<quantity.size();j++){
                                Log.e("resid", menuid);
                                if (quantity.get(j).get("id").equals(menuid)){
                                    foodprice = quantity.get(j).get("quantity");
                                    break;
                                }

                            }
                            restcurrency = restcurrency.replace(AppClass.preferences.getCurrency(), "");
                            foodpriceOld = foodpriceOld.replace(AppClass.preferences.getCurrency(), "");

                            obj.setResid(resid);
                            obj.setFoodid(foodid);
                            obj.setMenuid(menuid);
                            obj.setFoodname(foodname);
                            obj.setFoodprice(foodprice);
                            obj.setFooddesc(fooddesc);
                            obj.setRestcurrency(restcurrency);
                            obj.setFoodpriceOld(foodpriceOld);
                            obj.setFoodImage(foodImage);
                            obj.setIsActive(IsActive);
                            obj.setKg(kg);
                            updateDb(restcurrency, foodpriceOld, menuid);
                            cartlist.add(obj);
                            try {
                                float quant = Float.parseFloat(foodprice);
                                float single = Float.parseFloat(restcurrency);
                                float singleOld = Float.parseFloat(foodpriceOld);
                                float totalsum = quant * single;
                                float totalsaved = quant * singleOld;
                                total = totalsum + total;
                                saved = totalsaved + saved;
                            } catch (NumberFormatException e) {
                                Log.e("Error", e.getMessage());
                            }

                        }
                    case "Failed":
                      //  Error = obj.getString("error");


                        break;
                    default:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Cart.this, R.string.txt_try_later, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        void updateDb(String priceNew, String priceOld, String menuId){
            sqliteHelper = new sqliteHelper(Cart.this);
            SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();

            try {
                Cursor cur = db1.rawQuery("UPDATE cart SET restcurrency ='" + priceNew + "', foodpriceOld = '"+priceOld+"' Where menuid ='" + menuId + "';", null);
                //Log.e("updatequeryminus", "" + "UPDATE cart SET restcurrency ='" + priceNew + "' Where menuid ='" + menuId + "';");
                //Log.e("SIZWA", "" + cur.getCount());
//                if (cur.getCount() != 0) {
//                    if (cur.moveToFirst()) {
//                        do {
//                            cartgetset obj = new cartgetset();
//                            menuid321 = cur.getString(cur.getColumnIndex("menuid"));
//                            foodid = cur.getString(cur.getColumnIndex("foodid"));
//                            foodname = cur.getString(cur.getColumnIndex("foodname"));
//                            foodprice321 = cur.getString(cur.getColumnIndex("foodprice"));
//                            fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
//                            restcurrency321 = cur.getString(cur.getColumnIndex("restcurrency"));
//                            foodpriceOld = cur.getString(cur.getColumnIndex("foodpriceOld"));
//                            kg = cur.getString(cur.getColumnIndex("kg"));
//                            obj.setFoodid(foodid);
//                            obj.setMenuid(menuid321);
//                            obj.setFoodname(foodname);
//                            obj.setFoodprice(foodprice321);
//                            obj.setFooddesc(fooddesc);
//                            obj.setRestcurrency(restcurrency321);
//                            obj.setFoodpriceOld(foodpriceOld);
//                            obj.setKg(kg);
//                            Log.e("menuid321updatedcart", "" + menuid321);
//                            Log.e("foodp321updatedcart", "" + foodprice321);
//                            data1.add(obj);
//                            new getList().execute();
//                        } while (cur.moveToNext());
//                    }
//                }
                cur.close();
                db1.close();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }

    private boolean checkingSignIn() {
        //getting shared preference
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Log.e("user", "" + prefs.getString("userid", null));
        // check user is created or not
        // if user is already logged in
        if (prefs.getString("userid", null) != null) {
            String userid = prefs.getString("userid", null);
            return !userid.equals("delete");
        } else {
            return false;
        }
    }

    private boolean checkTime(String initialTime, String finalTime,
                              String currentTime) {
        boolean valid = false;
        try {
            Date inTime = new SimpleDateFormat("HH:mm").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            Date checkTime = new SimpleDateFormat("HH:mm").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            Date finTime = new SimpleDateFormat("HH:mm").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) ||
                    actualTime.compareTo(calendar1.getTime()) == 0) &&
                    actualTime.before(calendar2.getTime())) {
                valid = true;
                return valid;
            } else {
                Log.e("Error","Not a valid time, expecting HH:MM:SS format");
            }
        }catch (ParseException e){
            e.printStackTrace();
        }
        return valid;
    }

    private void placeOrder(){
        if (checkingSignIn()) {
            Intent iv = new Intent(Cart.this, PlaceOrder.class);
            iv.putExtra("order_price", "" + s1);
            iv.putExtra("res_id", menuid);
            iv.putExtra("delivery_charge", delivery_charge);
            startActivity(iv);
        } else {
            Intent iv = new Intent(Cart.this, Login.class);
            iv.putExtra("key", "PlaceOrder");
            iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(iv);
            Toast.makeText(Cart.this, R.string.loginmsg, Toast.LENGTH_SHORT).show();
        }
    }

    class cartadapter1 extends BaseAdapter {
        final ArrayList<Integer> quantity = new ArrayList<>();
        SQLiteDatabase db;
        Cursor cur = null;
        final String menuid1;
        String menuid321, foodprice321, restcurrency321, foodpriceOld, kg;
        final int quen;
        String foodid, foodname, fooddesc;
        int val1, val1kg;
        float add, sub,add1, sub1;
        private final ArrayList<cartgetset> data1;
        private final Activity activity;
        private LayoutInflater inflater = null;

        cartadapter1(Activity a, ArrayList<cartgetset> str, String menuid, int quantity) {
            activity = a;
            data1 = str;
            menuid1 = menuid;
            quen = quantity;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < data1.size(); i++) {
                this.quantity.add(i);
            }
        }

        @Override
        public int getCount() {
            return data1.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row;
            final ListViewHolderCart listViewHolder;
            Typeface opensansregular = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular.ttf");
            if (convertView == null) {
                row = inflater.inflate(R.layout.cell_cart, parent, false);
                listViewHolder = new ListViewHolderCart();
                listViewHolder.txt_name1 = row.findViewById(R.id.txt_name1);
                listViewHolder.txt_name1.setTypeface(opensansregular);
                listViewHolder.txt_desc = row.findViewById(R.id.txt_desc);
                listViewHolder.txt_desc.setTypeface(opensansregular);
                listViewHolder.txt_totalprice = row.findViewById(R.id.txt_totalprice);
                listViewHolder.txt_basic_priceMRP = row.findViewById(R.id.txt_basic_priceMRP);
                listViewHolder.txt_basic_priceMRP.setTypeface(opensansregular);
                listViewHolder.txt_basic_price = row.findViewById(R.id.txt_basic_price);
                listViewHolder.txt_basic_price.setPaintFlags(listViewHolder.txt_basic_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                listViewHolder.txt_totalprice.setTypeface(MainActivity.tf_opensense_medium);
                listViewHolder.txt_basic_price.setTypeface(opensansregular);
                listViewHolder.txt_quantity = row.findViewById(R.id.txt_quantity);
                listViewHolder.txt_quantity.setTypeface(opensansregular);
                listViewHolder.btn_minus1_size = row.findViewById(R.id.btn_minus1_size);
                listViewHolder.btn_plus_size = row.findViewById(R.id.btn_plus_size);
                listViewHolder.edTextQuantitySize = row.findViewById(R.id.edTextQuantitySize);
                listViewHolder.edTextQuantitySize.setTypeface(opensansregular);
                listViewHolder.txt_item_saved_price = row.findViewById(R.id.txt_item_saved_price);
                listViewHolder.txt_item_saved_price.setTypeface(opensansregular);
                listViewHolder.txt_item_saved = row.findViewById(R.id.txt_item_saved);
                listViewHolder.txt_item_saved.setTypeface(opensansregular);
                listViewHolder.txt_outOfStock = row.findViewById(R.id.txt_outOfStock);
                listViewHolder.txt_outOfStock.setTypeface(opensansregular);
                listViewHolder.image = row.findViewById(R.id.cellCartImage);


                listViewHolder.btn_plus = row.findViewById(R.id.btn_plus);
                listViewHolder.btn_minus1 = row.findViewById(R.id.btn_minus1);
                listViewHolder.edTextQuantity = row.findViewById(R.id.edTextQuantity);
                listViewHolder.edTextQuantity.setTypeface(opensansregular);
                listViewHolder.btn_plus.setTag(listViewHolder);
                listViewHolder.btn_plus_size.setTag(listViewHolder);
                listViewHolder.btn_minus1.setTag(listViewHolder);
                listViewHolder.btn_minus1_size.setTag(listViewHolder);
                row.setTag(listViewHolder);
            } else {
                row = convertView;
                listViewHolder = (ListViewHolderCart) row.getTag();
            }
            if (data1.get(position).getFoodpriceOld().equals(data1.get(position).getRestcurrency())){
                listViewHolder.txt_basic_priceMRP.setVisibility(View.GONE);
                listViewHolder.txt_basic_price.setVisibility(View.GONE);
            }

            listViewHolder.txt_name1.setText((data1.get(position).getFoodname()));
            listViewHolder.txt_desc.setText(data1.get(position).getFooddesc());
//            listViewHolder.txt_totalprice.setText(AppClass.preferences.getCurrency() + " " + Integer.parseInt(data1.get(position).getRestcurrency()));
            listViewHolder.txt_totalprice.setText(AppClass.preferences.getCurrency() + " " + calculateTotal(data1.get(position).getRestcurrency(),data1.get(position).getFoodprice()));
            listViewHolder.txt_basic_price.setText(AppClass.preferences.getCurrency() + " " + calculateTotal(data1.get(position).getFoodpriceOld(),data1.get(position).getFoodprice()));
            listViewHolder.edTextQuantity.setText(data1.get(position).getFoodprice());
            listViewHolder.txt_item_saved_price.
                    setText(AppClass.preferences.getCurrency() + " " + calculateSaved(data1.get(position).getRestcurrency(),data1.get(position).getFoodpriceOld(),data1.get(position).getFoodprice()));
            listViewHolder.edTextQuantitySize.setText(data1.get(position).getKg());
            Picasso.with(Cart.this)
                    .load(getString(R.string.link)+getString(R.string.imagepath)+data1.get(position).getFoodImage())
                    .error(R.drawable.no_image_available).into(listViewHolder.image);
            val1 = Integer.parseInt(data1.get(position).getFoodprice());
            val1kg = Integer.parseInt(data1.get(position).getKg());

            if (data1.get(position).getIsActive().equals("1")){
                listViewHolder.txt_outOfStock.setVisibility(View.GONE);
            }else {
                listViewHolder.txt_outOfStock.setVisibility(View.VISIBLE);
            }


            listViewHolder.btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View p = (View) v.getParent();
                    ListViewHolderCart holder1 = (ListViewHolderCart) (v.getTag());
                    val1 = Integer.valueOf(holder1.edTextQuantity.getText().toString());
                    val1++;
                    val1kg = Integer.valueOf(holder1.edTextQuantitySize.getText().toString());

                    holder1.txt_totalprice.setText(AppClass.preferences.getCurrency() + " " + calculateTotal(data1.get(position).getRestcurrency().trim(),String.valueOf(val1)));
                    holder1.txt_basic_price.setText(AppClass.preferences.getCurrency() + " " + calculateTotal(data1.get(position).getFoodpriceOld().trim(),String.valueOf(val1)));
                    holder1.txt_item_saved_price.setText(AppClass.preferences.getCurrency() + " " + calculateSaved(data1.get(position).getRestcurrency().trim(),data1.get(position).getFoodpriceOld().trim(),String.valueOf(val1)));
                    add = Float.parseFloat(data1.get(position).getRestcurrency().trim());
                    add1 = Float.parseFloat(data1.get(position).getFoodpriceOld().trim());
                    total = total + add;
                    saved = saved + add1;

                    Log.e("totalansadd ", "" + total);
                    double roundOff = (double) Math.round(total * 100) / 100;

                    double delivery_chrg = Double.parseDouble(delivery_charge);
                    double totalChrg = roundOff+delivery_chrg;
                    txt_delivery_charge.setText(AppClass.preferences.getCurrency() + " " + delivery_charge);
                    txt_finalans.setText(AppClass.preferences.getCurrency() + " " + totalChrg);


                   // txt_finalans.setText(String.format("%s %s", AppClass.preferences.getCurrency(), roundOff));
                    txt_savedans.setText(String.format("%s %s", AppClass.preferences.getCurrency(), (saved-roundOff)));

                    holder1.edTextQuantity.setText(String.valueOf(val1));
                    sqliteHelper = new sqliteHelper(Cart.this);
                    SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
                    cartlist.get(position).setFoodprice(String.valueOf(val1));
                    try {
                        cur = db1.rawQuery("UPDATE cart SET foodprice ='" + val1 + "' Where menuid ='" + data1.get(position).getMenuid() + "';", null);
                        Log.e("updatequeryplus", "" + "UPDATE cart SET foodprice ='" + val1 + "' Where menuid ='" + data1.get(position).getMenuid() + "';");
                        Log.e("SIZWA", "" + cur.getCount());
                        if (cur.getCount() != 0) {
                            if (cur.moveToFirst()) {
                                do {
                                    cartgetset obj = new cartgetset();
                                    menuid321 = cur.getString(cur.getColumnIndex("menuid"));
                                    foodid = cur.getString(cur.getColumnIndex("foodid"));
                                    foodname = cur.getString(cur.getColumnIndex("foodname"));
                                    foodprice321 = cur.getString(cur.getColumnIndex("foodprice"));
                                    fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
                                    restcurrency321 = cur.getString(cur.getColumnIndex("restcurrency"));
                                    foodpriceOld = cur.getString(cur.getColumnIndex("foodpriceOld"));
                                    kg = cur.getString(cur.getColumnIndex("kg"));
                                    obj.setFoodid(foodid);
                                    obj.setMenuid(menuid321);
                                    obj.setFoodname(foodname);
                                    obj.setFoodprice(foodprice321);
                                    obj.setFooddesc(fooddesc);
                                    obj.setRestcurrency(restcurrency321);
                                    obj.setFoodpriceOld(foodpriceOld);
                                    obj.setKg(kg);
                                    Log.e("menuid321updatedcart", "" + menuid321);
                                    Log.e("foodp321updatedcart", "" + foodprice321);
                                    data1.add(obj);
                                    new getList().execute();
                                } while (cur.moveToNext());
                            }
                        }
                        cur.close();
                        db1.close();
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());

                    }
                }
            });

            listViewHolder.btn_minus1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListViewHolderCart holder1 = (ListViewHolderCart) (v.getTag());
//                    String sub_price = holder1.txt_totalprice.getText().toString().replace(AppClass.preferences.getCurrency(), "").trim();
//
//                    sub = Float.valueOf(sub_price);
                    val1 = Integer.valueOf(holder1.edTextQuantity.getText().toString());
                    if (val1 <= 1) {
                        sqliteHelper = new sqliteHelper(Cart.this);
                        SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();

                        String selection = "menuid =? ";
                        String[] selectionArgs = {data1.get(position).getMenuid()};
                        int deleteRow = db1.delete("cart", selection, selectionArgs);

                        new getList().execute();


                    } else {

                        val1kg = Integer.valueOf(holder1.edTextQuantitySize.getText().toString());
                        val1--;
                        holder1.txt_totalprice.setText(AppClass.preferences.getCurrency() + " " + calculateTotal(data1.get(position).getRestcurrency().trim(),String.valueOf(val1)));
                        holder1.txt_basic_price.setText(AppClass.preferences.getCurrency() + " " + calculateTotal(data1.get(position).getFoodpriceOld().trim(),String.valueOf(val1)));
                        holder1.txt_item_saved_price.setText(AppClass.preferences.getCurrency() + " " + calculateSaved(data1.get(position).getRestcurrency().trim(),data1.get(position).getFoodpriceOld().trim(),String.valueOf(val1)));

                        sub = Float.parseFloat(data1.get(position).getRestcurrency().trim());
                        sub1 = Float.parseFloat(data1.get(position).getFoodpriceOld().trim());

                        total = total - sub;
                        saved = saved - sub1;
                        Log.e("totalanssub ", "" + total);
                        double roundOff = (double) Math.round(total * 100) / 100;

                        double delivery_chrg = Double.parseDouble(delivery_charge);
                        double totalChrg = roundOff+delivery_chrg;
                        txt_delivery_charge.setText(AppClass.preferences.getCurrency() + " " + delivery_charge);
                        txt_finalans.setText(AppClass.preferences.getCurrency() + " " + totalChrg);

                       // txt_finalans.setText(AppClass.preferences.getCurrency() + " " + roundOff);
                        txt_savedans.setText(AppClass.preferences.getCurrency() + " " + (saved-roundOff));
                        holder1.edTextQuantity.setText(String.valueOf(val1));


                        sqliteHelper = new sqliteHelper(Cart.this);
                        SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();

                        cartlist.get(position).setFoodprice(String.valueOf(val1));
                        try {
                            cur = db1.rawQuery("UPDATE cart SET foodprice ='" + val1 + "' Where menuid ='" + data1.get(position).getMenuid() + "';", null);
                            Log.e("updatequeryminus", "" + "UPDATE cart SET foodprice ='" + val1 + "' Where menuid ='" + data1.get(position).getMenuid() + "';");
                            Log.e("SIZWA", "" + cur.getCount());
                            if (cur.getCount() != 0) {
                                if (cur.moveToFirst()) {
                                    do {
                                        cartgetset obj = new cartgetset();
                                        menuid321 = cur.getString(cur.getColumnIndex("menuid"));
                                        foodid = cur.getString(cur.getColumnIndex("foodid"));
                                        foodname = cur.getString(cur.getColumnIndex("foodname"));
                                        foodprice321 = cur.getString(cur.getColumnIndex("foodprice"));
                                        fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
                                        restcurrency321 = cur.getString(cur.getColumnIndex("restcurrency"));
                                        foodpriceOld = cur.getString(cur.getColumnIndex("foodpriceOld"));
                                        kg = cur.getString(cur.getColumnIndex("kg"));
                                        obj.setFoodid(foodid);
                                        obj.setMenuid(menuid321);
                                        obj.setFoodname(foodname);
                                        obj.setFoodprice(foodprice321);
                                        obj.setFooddesc(fooddesc);
                                        obj.setRestcurrency(restcurrency321);
                                        obj.setFoodpriceOld(foodpriceOld);
                                        obj.setKg(kg);
                                        Log.e("menuid321updatedcart", "" + menuid321);
                                        Log.e("foodp321updatedcart", "" + foodprice321);
                                        data1.add(obj);
                                        new getList().execute();
                                    } while (cur.moveToNext());
                                }
                            }
                            cur.close();
                            db1.close();
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                        }
                    }
                }
            });

            return row;
        }
    }

    private String calculateTotal(String origonalPrice, String qty) {
        try {
            int price = Integer.parseInt(origonalPrice);
            int intQty = Integer.parseInt(qty);
            price = price * intQty;
            return String.valueOf(price);
        }catch (NumberFormatException e){
            e.printStackTrace();
            return origonalPrice;
        }
    }
    private String calculateSaved(String origonalPrice, String oldPrice, String qty) {
        try {
            int price = Integer.parseInt(origonalPrice);
            int old = Integer.parseInt(oldPrice);
            int q = Integer.parseInt(qty);
            price = old-price;
            price = price*q;
            return String.valueOf(price);
        }catch (NumberFormatException e){
            e.printStackTrace();
            return String.valueOf(0);
        }
    }
}