package com.healthy.basket.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.healthy.basket.Getset.ForgetPassGetSet;
import com.healthy.basket.R;
import com.healthy.basket.network.APIClient;
import com.healthy.basket.network.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {
    private TextInputLayout inputLayoutPhone;
    private EditText forgetPassPhone;
    private Button btnForgetPass;
    private String phoneNumber;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        initializations();
        setFonts();
        ImageButton ib_back = findViewById(R.id.forgetPassBack);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitform();
            }
        });
    }

    private void initializations() {
        inputLayoutPhone = findViewById(R.id.forgetPassPhoneInput);
        forgetPassPhone = findViewById(R.id.forgetPassPhone);
        btnForgetPass = findViewById(R.id.forgetPassBtn);
        progressBar = findViewById(R.id.forgetPassProgressBar);

    }

    private void setFonts(){
        TextView txt_title = findViewById(R.id.forgetPassTitle);
        txt_title.setTypeface(MainActivity.tf_opensense_regular);
        btnForgetPass.setTypeface(MainActivity.tf_opensense_regular);
    }

    private void submitform() {
        if (!validatephone()) {
            return;
        }

        phoneNumber = forgetPassPhone.getText().toString();

        if (MainActivity.checkInternet(ForgetPasswordActivity.this))
            forgetPass();
           // new getlogin().execute();
        else {
            MainActivity.showErrorDialog(ForgetPasswordActivity.this);
        }

    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validatephone() {
        if (forgetPassPhone.getText().toString().trim().isEmpty()){
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(forgetPassPhone);
            return false;
        }
        else inputLayoutPhone.setErrorEnabled(false);
        return true;
//        String regex = "(0/91)?[6-9][0-9]{9}";
//        if (!forgetPassPhone.getText().toString().trim().matches(regex)) {
//            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
//            requestFocus(forgetPassPhone);
//            return false;
//        } else {
//            inputLayoutPhone.setErrorEnabled(false);
//        }
//        return true;
    }

    private void forgetPass(){
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<ForgetPassGetSet> call = apiInterface.forgetPass(phoneNumber);
        call.enqueue(new Callback<ForgetPassGetSet>() {
            @Override
            public void onResponse(Call<ForgetPassGetSet> call, Response<ForgetPassGetSet> response) {
                progressBar.setVisibility(View.GONE);
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this, R.style.CustomAlertDialog);
                builder.setMessage(response.body().getMessage()+"");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });
                builder.show();

                //Toast.makeText(ForgetPasswordActivity.this, response.body().getMessage()+"", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ForgetPassGetSet> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ForgetPasswordActivity.this, "Error! Try again", Toast.LENGTH_SHORT).show();

                //finish();
            }
        });
    }
}
