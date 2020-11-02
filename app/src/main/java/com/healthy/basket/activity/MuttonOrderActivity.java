package com.healthy.basket.activity;
import android.app.ProgressDialog;
import android.os.Bundle;
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
    String api_getP="https://healthybaskets.co/api/getScheduledItems.php?res_id=7";

    AdapterRecyclerNew adapter;
    ProgressDialog progressDialog;


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



        letsShakeRey();

        adapter = new AdapterRecyclerNew(this, title, images,quantity, mrp, price);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        dataList.setLayoutManager(gridLayoutManager);
        dataList.setAdapter(adapter);


    }

    private void letsShakeRey() {
        progressDialog.show();

        StringRequest request=new StringRequest(Request.Method.GET, api_getP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String success=jsonObject.getString("status");
                    JSONArray jsonArray=jsonObject.getJSONArray("Menu_List");

                    if (success.equals("Success")){
                        for (int i=0; i<jsonArray.length(); i++){
                            JSONObject object=jsonArray.getJSONObject(i);

                            String name=object.getString("name");
                            String qnt=object.getString("desc");
                            String mrp_s=object.getString("oldprice");
                            String price_s=object.getString("price");
                            String photo=object.getString("photo");

                            // customers=new Customers(name, mobile, address, city, mother, gender, email, nationalid);

                            title.add(name);
                            quantity.add(qnt);
                            images.add(photo);
                            mrp.add(mrp_s);
                            price.add(price_s);

                            //customersList.add(customers);

                            adapter.notifyDataSetChanged();

                        }
                        progressDialog.dismiss();



                    }else {
                        progressDialog.dismiss();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(MuttonOrderActivity.this, "Json Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
