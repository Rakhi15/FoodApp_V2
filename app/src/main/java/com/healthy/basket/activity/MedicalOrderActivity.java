package com.healthy.basket.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.healthy.basket.Adapter.MedicalOrderAdapter;
import com.healthy.basket.Getset.MedicalOrderModel;
import com.healthy.basket.GridDividerDecoration;
import com.healthy.basket.R;
import com.healthy.basket.network.APIClient;
import com.healthy.basket.network.APIInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.healthy.basket.activity.MainActivity.changeStatsBarColor;
import static com.healthy.basket.activity.MainActivity.checkInternet;
import static com.healthy.basket.activity.MainActivity.showErrorDialog;

public class MedicalOrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private static final String MyPREFERENCES = "Fooddelivery";
    private String userid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_order);
        changeStatsBarColor(MedicalOrderActivity.this);
        initializations();
        getData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initializations() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = findViewById(R.id.medicalOrderRecyclerView);
        progressBar = findViewById(R.id.medicalOrderProgressBar);
//        FloatingActionButton fab = findViewById(R.id.medicalOrderFab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent iv = new Intent(MedicalOrderActivity.this, UploadMedicalOrderActivity.class);
//                startActivityForResult(iv,1001);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getData();
    }

    private void getData(){
        if (checkingSignIn()) {
            if (checkInternet(MedicalOrderActivity.this)){
                final ProgressBar progressBar = findViewById(R.id.medicalOrderProgressBar);
                progressBar.setVisibility(View.VISIBLE);
                APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                Call<MedicalOrderModel> call = apiInterface.getMedicalOrder(userid);
                call.enqueue(new Callback<MedicalOrderModel>() {
                    @Override
                    public void onResponse(Call<MedicalOrderModel> call, Response<MedicalOrderModel> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.code()==200){
                            if (response.body().getStatus().equals("Success")){
                                List<MedicalOrderModel.UserDetail> userDetail =  response.body().getUserDetail();
                                if (userDetail!=null) {

                                    MedicalOrderAdapter adapter = new MedicalOrderAdapter(MedicalOrderActivity.this, userDetail);
                                    //set layout manager and adapter for "GridView"
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(MedicalOrderActivity.this);
                                    recyclerView.addItemDecoration(new GridDividerDecoration(MedicalOrderActivity.this));
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                            Toast.makeText(MedicalOrderActivity.this, response.body().getStatus(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MedicalOrderActivity.this, response.message(), Toast.LENGTH_SHORT).show();


                        }
                    }

                    @Override
                    public void onFailure(Call<MedicalOrderModel> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("MedicalOrderActivity", t.toString());
                        Log.e("MedicalOrderActivity2", t.getMessage());
                        Toast.makeText(MedicalOrderActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                showErrorDialog(MedicalOrderActivity.this);
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
            userid = prefs.getString("userid", null);
            return !userid.equals("delete");
        } else {
            return false;
        }
    }
}
