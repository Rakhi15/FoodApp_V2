package com.healthy.basket.activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.healthy.basket.R;
import com.healthy.basket.utils.sqliteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MuttonOrderActivity extends AppCompatActivity {


    RecyclerView dataList;

    List<String> title;
    List<String> quantity;
    List<String> mrp;
    List<String> price;
    List<String> images;
    List<String> itemId;
    List<String> desc;
    List<String> menuId;
    List<String> curQuantities;
    String api_getP="https://healthybaskets.co/api/getScheduledItems.php?res_id=";
    int res_id;
    AdapterRecyclerNew adapter;
    ProgressDialog progressDialog;
    HashMap<String, String> prevCartQuantities;

    /*
    private int mini_qnt=500, qnt_price=300, res_qnt=1, res_price, tempqnt=500;
    TextView txt_quantity_display, txt_minus_qnt, txt_plus_qnt, txt_reserved_qnt, txt_reserved_price;
    CardView add_cart;

    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_mutton);


        dataList=findViewById(R.id.recyclerviewRakhi);
        /*

        txt_quantity_display=findViewById(R.id.txt_AddToCart_qty);
        txt_minus_qnt=findViewById(R.id.txt_AddToCart_qtyMinus);
        txt_plus_qnt=findViewById(R.id.txt_AddToCart_qtyPlus);
        add_cart=findViewById(R.id.card_AddToCart_add);
        txt_reserved_qnt=findViewById(R.id.txt_Quantity);
        txt_reserved_price=findViewById(R.id.txt_AddToCart_amount);


        calculateRaji();

        txt_plus_qnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res_qnt++;
                tempqnt=tempqnt+mini_qnt;
                calculateRaji();
            }
        });
        txt_minus_qnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res_qnt--;
                tempqnt=tempqnt-mini_qnt;
                calculateRaji();
            }
        });

        add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MuttonOrderActivity.this, "Rey Code Rayi Raa Mama...", Toast.LENGTH_LONG).show();


            }
        });


        */

        progressDialog=new ProgressDialog(MuttonOrderActivity.this);
        progressDialog.setMessage("Please Wait......");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        title=new ArrayList<>();
        images=new ArrayList<>();
        quantity=new ArrayList<>();
        mrp=new ArrayList<>();
        price=new ArrayList<>();
        itemId=new ArrayList<>();
        desc=new ArrayList<>();
        menuId=new ArrayList<>();
        curQuantities=new ArrayList<>();
        res_id=this.getIntent().getIntExtra("res_id",7);

        letsShakeRey();

        adapter = new AdapterRecyclerNew(this, title, images,quantity, mrp, price,itemId,desc,menuId,curQuantities);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        dataList.setLayoutManager(gridLayoutManager);
        dataList.setAdapter(adapter);


    }

    private void letsShakeRey() {
        progressDialog.show();

        StringRequest request=new StringRequest(Request.Method.GET, api_getP+res_id, new Response.Listener<String>() {
             @Override
            public void onResponse(String response) {
                try {

                    com.healthy.basket.utils.sqliteHelper sqliteHelper = new sqliteHelper(getApplicationContext());
                    SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();

                    try {
                        Cursor cur = db1.rawQuery("select * from cart where foodprice >=1;", null);
                        Log.e("cartlisting", "" + ("select * numberOfRecords cart where foodprice <=0;"));
                        Log.d("SIZWA", "" + cur.getCount());

                         prevCartQuantities = new HashMap<>();
                        if (cur.getCount() != 0) {
                            if (cur.moveToFirst()) {
                                do {

                                    prevCartQuantities.put(cur.getString(cur.getColumnIndex("menuid")),cur.getString(cur.getColumnIndex("foodprice")));
                                }while(cur.moveToNext());
                            }
                        }
                    }catch(Exception e){
                        //continue without populating quantities;
                    }

                                    JSONObject jsonObject=new JSONObject(response);
                    String success=jsonObject.getString("status");
                    JSONArray jsonArray=jsonObject.getJSONArray("Menu_List");

                    if (success.equals("Success")){
                        AdapterRecyclerNew.res_id=res_id;
                        for (int i=0; i<jsonArray.length(); i++){
                            JSONObject object=jsonArray.getJSONObject(i);

                            String name=object.getString("name");
                            String qnt=object.getString("desc");
                            String mrp_s=object.getString("oldprice");
                            String price_s=object.getString("price");
                            String photo=object.getString("photo");
                            String itemID=object.getString("id");
                            if(mrp_s.equals("") || Integer.parseInt(mrp_s)<=Integer.parseInt(price_s))
                                mrp_s="";
                            // customers=new Customers(name, mobile, address, city, mother, gender, email, nationalid);
                            String curQuantity="1";
                            if(prevCartQuantities.containsKey(itemID))
                                curQuantity=prevCartQuantities.get(itemID);
                            title.add(name);
                            quantity.add(qnt);
                            images.add(photo);
                            mrp.add(mrp_s);
                            price.add(price_s);
                            curQuantities.add(curQuantity);
                            itemId.add(itemID);
                            desc.add(object.getString("desc"));
                            menuId.add(object.getString("menu_id"));
                            //customersList.add(customers);

                            adapter.notifyDataSetChanged();

                        }
                        progressDialog.dismiss();



                    }else {
                        progressDialog.dismiss();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(MuttonOrderActivity.this, "Cannot get Products try again",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String, String>();
                String id_s="7";
                params.put("res_id", id_s);

                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(MuttonOrderActivity.this);
        requestQueue.add(request);



    }

    /*
    private void calculateRaji() {

        res_price=res_qnt*qnt_price;
        txt_reserved_price.setText("₹ "+res_price);
        txt_quantity_display.setText(new String(String.valueOf(res_qnt)));
        txt_reserved_qnt.setText(tempqnt+"gm");

    }


     */

}
