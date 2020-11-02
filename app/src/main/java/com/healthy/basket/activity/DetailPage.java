package com.healthy.basket.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.healthy.basket.BuildConfig;
import com.healthy.basket.Getset.CustomMarker;
import com.healthy.basket.Getset.cartgetset;
import com.healthy.basket.Getset.detailgetset;
import com.healthy.basket.Getset.favgetset;
import com.healthy.basket.Getset.menugetset;
import com.healthy.basket.R;
import com.healthy.basket.utils.GPSTracker;
import com.healthy.basket.utils.sqliteHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.healthy.basket.activity.MainActivity.changeStatsBarColor;
import static com.healthy.basket.activity.MainActivity.checkInternet;
import static com.healthy.basket.activity.MainActivity.showErrorDialog;


public class DetailPage extends AppCompatActivity implements OnMapReadyCallback {
    private static ArrayList<detailgetset> detaillist;
    sqliteHelper sqliteHelper;
    private static ArrayList<cartgetset> cartlist;
    private ArrayList<favgetset> favlist;
    private ProgressDialog progressDialog;
    private double latitudecur;
    private double longitudecur;
    private final int start = 0;
    private String res_id;
    private ImageButton btn_fav;
    private ImageButton btn_fav1;
    private SQLiteDatabase db;
    private Cursor cur = null;
    View layout;
    private Button btn_share;
    String id;
    private String name;
    private String category;
    private String timing;
    private String rating;
    private String distance;
    private String image;
    private String restaurent_id;
    private String address;
    private String CategoryTotal = "";
    private CollapsingToolbarLayout collapsingToolbar;
    private String distancenew;
    private GoogleMap googleMap;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private final String tag = "fb";
    private HashMap<CustomMarker, Marker> markersHashMap;
    private TextView txt_addressdesc;

    private static ArrayList<menugetset> productlist;
    private String detail_id;

    private static double roundMyData(double Rval, int numberOfDigitsAfterDecimal) {
        double p = (float) Math.pow(10, numberOfDigitsAfterDecimal);
        Rval = Rval * p;
        double tmp = Math.floor(Rval);
        System.out.println("~~~~~~tmp~~~~~" + tmp);
        return tmp / p;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_detail_page);
        changeStatsBarColor(DetailPage.this);
        initializations();

        getIntents();
        getCart();
        if (checkInternet(DetailPage.this)) {
            gettingGPSLocation();
            getData();
            getProductData();
        } else
            showErrorDialog(DetailPage.this);


    }

    private void gettingGPSLocation() {
        GPSTracker gps = new GPSTracker();
        gps.init(DetailPage.this);        // check if GPS enabled
        if (gps.canGetLocation()) {
            try {
                latitudecur = gps.getLatitude();
                longitudecur = gps.getLongitude();
                Log.w("Current Location", "Lat: " + latitudecur + "Long: " + longitudecur);
            } catch (NullPointerException | NumberFormatException e) {
                // TODO: handle exception
                Log.e("Error", e.getMessage());
            }

        } else {
            gps.showSettingsAlert();
        }


    }

    private void initializations() {
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //initialization
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        btn_share = findViewById(R.id.btn_share);
        btn_fav = findViewById(R.id.btn_fav);
        btn_fav1 = findViewById(R.id.btn_fav1);
        sqliteHelper = new sqliteHelper(DetailPage.this);
        findViewById(R.id.img_DetailPage_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = findViewById(R.id.detailPageFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iv = new Intent(DetailPage.this, Cart.class);
                iv.putExtra("detail_id", detail_id);
                iv.putExtra("restaurent_name", detaillist.get(0).getName());
                iv.putExtra("delivery_charge", detaillist.get(0).getDelivery_charg());
                iv.putExtra("minimum_order", detaillist.get(0).getDelivery_time());
                iv.putExtra("time", detaillist.get(0).getTime());
                startActivityForResult(iv, 100);
            }
        });
    }

    private void getIntents() {
        //getting intents
        Intent iv = getIntent();
        res_id = iv.getStringExtra("res_id");
        distancenew = iv.getStringExtra("distance");
    }

    private void getData() {
        //getting data
        detaillist = new ArrayList<>();
        cartlist = new ArrayList<>();
        favlist = new ArrayList<>();

        if (checkInternet(DetailPage.this))
            new GetDataAsyncTask().execute();
        else showErrorDialog(DetailPage.this);


        //fav button onclick
        onClickFavourite();
        //facebook share callbacks
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(DetailPage.this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(DetailPage.this, "You shared this post", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Log.e(tag, "cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(tag, error.toString());
            }
        });


    }

    private void onClickFavourite() {
        btn_fav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sqliteHelper = new sqliteHelper(DetailPage.this);
                SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
                // TODO Auto-generated method stub
                final detailgetset temp_Obj3 = detaillist.get(start);
                btn_fav1.setVisibility(View.VISIBLE);
                btn_fav.setVisibility(View.INVISIBLE);
                ContentValues values = new ContentValues();
                values.put("restaurent_id", temp_Obj3.getId());
                values.put("name", temp_Obj3.getName());
                values.put("category", temp_Obj3.getCategory());
                values.put("timing", temp_Obj3.getTime());
                values.put("rating", temp_Obj3.getRatting());
                values.put("distance", distancenew);
                values.put("image", temp_Obj3.getPhoto());
                values.put("address", temp_Obj3.getAddress());
                db1.insert("favourite", null, values);
                Log.e("inserted values", values.toString());
                db1.close();

            }
        });
        // on click of this Favourite button store will be unfavourite
        btn_fav1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                btn_fav.setVisibility(View.VISIBLE);
                btn_fav1.setVisibility(View.INVISIBLE);
                final detailgetset temp_Obj3 = detaillist.get(start);
                // remove record of store numberOfRecords database to unfavourite

                sqliteHelper = new sqliteHelper(DetailPage.this);
                SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();


                cur = db1.rawQuery("Delete from favourite where restaurent_id =" + temp_Obj3.getId() + ";", null);
                Log.e("deletedvalues", "" + ("Delete numberOfRecords Favourite where id =" + temp_Obj3.getId() + ";"));
                if (cur.getCount() != 0) {
                    if (cur.moveToFirst()) {
                        do {
                            favgetset obj = new favgetset();
                            restaurent_id = cur.getString(cur.getColumnIndex("restaurent_id"));
                            name = cur.getString(cur.getColumnIndex("name"));
                            category = cur.getString(cur.getColumnIndex("category"));
                            timing = cur.getString(cur.getColumnIndex("timing"));
                            rating = cur.getString(cur.getColumnIndex("rating"));
                            distance = cur.getString(cur.getColumnIndex("distance"));
                            image = cur.getString(cur.getColumnIndex("image"));
                            address = cur.getString(cur.getColumnIndex("address"));
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
            }
        });
    }

    private Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap numberOfRecords ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(DetailPage.this, BuildConfig.APPLICATION_ID + ".provider", file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void getlist() {

        sqliteHelper = new sqliteHelper(DetailPage.this);
        SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();

        try {
            cur = db1.rawQuery("delete  from cart ;", null);
            Log.e("deletdetail_pagedata", "delete numberOfRecords cart ;");
            if (cur.getCount() != 0) {
                if (cur.moveToFirst()) {
                    do {
                        cartgetset obj = new cartgetset();
                        String resid = cur.getString(cur.getColumnIndex("resid"));
                        String menuid321 = cur.getString(cur.getColumnIndex("menuid"));
                        String foodid = cur.getString(cur.getColumnIndex("foodid"));
                        String foodname = cur.getString(cur.getColumnIndex("foodname"));
                        String foodprice321 = cur.getString(cur.getColumnIndex("foodprice"));
                        String fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
                        obj.setResid(resid);
                        obj.setFoodid(foodid);
                        obj.setMenuid(menuid321);
                        obj.setFoodname(foodname);
                        obj.setFooddesc(fooddesc);
                        Log.e("menuid321", menuid321);
                        Log.e("foodp321", "" + foodprice321);
                        cartlist.add(obj);
                    } while (cur.moveToNext());
                }
            }
            cur.close();
            db1.close();
//            myDbHelper.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        double latitude = 0, longitude = 0;
        try {
            String lat = detaillist.get(0).getLat();
            String lon = detaillist.get(0).getLon();
            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(lon);
        } catch (NumberFormatException e) {
            Log.e("Error", e.getMessage());
            Toast.makeText(DetailPage.this, getString(R.string.later_txt), Toast.LENGTH_SHORT).show();
            // TODO: handle exception
        }
        afterMapReady(latitude, longitude);

    }

    private void afterMapReady(double latitude, double longitude) {
        LatLng position = new LatLng(latitude, longitude);
        CustomMarker customMarkerOne = new CustomMarker("markerOne", latitude, longitude);
        try {
            MarkerOptions markerOption = new MarkerOptions().position(

                    new LatLng(customMarkerOne.getCustomMarkerLatitude(), customMarkerOne.getCustomMarkerLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .title(detaillist.get(0).getName());

            Marker newMark = googleMap.addMarker(markerOption);

            addMarkerToHashMap(customMarkerOne, newMark);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
        } catch (Exception e1) {
            Toast.makeText(DetailPage.this, getString(R.string.later_txt), Toast.LENGTH_SHORT).show();

        }
    }

    private void addMarkerToHashMap(CustomMarker customMarker, Marker marker) {
        setUpMarkersHashMap();
        markersHashMap.put(customMarker, marker);
    }

    private void setUpMarkersHashMap() {
        if (markersHashMap == null) {
            markersHashMap = new HashMap<>();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode==100) getCart();

    }

    class GetDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DetailPage.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL hp;
            String error1;
            try {
                detaillist.clear();
                hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "getrestaurantdetail.php?res_id=" + res_id + "&lat=" + latitudecur + "&" + "lon=" + longitudecur);
                Log.e("URLdetail", "" + hp);
                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(input));
                String x;
                x = r.readLine();
                StringBuilder total = new StringBuilder();
                while (x != null) {
                    total.append(x);
                    x = r.readLine();
                }
                Log.e("URL", "" + total);

                JSONArray jObject = new JSONArray(total.toString());
                Log.d("URL12", "" + jObject);
                JSONObject Obj1;
                Obj1 = jObject.getJSONObject(0);
                switch (Obj1.getString("status")) {
                    case "Success":
                        JSONObject jsonO = Obj1.getJSONObject("Restaurant_Detail");
                        detailgetset temp = new detailgetset();
                        temp.setId(jsonO.getString("id"));
                        temp.setName(jsonO.getString("name"));
                        temp.setAddress(jsonO.getString("address"));
                        temp.setTime(jsonO.getString("time"));
                        temp.setDelivery_time(jsonO.getString("delivery_time"));
                        temp.setCurrency(jsonO.getString("currency"));
                        temp.setPhoto(jsonO.getString("photo"));
                        temp.setPhone(jsonO.getString("phone"));
                        temp.setLat(jsonO.getString("lat"));
                        temp.setLon(jsonO.getString("lon"));
                        temp.setDesc(jsonO.getString("desc"));
                        temp.setEmail(jsonO.getString("email"));
                        temp.setLocation(jsonO.getString("address"));
                        temp.setRatting(jsonO.getString("ratting"));
                        temp.setRes_status(jsonO.getString("res_status"));
                        temp.setDelivery_charg(jsonO.getString("delivery_charg"));
                        temp.setDistance(jsonO.getString("distance"));
                        temp.setCategory(jsonO.getString("Category"));
                        temp.setFree_del_km(jsonO.getString("free_del_km"));
                        temp.setPer_km_charge(jsonO.getString("per_km_charge"));
                        temp.setCategory(jsonO.getString("Category"));
                        String catname = jsonO.getString("Category");
                        Log.e("catname", "" + catname);
                        CategoryTotal = CategoryTotal + catname;
                        detaillist.add(temp);
                        Log.e("detaillist", detaillist.get(0).getName());
                        break;
                    case "Failed":
                        final String error = Obj1.getString("Error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailPage.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    default:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailPage.this, "Please try again later!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                error1 = e.getMessage();
            } catch (NullPointerException e) {
                // TODO: handle exception
                error1 = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            collapsingToolbar.setTitle("");
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsingToolbar.setNestedScrollingEnabled(false);
            SharedPreferences preferences = getSharedPreferences(getString(R.string.MY_PREFS_NAME),MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("lat", detaillist.get(0).getLat());
            editor.putString("lon", detaillist.get(0).getLon());
            editor.putString("free_del_km", detaillist.get(0).getFree_del_km());
            editor.putString("per_km_charge", detaillist.get(0).getPer_km_charge());
            editor.apply();
            editor.commit();
            Typeface tf_opensense_regular = Typeface.createFromAsset(DetailPage.this.getAssets(), "fonts/OpenSans-Regular.ttf");

            //initialize
            AppBarLayout appBarLayout = findViewById(R.id.appbar);
            final TextView txt_title = findViewById(R.id.txt_title);
            txt_addressdesc = findViewById(R.id.txt_addressdesc);
            TextView txt_descnumber = findViewById(R.id.txt_descnumber);
            TextView txt_timingdesc = findViewById(R.id.txt_timingdesc);
            TextView txt_fooddesc = findViewById(R.id.txt_fooddesc);
            TextView txt_deliverydesc = findViewById(R.id.txt_deliverydesc);
            TextView txt_orderdesc = findViewById(R.id.txt_orderdesc);
            TextView txt_deliverytypedesc = findViewById(R.id.txt_deliverytypedesc);
            final RatingBar rb = findViewById(R.id.rate);
            final TextView txt_ratenumber = findViewById(R.id.txt_ratenumber);
            TextView txt_distance = findViewById(R.id.txt_distance);
            final ImageView imageview = findViewById(R.id.img_detail);
            TextView txt_description = findViewById(R.id.txt_description);


            //setting typeface
            txt_addressdesc.setTypeface(tf_opensense_regular);
            txt_descnumber.setTypeface(tf_opensense_regular);
            txt_addressdesc.setTypeface(tf_opensense_regular);
            txt_fooddesc.setTypeface(tf_opensense_regular);
            txt_deliverydesc.setTypeface(tf_opensense_regular);
            txt_orderdesc.setTypeface(tf_opensense_regular);
            txt_deliverytypedesc.setTypeface(tf_opensense_regular);
            txt_description.setTypeface(tf_opensense_regular);

            //setting data
            if (detaillist.size() > 0) {
                txt_title.setText((detaillist.get(0).getName()));
                txt_addressdesc.setText((detaillist.get(0).getAddress()));
                txt_descnumber.setText((detaillist.get(0).getPhone()));
                txt_timingdesc.setText((detaillist.get(0).getTime()));
                if (CategoryTotal != null) {
                    String category = CategoryTotal.replace("[", "").replace("]", "").replace("\"", "").replace(",", ", ");
                    txt_fooddesc.setText(category);
                }
                //txt_deliverydesc.setText((detaillist.get(0).getDelivery_time()));
                txt_deliverytypedesc.setText((detaillist.get(0).getDelivery_charg()));
                rb.setRating(Float.parseFloat(detaillist.get(0).getRatting()));
                txt_orderdesc.setText(detaillist.get(0).getCurrency()+" "+detaillist.get(0).getDelivery_time());
                rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        // Auto-generated
                        Log.d("rate", "" + rating);
                    }
                });

                txt_ratenumber.setText("" + Float.parseFloat(detaillist.get(0).getRatting()));
                txt_ratenumber.setTypeface(tf_opensense_regular);
                final String image = detaillist.get(0).getPhoto().replace(" ", "%20");
                Picasso.with(DetailPage.this)
                        .load(getString(R.string.link) + getString(R.string.imagepath) + image)
                        .into(imageview);
                Log.e("Image", getString(R.string.link) + getString(R.string.imagepath) + image);


                appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (collapsingToolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbar)) {
                            //      btn_share.animate().alpha(1).setDuration(600);
                            txt_ratenumber.animate().alpha(0).setDuration(600);
                            rb.animate().alpha(0).setDuration(600);
                            //       txt_name.animate().alpha(0).setDuration(600);
                        } else {
                            txt_ratenumber.animate().alpha(1).setDuration(600);
                            rb.animate().alpha(1).setDuration(600);
                            //      txt_name.animate().alpha(1).setDuration(600);
                            //   btn_share.animate().alpha(0).setDuration(600);
                            txt_title.animate().alpha(1).setDuration(600);
                            collapsingToolbar.setTitle("");
                        }
                    }
                });
                txt_distance.setTypeface(tf_opensense_regular);
                try {
                    double numbar = roundMyData(Double.parseDouble(distancenew), 1);
                    txt_distance.setText("" + numbar + " " + getResources().getString(R.string.km));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                txt_description.setText(detaillist.get(0).getDesc());

            }
            //adding map support

            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment));
            mapFragment.getMapAsync(DetailPage.this);

            //listeners after getting detail

            Button btn_call = findViewById(R.id.btn_call);
            btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String call = detaillist.get(0).getPhone();
                    String uri = "tel:" + call;
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse(uri));
                    startActivity(i);
                }
            });

            Button btn_map = findViewById(R.id.btn_map);
            btn_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final detailgetset temp_Obj3 = detaillist.get(start);

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + latitudecur + "," + longitudecur + "&daddr=" + temp_Obj3.getLat() + "," + temp_Obj3.getLon()));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            });

            LinearLayout btn_menu = findViewById(R.id.btn_menu);
            btn_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getlist();
                    Intent iv = new Intent(DetailPage.this, ProductList.class);
                    iv.putExtra("detail_id", "" + res_id);
                    iv.putExtra("restaurent_name", "" + detaillist.get(0).getName());
                    startActivity(iv);
                }

            });

            Button btn_review = findViewById(R.id.btn_review);
            btn_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent iv = new Intent(DetailPage.this, Review.class);
                    iv.putExtra("detail_id", "" + detaillist.get(0).getId());
                    iv.putExtra("name", "" + detaillist.get(0).getName());
                    iv.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(iv);
                }
            });

            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog alertDialog = new AlertDialog.Builder(DetailPage.this, R.style.MyDialogTheme).create();
                    alertDialog.setTitle(getString(R.string.share));
                    alertDialog.setMessage(getString(R.string.sharetitle));
                    // share on gmail,hike etc
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.more),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        String Name = detaillist.get(0).getName() + "\n";
                                        String address = "Address:" + detaillist.get(0).getAddress() + "\n";
                                        String Mobile = "Mobile No:" + detaillist.get(0).getPhone() + "\n";

                                        String sharee = Name + address + Mobile;
                                        Log.e("Address", address);
                                        Uri bmpUri = getLocalBitmapUri(imageview);
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("text/plain");
                                        share.setType("image/*");
                                        share.setType("image/jpeg");
                                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                        share.putExtra(Intent.EXTRA_SUBJECT, "Restaurant");
                                        share.putExtra("android.intent.extra.TEXT", sharee);
                                        share.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                        startActivity(Intent.createChooser(share, "Share link!"));
                                    } catch (NullPointerException e) {
                                        // TODO: handle exception
                                    }
                                }
                            });

                    // share on whatsapp

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.whatsapp),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {

                                    String Name = detaillist.get(0).getName() + "\n";
                                    String address = "Address:" + detaillist.get(0).getAddress() + "\n";
                                    String Mobile = "Mobile No:" + detaillist.get(0).getPhone() + "\n";

                                    String sharee = Name + address + Mobile;
                                    Uri bmpUri = getLocalBitmapUri(imageview);
                                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                                    whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, sharee);
                                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                    whatsappIntent.setType("text/plain");
                                    whatsappIntent.setType("image/*");
                                    whatsappIntent.setPackage("com.whatsapp");
                                    try {
                                        startActivity(whatsappIntent);

                                    } catch (android.content.ActivityNotFoundException ex) {
                                        Toast.makeText(DetailPage.this, "Whatsapp have not been installed.", Toast.LENGTH_LONG)
                                                .show();
                                    }

                                }
                            });

                    // share on facebook

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.facebook),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    //fb share integrate
                                    String imageurl = getString(R.string.link) + getString(R.string.imagepath) + image;
                                    String url1 = "https://www.google.com";
                                    String dist = detaillist.get(0).getDesc();
                                    String Name = detaillist.get(0).getName() + "\n";
                                    String address = "Address:" + detaillist.get(0).getAddress() + "\n";
                                    String Mobile = "Mobile No:" + detaillist.get(0).getPhone() + "\n";

                                    String urlToShare = dist + Name + address + Mobile;


                                    ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                                            .putString("fb:app_id", getString(R.string.facebook_app_id))
                                            .putString("og:type", "article")
                                            .putString("og:url", url1)
                                            .putString("og:title", detaillist.get(0).getName())
                                            .putString("og:image", imageurl)
                                            .putString("og:description", urlToShare).build();
                                    // Create an action
                                    ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                                            .setActionType("news.publishes")
                                            .putObject("article", object)
                                            .build();

                                    // Create the content
                                    ShareOpenGraphContent contentn = new ShareOpenGraphContent.Builder()
                                            .setPreviewPropertyName("article").setAction(action)
                                            .build();

                                    Log.e("check", object.getBundle().toString() + " " + action.getBundle().toString());
                                    shareDialog.show(contentn, ShareDialog.Mode.AUTOMATIC);

                                }
                            });
                    alertDialog.show();
                }
            });


            //checking Favourite
            new getfavlist().execute();
        }


    }

    private class getfavlist extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            favlist.clear();

            sqliteHelper = new sqliteHelper(DetailPage.this);
            SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
            try {
                cur = db1.rawQuery("select * from Favourite where restaurent_id=" + detaillist.get(0).getId() + ";", null);


                Log.e("alreadyfav", "select * numberOfRecords Favourite where restaurent_id=" + detaillist.get(0).getId() + ";");
                if (cur.getCount() != 0) {
                    if (cur.moveToFirst()) {
                        do {
                            favgetset obj = new favgetset();
                            restaurent_id = cur.getString(cur.getColumnIndex("restaurent_id"));
                            name = cur.getString(cur.getColumnIndex("name"));
                            category = cur.getString(cur.getColumnIndex("category"));
                            timing = cur.getString(cur.getColumnIndex("timing"));
                            rating = cur.getString(cur.getColumnIndex("rating"));
                            distance = cur.getString(cur.getColumnIndex("distance"));
                            image = cur.getString(cur.getColumnIndex("image"));
                            address = cur.getString(cur.getColumnIndex("address"));
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
                // TODO: handle exception
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (favlist.size() == 0) {
                btn_fav.setVisibility(View.VISIBLE);
                btn_fav1.setVisibility(View.INVISIBLE);
            } else {

                btn_fav1.setVisibility(View.VISIBLE);
                btn_fav.setVisibility(View.INVISIBLE);
            }

        }
    }

    private void getProductData() {
        productlist = new ArrayList<>();
        detail_id = res_id;
        if (MainActivity.checkInternet(DetailPage.this))
            new GetProductDataAsyncTask().execute();
        else MainActivity.showErrorDialog(DetailPage.this);
    }

    class GetProductDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            productlist.clear();
            URL hp;
            try {
                productlist.clear();
                hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "/restaurant_menu.php?res_id=" + detail_id);
                Log.e("URLmenu", "" + hp);
                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                Log.d("input", "" + input);
                BufferedReader r = new BufferedReader(new InputStreamReader(input));
                String x;
                x = r.readLine();
                StringBuilder total = new StringBuilder();
                while (x != null) {
                    total.append(x);
                    x = r.readLine();
                }
                Log.e("URL", "" + total);
                JSONArray jObject = new JSONArray(total.toString());
                for (int i = 0; i < jObject.length(); i++) {
                    final JSONObject Obj1;
                    Obj1 = jObject.getJSONObject(i);

                    if (Obj1.getString("status").equals("Success")) {
                        JSONArray data = Obj1.getJSONArray("Menu_Category");
                        for (int iq = 0; iq < data.length(); iq++) {
                            JSONObject jdat = data.getJSONObject(iq);
                            menugetset temp = new menugetset();
                            temp.setId(jdat.getString("id"));
                            temp.setName(jdat.getString("name"));
                            temp.setCreated_at(jdat.getString("created_at"));
                            productlist.add(temp);
                        }

                        Log.e("productlist", productlist.get(0).getId());
                    } else {
                        Log.e("success", "Failed:No data available ");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(DetailPage.this, Obj1.getString("error"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("Error", e.getMessage());
            } catch (NullPointerException e) {
                // TODO: handle exception
                Log.e("Error", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            {
                ViewPager viewPager = findViewById(R.id.detailPageViewpager);
                ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
                int i;
                Menufragment menufragment;
                for (i = 0; i < productlist.size(); i++) {
                    int pos = Integer.parseInt(productlist.get(i).getId());
                    menufragment = new Menufragment();
                    menufragment.init(productlist.get(i).getId(), detail_id);
                    adapter.addFragment(menufragment, productlist.get(i).getId(), productlist.get(i).getName(), detail_id, 0);
                    Log.e("data123", productlist.get(i).getName());
                    Log.e("tabedid", productlist.get(i).getId());
                    adapter.getCount();
                }
                viewPager.setAdapter(adapter);

                final TabLayout tabLayout = findViewById(R.id.detailPageTabs);
                tabLayout.setupWithViewPager(viewPager);
                for (int j = 0; j<productlist.size(); j++){
                    if ((getIntent().getStringExtra("currentTab")!=null && (getIntent().getStringExtra("currentTab")!="")))
                    {
                        if (productlist.get(j).getId().equals(getIntent().getStringExtra("currentTab"))) {
                            viewPager.setCurrentItem(j);
                            Log.e("DetailPage", getIntent().getIntExtra("currentTab", 0) + "");
                            break;
                        }
                    }
                    else
                    {
                        viewPager.setCurrentItem(j);
                    }
                }
//                viewPager.setCurrentItem(getIntent().getIntExtra("currentTab",0));
//                Log.e("DetailPage", getIntent().getIntExtra("currentTab",0)+"");

                View root = tabLayout.getChildAt(0);

                if (root instanceof LinearLayout) {
                    ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setColor(Color.WHITE);
                    drawable.setSize(1, 1);
                    ((LinearLayout) root).setDividerPadding(10);
                    root.setPadding(0, 25, 0, 25);
                    ((LinearLayout) root).setDividerDrawable(drawable);
                }

                ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
                int tabsCount = vg.getChildCount();
                for (int j = 0; j < tabsCount; j++) {
                    ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                    int tabChildsCount = vgTab.getChildCount();
                    for (int it = 0; it < tabChildsCount; it++) {
                        View tabViewChild = vgTab.getChildAt(it);
                        if (tabViewChild instanceof TextView) {
                            if(j ==0){
                                ((TextView) tabViewChild).setTypeface(MainActivity.tf_opensense_medium, Typeface.NORMAL);
                            }else {
                                ((TextView) tabViewChild).setTypeface(MainActivity.tf_opensense_regular, Typeface.NORMAL);
                            }

                        }
                    }
                }

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        LinearLayout linearLayout = (LinearLayout) ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                        TextView tabTextView = (TextView) linearLayout.getChildAt(1);
                        tabTextView.setTypeface(MainActivity.tf_opensense_medium, Typeface.NORMAL);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        LinearLayout linearLayout = (LinearLayout) ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                        TextView tabTextView = (TextView) linearLayout.getChildAt(1);
                        tabTextView.setTypeface(MainActivity.tf_opensense_regular, Typeface.NORMAL);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return productlist.size();
        }

        private void addFragment(Fragment fragment, String id, String s, String detail_id, int i) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(s);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            int index = productlist.indexOf(object);
            if (index == 1) {
                return POSITION_NONE;
            } else {
                return index;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    void getCart(){
        sqliteHelper = new sqliteHelper(DetailPage.this);
        SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
        int i = 1;
        try {
            Cursor cur = db1.rawQuery("select * from cart where foodprice >=1;", null);
            Log.e("cartlisting", "" + ("select * numberOfRecords cart where foodprice <=0;"));
            Log.d("SIZWA", "" + cur.getCount());
            TextView detailPageCartCount = findViewById(R.id.detailPageCartCount);
            detailPageCartCount.setText(String.valueOf(cur.getCount()));
            cur.close();
            db1.close();
        } catch (Exception e) {
        }
    }


}
