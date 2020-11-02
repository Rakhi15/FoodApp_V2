package com.healthy.basket.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.healthy.basket.Getset.UploadMedicalResult;
import com.healthy.basket.R;
import com.healthy.basket.network.APIClient;
import com.healthy.basket.network.APIInterface;

import org.apache.http.entity.ContentType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.BitmapFactory.decodeFile;
import static com.healthy.basket.activity.MainActivity.changeStatsBarColor;
import static com.healthy.basket.activity.MainActivity.checkInternet;
import static com.healthy.basket.activity.MainActivity.showErrorDialog;
import static com.healthy.basket.activity.MainActivity.tf_opensense_regular;

public class UploadMedicalOrderActivity extends AppCompatActivity {

    private LinearLayout camera;
    private LinearLayout gallery;
    private Button uploadMedicalUpload;
    private ImageView img_user;
    private EditText note;

    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final String[] PERMISSIONS_STORAGE = {
//            android.Manifest.permission.READ_EXTERNAL_STORAGE,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

    };


    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_cam_IMAGE = 2;


    private String picturepath = "";
    private static final String MyPREFERENCES = "Fooddelivery";
    private String userid;
    private  Uri aUri;
    Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_medical_order);
        changeStatsBarColor(UploadMedicalOrderActivity.this);

        initializations();

        setClickListener();
    }

    private void initializations() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        uploadMedicalUpload = findViewById(R.id.uploadMedicalUpload);
        camera = findViewById(R.id.uploadMedicalCamera);
        gallery = findViewById(R.id.uploadMedicalGallery);
        img_user = findViewById(R.id.uploadMedicalPrescription);
        note = findViewById(R.id.uploadMedicalNote);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setClickListener() {
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ActivityCompat.checkSelfPermission(UploadMedicalOrderActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    verifyStoragePermissions(UploadMedicalOrderActivity.this);
                } else {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ActivityCompat.checkSelfPermission(UploadMedicalOrderActivity.this, Manifest.permission.CAMERA);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    verifyStoragePermissions(UploadMedicalOrderActivity.this);

                } else {
//                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, RESULT_cam_IMAGE);
                }
            }
        });

        uploadMedicalUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();

            }
        });

//        img_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
    }

    private void submitForm() {
        if (picturepath.equals("")) {
            Toast.makeText(getApplicationContext(), "Choose Your Prescription Image", Toast.LENGTH_LONG).show();

        } else {
            img_user.setImageBitmap(decodeFile(picturepath));
            Log.d("picturepath", "" + picturepath);
            if (checkingSignIn()) {
                if (checkInternet(UploadMedicalOrderActivity.this))
                {
                    ///new postingData().execute();
                    uploadImage();
                }
                else {
                    showErrorDialog(UploadMedicalOrderActivity.this);
                }
            } else {
                Intent iv = new Intent(UploadMedicalOrderActivity.this, Login.class);
                iv.putExtra("key", "UploadMedicalOrder");
                iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(iv);
                Toast.makeText(UploadMedicalOrderActivity.this, R.string.loginmsg, Toast.LENGTH_SHORT).show();
            }


        }


    }

    void uploadImage(){
        final ProgressBar progressBar = findViewById(R.id.uploadMedicalProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        //creating a file
        File file = new File(picturepath);

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(aUri)), file);
        RequestBody noteBody = RequestBody.create(MediaType.parse("text/plain"), note.getText().toString());
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), userid);

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<UploadMedicalResult> call = apiInterface.uploadImage(requestFile, noteBody, userIdBody);
        call.enqueue(new Callback<UploadMedicalResult>() {
            @Override
            public void onResponse(Call<UploadMedicalResult> call, Response<UploadMedicalResult> response) {
                progressBar.setVisibility(View.GONE);
               Log.e("UploadMedicalOrder", response.code()+"");
                if(response.code()==200) {
                    Toast.makeText(UploadMedicalOrderActivity.this,response.body().getStatus(),Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UploadMedicalResult> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("UploadMedicalOrder", t.toString()+"");
                Log.e("UploadMedicalOrder", t.getMessage()+"");
                t.printStackTrace();
            }
        });

    }

    private static void verifyStoragePermissions(final Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
//            new AlertDialog.Builder(activity)
//                    .setTitle(R.string.msg)
//                    .setMessage(R.string.mssg)
//                    .setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            ActivityCompat.requestPermissions(activity,
//                                    PERMISSIONS_STORAGE,
//                                    REQUEST_EXTERNAL_STORAGE);
//                        }
//                    })
//                    .create()
//                    .show();
            ActivityCompat.requestPermissions(activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);

        } else {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("OnActivity Method",requestCode+" "+resultCode);
        try {
            Bitmap photo;
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.MediaColumns.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturepath = cursor.getString(columnIndex);

                Log.d("picturepath", "" + picturepath);
                cursor.close();
                try {
                    photo = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    img_user.setImageBitmap(decodeFile(picturepath));
                    img_user.setScaleType(ImageView.ScaleType.FIT_XY);
                    aUri = selectedImage;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == RESULT_cam_IMAGE && resultCode == RESULT_OK) {
                Log.e("OnActivity Method",requestCode+"");
                Log.d("photo", "" + MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri));
                photo = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
//                photo = (Bitmap) data.getExtras().get("data");
//                img_user.setImageBitmap(photo);
//                img_user.setScaleType(ImageView.ScaleType.FIT_XY);
                Uri tempUri = getImageUri(getApplicationContext(), photo);
                aUri = tempUri;
                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));
                picturepath = getRealPathFromURI(tempUri);
                img_user.setImageBitmap(photo);
//                img_user.setImageBitmap(decodeFile(picturepath));
                img_user.setScaleType(ImageView.ScaleType.FIT_XY);
//                picturepath = String.valueOf(finalFile);


            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e("NullPointerException",e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("FileNotFoundException",e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException",e.getMessage());
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private String getRealPathFromURI(Uri uri) {
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(filePathColumn[0]);
        String temp = cursor.getString(idx);
        cursor.close();
        return temp;
    }

//    private Bitmap decodeFile(String path) {
//        try {
//            // Decode image size
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(path, o);
//            // The new size we want to scale to
//            final int REQUIRED_SIZE = 100;
//
//            // Find the correct scale value. It should be the power of 2.
//            int scale = 1;
//            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
//                scale *= 2;
//
//            // Decode with inSampleSize
//            BitmapFactory.Options o2 = new BitmapFactory.Options();
//            o2.inSampleSize = scale;
//            return BitmapFactory.decodeFile(path, o2);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//        return null;
//
//    }
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
