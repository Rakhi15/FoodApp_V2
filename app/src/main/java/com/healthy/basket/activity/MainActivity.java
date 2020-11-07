package com.healthy.basket.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TypefaceSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.healthy.basket.Adapter.CustomButtonListener;
import com.healthy.basket.Adapter.restaurentadapter;
import com.healthy.basket.AppClass;
import com.healthy.basket.Getset.CitylistGetSet;
import com.healthy.basket.Getset.cartgetset;
import com.healthy.basket.Getset.menugetset;
import com.healthy.basket.Getset.ordergetset;
import com.healthy.basket.Getset.restaurentGetSet;
import com.healthy.basket.GridDividerDecoration;
import com.healthy.basket.Adapter.GridItemClickListener;
import com.healthy.basket.Adapter.GridViewAdapter;
import com.healthy.basket.R;
import com.healthy.basket.jsdialog.DialogAddItemPopup;
import com.healthy.basket.utils.Config;
import com.healthy.basket.utils.ConnectionDetector;
import com.healthy.basket.utils.GPSTracker;
import com.healthy.basket.utils.Preferences;
import com.healthy.basket.utils.RecyclerTouchListener;
import com.facebook.login.LoginManager;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.healthy.basket.utils.sqliteHelper;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.healthy.basket.utils.Config.SHARED_PREF;

public class MainActivity extends AppCompatActivity implements CustomButtonListener{
    private String DB_PATH = Environment.getDataDirectory() + "/Bhagirath/databases/";
    private static final String DB_NAME = "restaurant.sqlite";
    private static final String MY_PREFS_NAME = "Fooddelivery";
    private static ArrayList<restaurentGetSet> restaurentlist;
    private String Error;
    private RecyclerView recyclerView;
    private String timezoneID;
    private String search = "";
    private String res_name;
    private ProgressDialog progressDialog;
    private ProgressDialog pd;
    private double latitudecur = 0;
    private double longitudecur = 0;
    private TextView txt_nameuser;
    private FrameLayout ll_profile;
    private TextView txt_profile;
    private RelativeLayout rel_main;
    private String Location;
    private ImageView img_profile;
    private String CategoryTotal = "", regId;
//    private SwipyRefreshLayout mSwipeRefreshLayout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private restaurentadapter adapter;
    private ImageView img_MainActivity_closeSearch;
    private static int pageCount;
    public static int numberOfRecord;
    private String subCategoryName;
    public static Typeface tf_opensense_regular;
    public static Typeface tf_opensense_medium;
    private SharedPreferences prefs;
    private int radius;
    private final int PERMISSION_REQUEST_CODE = 1001;
    private final int PERMISSION_REQUEST_CODE1 = 10011;
    private final String[] permission_location = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private final String[] permission_location1 = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private ArrayList<CitylistGetSet> getSet;
    AdRequest adRequest;
    String userId1, DeliveryBoyId;
    AdView mAdView;
    private InterstitialAd mInterstitialAd;

    private static ArrayList<menugetset> productlist;
    RecyclerView gridView;
    RecyclerView mainSearchRecyclerView;
    LinearLayout mainLinearLayout;
    private int quantity;
    private ArrayList<ordergetset> orderlist;
    sqliteHelper sqliteHelper;


    //oakspro

    String announcement=null;
    long startMilliSeconds=-1;
    long endMilliSeconds=-1;
    long presentMilliSeconds;
    int res_id;
    ImageView meatMutton;
    TextView notificationtxt;
    LinearLayout notificationPanel;
    TextView openCloseTime;


    //oakspro end

    public static boolean checkInternet(Context context) {
        // TODO Auto-generated method stub
        ConnectionDetector cd = new ConnectionDetector(context);
        return cd.isConnectingToInternet();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);

        meatMutton=(ImageView)findViewById(R.id.muttonMeat);
        notificationPanel=(LinearLayout) findViewById(R.id.announcePanel);
        openCloseTime=(TextView) findViewById(R.id.openclose_timing);
        notificationtxt=(TextView)findViewById(R.id.marqueeTxt);

        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId();
        Log.e("calculate", doubleToDegree(1.4320961841646465));

        //gene
        // rate key hash for facebook
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MainActivity:", "hhy== " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }






        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String currentLocation = prefs.getString("CityName", null);

        if (currentLocation == null) {
            getCityName();
        }

        if (checkPermission()) {
            gettingGPSLocation();
        } else {
            requestPermission();
        }

        changeStatsBarColor(MainActivity.this);

        gettingSharedPref();

        gettingIntent();

        initializations();

        settingActionBar();

        clickEvents();

//        gridView.setHasFixedSize(true);
//        gridView.setNestedScrollingEnabled(false);
        productlist = new ArrayList<>();

        /* Muked by Rajeswar listeners will be set dynamically
        notificationtxt=findViewById(R.id.marqueeTxt);
        notificationtxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        notificationtxt.setSelected(true);



        meatMutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mutton_intent=new Intent(MainActivity.this, MuttonOrderActivity.class);
                startActivity(mutton_intent);
            }
        });*/


        RelativeLayout cardView = findViewById(R.id.mainOrderMedicine);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iv = new Intent(MainActivity.this, UploadMedicalOrderActivity.class);
                iv.putExtra("res_id",res_id);
                startActivity(iv);
            }
        });
        Typeface openSansSemiBold = Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/OpenSans-Semibold.ttf");
        TextView mainOrderMedicineName = findViewById(R.id.mainOrderMedicineName);
        mainOrderMedicineName.setTypeface(openSansSemiBold);
    }

    private void getCityName() {


        String hp = getString(R.string.link) + getString(R.string.servicepath) + "/restaurant_city.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, hp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //hiding the progressbar after completion
                Log.e("Response", response);
                try {
                    CitylistGetSet temp;
                    JSONObject jo_response = new JSONObject(response);
                    getSet = new ArrayList<>();
                    JSONArray ja_city = jo_response.getJSONArray("city");
                    for (int i = 0; i < ja_city.length(); i++) {
                        temp = new CitylistGetSet();
                        JSONObject cityname = ja_city.getJSONObject(i);
                        String city = cityname.getString("city_name");
                        temp.setName(city);
                        temp.setId(String.valueOf(i));
                        getSet.add(temp);
                    }

                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("CityName", getSet.get(0).getName());
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);


    }

    private void gettingIntent() {
        Intent i = getIntent();
        if (i.getStringExtra("sub_category_name") != null) {
            subCategoryName = i.getStringExtra("sub_category_name");
        }
        if (i.getStringExtra("sub_category_id") != null) {
            String subCategoryId = i.getStringExtra("sub_category_id");
        }
    }

    private void initializations() {

        recyclerView = findViewById(R.id.listview);
        gridView = findViewById(R.id.mainGridView);
        mainLinearLayout = findViewById(R.id.mainLinearLayout);
        mainSearchRecyclerView = findViewById(R.id.mainSearchRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mainSearchRecyclerView.setLayoutManager(mLayoutManager);
        mainSearchRecyclerView.addItemDecoration(new GridDividerDecoration(MainActivity.this));
        mainSearchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        orderlist = new ArrayList<>();

        mDrawerLayout = findViewById(R.id.drawer_layout);
        rel_main = findViewById(R.id.rel_main);
        numberOfRecord = getResources().getInteger(R.integer.numberOfRecords);
        pageCount = 1;

        //setting fonts
        font();
        SharedPreferences prefs1 = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        userId1 = prefs1.getString("userid", null);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
        if (pref.getString("regId", null) != null) {

            regId = pref.getString("regId", null);

            //Registering device id to server

            new RegisterMobile().execute();

        } else {
            BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // checking for type intent filter
                    if (Objects.equals(intent.getAction(), Config.REGISTRATION_COMPLETE)) {
                        // gcm successfully registered
                        // now subscribe to `global` topic to receive app wide notifications
                        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                        displayFirebaseRegId();
                    } else if (Objects.equals(intent.getAction(), Config.PUSH_NOTIFICATION)) {
                        // new push notification is received
                        String message = intent.getStringExtra("message");
                        Toast.makeText(getApplicationContext(), "notification: " + message, Toast.LENGTH_LONG).show();
                    }
                }
            };
        }


        //getting intents numberOfRecords Location.class
        timezoneID = TimeZone.getDefault().getID();

        Intent iv = getIntent();
        String res_id = iv.getStringExtra("res_id");
        res_name = iv.getStringExtra("res_name");
        String manualadd = iv.getStringExtra("manualadd");

        Log.e("res_id", res_id + res_name);
        Log.e("manualadd", "" + manualadd);
        AdShow();
        if (checkPermission()) {
            if (checkInternet(MainActivity.this))
            //Getting Data
            {
                restaurentlist = new ArrayList<>();
                new GetDataAsyncTask().execute();
            } else showErrorDialog(MainActivity.this);
        } else {
            requestPermission();
        }


    }

    private void gettingSharedPref() {
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Location = prefs.getString("CityName", null);
        int noRadius = Integer.parseInt(getString(R.string.radius));
        radius = prefs.getInt("radius", noRadius);

        String regId = getSharedPreferences(SHARED_PREF, MODE_PRIVATE).getString("regId", null);
    }

    private void displayFirebaseRegId() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e("fireBaseRid", "Firebase Reg id: " + regId);

        new RegisterMobile().execute();
    }

    private void settingActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            setupDrawer();
            drawer();
            SpannableString s;
            if (prefs.getString("CityName", null) == null) {
                s = new SpannableString(getString(R.string.txt_home_header));
                s.setSpan(new TypefaceSpan("OpenSans-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                s = new SpannableString(prefs.getString("CityName", null));
                s.setSpan(new TypefaceSpan("OpenSans-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            actionBar.setTitle(s);

        }

    }

    public static void showErrorDialog(Context c) {
        final NiftyDialogBuilder material;
        material = NiftyDialogBuilder.getInstance(c);
        material.withTitle(c.getString(R.string.text_warning)).withMessage(c.getString(R.string.internet_check_error)).withDialogColor(c.getString(R.string.colorErrorDialog)).withButton1Text("OK").setButton1Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                material.cancel();
            }
        }).withDuration(1000).withEffect(Effectstype.Fadein).show();
    }

    private void gettingGPSLocation() {
        GPSTracker gps = new GPSTracker();
        gps.init(MainActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            try {
                latitudecur = gps.getLatitude();
                longitudecur = gps.getLongitude();
                Log.w("Current Location", "Lat: " + latitudecur + "Long: " + longitudecur);
            } catch (NullPointerException | NumberFormatException e) {
                // TODO: handle exception
            }

        } else {
            gps.showSettingsAlert();
        }


    }

    private void clickEvents() {
        img_MainActivity_closeSearch = findViewById(R.id.img_MainActivity_closeSearch);


        final EditText edit_search = findViewById(R.id.edit_search);

        img_MainActivity_closeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_search.setText("");
            }
        });

        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && checkInternet(MainActivity.this)) {
                    if (adapter!=null){
                        mainLinearLayout.setVisibility(View.GONE);
                        mainSearchRecyclerView.setVisibility(View.VISIBLE);
                        orderlist.clear();
                        new GetDataAsyncTask1().execute();
                    }else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this,R.style.CustomAlertDialog);
                        dialog.setTitle("Notification");
                        dialog.setMessage("Please select your city from location settings");
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent iv = new Intent(MainActivity.this, SettingPage.class);
                                iv.putExtra("key", "main");
                                startActivity(iv);
                            }
                        });
                        dialog.create().show();
//                            Toast.makeText(MainActivity.this, "No Restaurant found at this location select any other location from setting", Toast.LENGTH_SHORT).show();
                    }

//                    Error = null;
//                    reset();
//                    restaurentlist.clear();
//                    pageCount = 1;
//                    new GetDataAsyncTasksearch().execute();
                    return true;
                }
                return false;
            }
        });

        // search on home page method
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.length() > 0) {
                    img_MainActivity_closeSearch.setVisibility(View.VISIBLE);
                } else {
                    img_MainActivity_closeSearch.setVisibility(View.INVISIBLE);
                }

                search = s.toString();
                if (search.length() == 0) {
                    if (checkInternet(MainActivity.this)) {
                        mainSearchRecyclerView.setVisibility(View.GONE);
                        mainLinearLayout.setVisibility(View.VISIBLE);
//                        Error = null;
//                        reset();
//                        pageCount = 1;
//                        restaurentlist.clear();
//                        new GetDataAsyncTask().execute();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) { // TODO

            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent iv = new Intent(MainActivity.this, DetailPage.class);
                iv.putExtra("res_id", "" + adapter.moviesList.get(position).getId());
                iv.putExtra("distance", "" + adapter.moviesList.get(position).getDistance());
                startActivity(iv);
                if (getResources().getString(R.string.show_admob_ads).equals("yes")) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();

                    }
                }
            }


            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void reset() {
        CategoryTotal = "";
    }

    private void font() {

        Typeface tf_worksans = Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/WorkSans-Regular.otf");
        tf_opensense_regular = ResourcesCompat.getFont(this, R.font.roboto_regular);
        tf_opensense_medium = ResourcesCompat.getFont(this, R.font.roboto_medium);
        TextView txt_search = findViewById(R.id.txt_search);
        txt_search.setTypeface(tf_worksans);
        TextView txt_rated = findViewById(R.id.txt_rated);
        txt_rated.setTypeface(tf_worksans);
        TextView txt_suggested = findViewById(R.id.txt_suggested);
        txt_suggested.setTypeface(tf_worksans);
        TextView txt_cusine = findViewById(R.id.txt_cusine);
        txt_cusine.setTypeface(tf_worksans);
        TextView txt_medical = findViewById(R.id.txt_medical);
        txt_medical.setTypeface(tf_worksans);
        TextView txt_medical_order = findViewById(R.id.txt_medical_order);
        txt_medical_order.setTypeface(tf_worksans);
        TextView txt_notification = findViewById(R.id.txt_notification);
        txt_notification.setTypeface(tf_worksans);
        TextView txt_fav = findViewById(R.id.txt_fav);
        txt_fav.setTypeface(tf_worksans);
        TextView txt_share = findViewById(R.id.txt_share);
        txt_share.setTypeface(tf_worksans);
        TextView txt_terms = findViewById(R.id.txt_terms);
        txt_terms.setTypeface(tf_worksans);
        TextView txt_aboutus = findViewById(R.id.txt_aboutus);
        txt_aboutus.setTypeface(tf_worksans);
        TextView txt_logout = findViewById(R.id.txt_logout);
        txt_logout.setTypeface(tf_worksans);

        txt_nameuser = findViewById(R.id.txt_nameuser);
        ll_profile = findViewById(R.id.ll_profile);
        txt_nameuser.setTypeface(tf_opensense_regular);
        txt_profile = findViewById(R.id.txt_profile);
        txt_profile.setTypeface(tf_worksans);
        img_profile = findViewById(R.id.img_profile);


    }

    private void drawer() {
        LinearLayout ll_fav = findViewById(R.id.ll_fav);
        LinearLayout ll_share = findViewById(R.id.ll_share);
        final LinearLayout ll_aboutus = findViewById(R.id.ll_aboutus);
        final LinearLayout ll_terms = findViewById(R.id.ll_terms);
        LinearLayout ll_cusine = findViewById(R.id.ll_cusine);
        LinearLayout ll_search = findViewById(R.id.ll_search);
        LinearLayout ll_rated = findViewById(R.id.ll_rated);
        LinearLayout ll_suggested = findViewById(R.id.ll_suggested);
        LinearLayout ll_signout = findViewById(R.id.ll_signout);
        LinearLayout ll_notification = findViewById(R.id.ll_notification);
        LinearLayout ll_medical = findViewById(R.id.ll_medical);
        LinearLayout ll_medical_order = findViewById(R.id.ll_medical_order);

        ll_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(MainActivity.this, Favourite.class);
                startActivity(iv);
            }
        });
        ll_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(MainActivity.this, MyOrderPage.class);
                startActivity(iv);
            }
        });
        ll_medical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(MainActivity.this, UploadMedicalOrderActivity.class);
                iv.putExtra("res_id",res_id);
                startActivity(iv);
            }
        });
        ll_medical_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(MainActivity.this, MedicalOrderActivity.class);
                startActivity(iv);
            }
        });

        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url1 = "Download Near Buy Kumher app" + "\n" + "https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Food Delivery");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, url1);
                startActivity(Intent.createChooser(intent, "Share Food Delivery with"));
            }
        });


        ll_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(MainActivity.this, Aboutus.class);
                startActivity(iv);

            }
        });

        ll_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent iv = new Intent(MainActivity.this, Termcondition.class);
                startActivity(iv);
            }
        });


        ll_cusine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(MainActivity.this, Category.class);
                startActivity(iv);
            }
        });

        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(MainActivity.this, MainActivity.class);
                startActivity(iv);
            }
        });


        ll_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(MainActivity.this, Mapactivity.class);
                startActivity(iv);
            }
        });


        ll_suggested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(MainActivity.this, MostRatedStores.class);
                startActivity(iv);
            }
        });


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        if (prefs.getString("userid", null) != null) {
            String userId = prefs.getString("userid", null);
            String image = prefs.getString("imagepath", null);
            String profileimage = prefs.getString("imageprofile", null);

            Log.e("image121", "" + profileimage);
            if (Objects.equals(userId, "delete")) {
                ll_signout.setVisibility(View.GONE);
                txt_nameuser.setText(R.string.txt_signin);
                txt_profile.setText(R.string.txt_profile);
                ll_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iv = new Intent(MainActivity.this, Login.class);
                        startActivity(iv);
                    }
                });
            } else {
                String uname = prefs.getString("username", null);
                try {
                    Picasso.with(getApplicationContext()).load(profileimage).into(img_profile);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

                ll_signout.setVisibility(View.VISIBLE);
                ll_signout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create(); // Read
                        // Update
                        alertDialog.setTitle(getString(R.string.log_out));
                        alertDialog.setMessage(getString(R.string.error_logout));
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.conti), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // here you can add functions
                                if (LoginManager.getInstance() != null) {
                                    LoginManager.getInstance().logOut();
                                }
                                String prodel = "delete";
                                String userid = "delete";
                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("delete", "" + prodel);
                                editor.putString("userid", "" + userid);
                                editor.apply();
                                Intent iv = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(iv);
                            }
                        });

                        alertDialog.show();
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));

                    }


                });
                txt_nameuser.setText(uname);
                txt_profile.setText(R.string.txt_profile);
                ll_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iv = new Intent(MainActivity.this, Profile.class);
                        startActivity(iv);
                    }
                });
            }
        } else {
            ll_signout.setVisibility(View.GONE);
            txt_profile.setText(R.string.txt_profile);
            txt_nameuser.setText(R.string.txt_signin);
            ll_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iv = new Intent(MainActivity.this, Login.class);
                    startActivity(iv);
                }
            });
        }

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerView.setEnabled(true);
                recyclerView.setClickable(false);
                recyclerView.setFocusable(false);

            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                view.setEnabled(false);
                rel_main.setEnabled(true);
                rel_main.setVisibility(View.VISIBLE);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null) mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_setting) {
            Intent iv = new Intent(MainActivity.this, SettingPage.class);
            iv.putExtra("key", "main");
            startActivity(iv);
            return true;
        }else if (id == R.id.action_cart) {
            if (adapter!=null) {
                Intent iv = new Intent(MainActivity.this, Cart.class);
                iv.putExtra("detail_id", restaurentlist.get(0).getId());
                iv.putExtra("restaurent_name", restaurentlist.get(0).getName());
                iv.putExtra("delivery_charge", restaurentlist.get(0).getDelivery_charge());
                iv.putExtra("minimum_order", restaurentlist.get(0).getDelivery_time());
                iv.putExtra("time", restaurentlist.get(0).getOpen_time() + " TO " + restaurentlist.get(0).getClose_time());
                startActivity(iv);
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this,R.style.CustomAlertDialog);
                dialog.setTitle("Notification");
                dialog.setMessage("Please select your city from location settings");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent iv = new Intent(MainActivity.this, SettingPage.class);
                        iv.putExtra("key", "main");
                        startActivity(iv);
                    }
                });
                dialog.create().show();
            }
            return true;
        }else if (id == R.id.action_refresh){
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }

        // Activate the navigation drawer toggle
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(Gravity.START)) mDrawerLayout.closeDrawer(Gravity.START);
        else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
            builder1.setTitle(getString(R.string.Quit));
            builder1.setMessage(getString(R.string.statementquit));
            builder1.setCancelable(true);
            builder1.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    finishAffinity();
                }
            });
            builder1.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    class GetDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage(getString(R.string.loading));
            pd.setCancelable(true);
            pd.show();
//            if (!mSwipeRefreshLayout.isRefreshing()) {
//                pd.show();
//            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            URL hp;
            restaurentlist.clear();
            try {
                if (subCategoryName == null) {
                    if (res_name == null) {
                        String user = getString(R.string.link) + getString(R.string.servicepath) + "restaurantlist.php?timezone=" + timezoneID + "&" + "lat=" + latitudecur + "&" + "lon=" + longitudecur + "&location=" + Location + "&noofrecords=" + numberOfRecord + "&pageno=" + pageCount + "&radius=" + radius;
                        hp = new URL(user.replace(" ", "%20"));

                    } else {
                        hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "restaurantlist.php?timezone=" + timezoneID + "&" + "lat=" + latitudecur + "&lon=" + longitudecur + "&search=" + res_name + "&noofrecords=" + numberOfRecord + "&pageno=" + pageCount + "&radius=" + radius);
                    }
                } else {
                    String user = getString(R.string.link) + getString(R.string.servicepath) + "restaurantlist.php?timezone=" + timezoneID + "&" + "lat=" + latitudecur + "&" + "lon=" + longitudecur + "&search=" + subCategoryName + "&noofrecords=" + numberOfRecord + "&pageno=" + pageCount + "&radius=" + radius;
                    hp = new URL(user.replace(" ", "%20"));
                }

                Log.e("URLs", "" + hp);
//                writefile("Url "+hp);
                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                Log.d("input", "" + input);
//                writefile("input "+input);
                BufferedReader r = new BufferedReader(new InputStreamReader(input));
                String x;
                x = r.readLine();
                StringBuilder total = new StringBuilder();
                while (x != null) {
                    total.append(x);
                    x = r.readLine();
                }
                JSONArray jObject = new JSONArray(total.toString());
                JSONObject obj = jObject.getJSONObject(0);

                switch (obj.getString("status")) {
                    case "Success":
                        JSONArray jsonArray = obj.getJSONArray("Restaurant_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Log.e("RESTURANT_DATA==>", jsonArray.getJSONObject(i).toString());
//                            writefile("RESTURANT_DATA==> "+jsonArray.getJSONObject(i).toString());
                            restaurentGetSet temp = new restaurentGetSet();
                            JSONObject Obj = jsonArray.getJSONObject(i);
                            temp.setId(Obj.getString("id"));
                            temp.setName(Obj.getString("name"));
                            temp.setLat(Obj.getString("lat"));
                            temp.setLon(Obj.getString("lon"));
                            temp.setDistance(Obj.getString("distance"));
                            temp.setOpen_time(Obj.getString("open_time"));
                            temp.setClose_time(Obj.getString("close_time"));
                            temp.setCurrency(Obj.getString("currency"));
                            temp.setDelivery_time(Obj.getString("delivery_time"));
                            temp.setImage(Obj.getString("image"));
                            temp.setRatting(Obj.getString("ratting"));
                            temp.setRes_status(Obj.getString("res_status"));
                            temp.setDelivery_charge(Obj.getString("del_charge"));

                            AppClass.preferences.storeDataIntoPreFerance(
                                    Preferences.CURRENCY,Obj.getString("currency"));
                            try {
                                JSONArray jCategory = Obj.getJSONArray("Category");
                                String[] temprory = new String[jCategory.length()];
                                for (int j = 0; j < jCategory.length(); j++) {
                                    temprory[j] = jCategory.getString(j);
                                    CategoryTotal += temprory[j];
                                    Log.e("catname12121", "" + CategoryTotal);
                                    temp.setCategory(temprory);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            restaurentlist.add(temp);

                        }
                        break;
                    case "Failed":
                        Error = obj.getString("error");


                        break;
                    default:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, R.string.txt_try_later, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Error = e.getMessage();
            } catch (NullPointerException e) {
                // TODO: handle exception
                Error = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (Error != null) {

                Toast.makeText(MainActivity.this, Error, Toast.LENGTH_SHORT).show();
                new GetCategoryDataAsyncTask().execute("0");
            } else {
                //Log.e("adapter", "" + restaurentlist.size());
                new GetCategoryDataAsyncTask().execute(restaurentlist.get(0).getId());
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                adapter = new restaurentadapter(recyclerView, MainActivity.this, restaurentlist);

                adapter.setOnLoadMoreListener(new restaurentadapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {

                    }
                });

                recyclerView.setAdapter(adapter);
            }
        }
    }

    class LoadMoreData extends AsyncTask<Void, Void, Void> {
        ArrayList data;


        @Override
        protected Void doInBackground(Void... params) {
            URL hp;
            try {

                if (search.length() == 0) {
                    if (subCategoryName == null) {
                        if (res_name == null) {
                            String user = getString(R.string.link) + getString(R.string.servicepath) + "restaurantlist.php?timezone=" + timezoneID + "&" + "lat=" + latitudecur + "&" + "lon=" + longitudecur + "&location=" + Location + "&noofrecords=" + numberOfRecord + "&pageno=" + pageCount + "&radius=" + radius;
                            hp = new URL(user.replace(" ", "%20"));

                        } else {
                            hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "restaurantlist.php?timezone=" + timezoneID + "&" + "lat=" + latitudecur + "&lon=" + longitudecur + "&search=" + res_name + "&noofrecords=" + numberOfRecord + "&pageno=" + pageCount + "&radius=" + radius);
                        }
                    } else {
                        String user = getString(R.string.link) + getString(R.string.servicepath) + "restaurantlist.php?timezone=" + timezoneID + "&" + "lat=" + latitudecur + "&" + "lon=" + longitudecur + "&search=" + subCategoryName + "&noofrecords=" + numberOfRecord + "&pageno=" + pageCount + "&radius=" + radius;
                        hp = new URL(user.replace(" ", "%20"));
                    }
                } else {
                    hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "restaurantlist.php?timezone=" + timezoneID + "&" + "lat=" + latitudecur + "&" + "lon=" + longitudecur + "&" + "search=" + search + "&noofrecords=" + numberOfRecord + "&pageno=" + pageCount + "&radius=" + radius);
                }


                Log.e("URLs", "" + hp);
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
                JSONArray jObject = new JSONArray(total.toString());
                JSONObject obj = jObject.getJSONObject(0);
                data = new ArrayList<restaurentGetSet>();


                switch (obj.getString("status")) {
                    case "Success":
                        JSONArray jsonArray = obj.getJSONArray("Restaurant_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            restaurentGetSet temp = new restaurentGetSet();
                            JSONObject Obj = jsonArray.getJSONObject(i);
                            temp.setId(Obj.getString("id"));
                            temp.setName(Obj.getString("name"));
                            temp.setLat(Obj.getString("lat"));
                            temp.setLon(Obj.getString("lon"));
                            temp.setDistance(Obj.getString("distance"));
                            temp.setOpen_time(Obj.getString("open_time"));
                            temp.setClose_time(Obj.getString("close_time"));
                            temp.setCurrency(Obj.getString("currency"));
                            temp.setDelivery_time(Obj.getString("delivery_time"));
                            temp.setImage(Obj.getString("image"));
                            temp.setRatting(Obj.getString("ratting"));
                            temp.setRes_status(Obj.getString("res_status"));
                            try {
                                JSONArray jCategory = Obj.getJSONArray("Category");
                                String[] temprory = new String[jCategory.length()];
                                for (int j = 0; j < jCategory.length(); j++) {
                                    temprory[j] = jCategory.getString(j);
                                    CategoryTotal += temprory[j];
                                    Log.e("catname12121", "" + CategoryTotal);
                                }
                                temp.setCategory(temprory);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            data.add(temp);


                        }
                        break;
                    case "Failed":
                        Error = obj.getString("error");


                        break;
                    default:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Please try later!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Error = e.getMessage();
            } catch (NullPointerException e) {
                // TODO: handle exception
                Error = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //mSwipeRefreshLayout.setRefreshing(false);
            if (Error != null) {

                Toast.makeText(MainActivity.this, Error, Toast.LENGTH_SHORT).show();


            } else if (data.size() != 0) {
                Log.e("adapter", "" + data.size());
                adapter.setLoaded();
                adapter.addItem(data, restaurentlist.size());

            }
        }
    }

    class GetDataAsyncTasksearch extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
            reset();
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL hp;
            try {


                String Usersearch = getString(R.string.link) + getString(R.string.servicepath) + "restaurantlist.php?timezone=" + timezoneID + "&" + "lat=" + latitudecur + "&" + "lon=" + longitudecur + "&" + "search=" + search + "&noofrecords=" + numberOfRecord + "&pageno=" + pageCount + "&radius=" + radius;
                hp = new URL(Usersearch.replace(" ", "%20"));
                Log.e("URLsearch", "" + hp);
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
                Log.d("URL", "" + total);
                JSONArray jObject = new JSONArray(total.toString());
                JSONObject obj = jObject.getJSONObject(0);

                switch (obj.getString("status")) {
                    case "Success":
                        restaurentlist.clear();
                        JSONArray jsonArray = obj.getJSONArray("Restaurant_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            restaurentGetSet temp = new restaurentGetSet();
                            JSONObject Obj = jsonArray.getJSONObject(i);
                            temp.setId(Obj.getString("id"));
                            temp.setName(Obj.getString("name"));
                            temp.setLat(Obj.getString("lat"));
                            temp.setLon(Obj.getString("lon"));
                            temp.setDistance(Obj.getString("distance"));
                            temp.setOpen_time(Obj.getString("open_time"));
                            temp.setClose_time(Obj.getString("close_time"));
                            temp.setCurrency(Obj.getString("currency"));
                            temp.setDelivery_time(Obj.getString("delivery_time"));
                            temp.setImage(Obj.getString("image"));
                            temp.setRatting(Obj.getString("ratting"));
                            temp.setRes_status(Obj.getString("res_status"));
                            JSONArray jCategory = Obj.getJSONArray("Category");
                            String[] temprory = new String[jCategory.length()];
                            for (int j = 0; j < jCategory.length(); j++) {
                                temprory[j] = jCategory.getString(j);
                                CategoryTotal = CategoryTotal + temprory[j];
                                Log.e("catname12121", "" + CategoryTotal);
                            }
                            temp.setCategory(temprory);
                            restaurentlist.add(temp);

                        }

                        break;
                    case "Failed":
                        Error = obj.getString("error");

                        break;
                    default:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Please try later!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Error = e.getMessage();
            } catch (NullPointerException e) {
                // TODO: handle exception
                Error = e.getMessage();
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
            if (Error != null) {
                Toast.makeText(MainActivity.this, Error, Toast.LENGTH_SHORT).show();
            } else {
                adapter = new restaurentadapter(recyclerView, MainActivity.this, restaurentlist);
                adapter.setOnLoadMoreListener(new restaurentadapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {

                    }
                });
                recyclerView.setAdapter(adapter);
            }


        }

    }

    class GetCategoryDataAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            productlist.clear();
            URL hp;
            try {

                //OAKSPRO
                // SET SCHEDULED ORDERS AND MARQ NOTIFICATIONS
                int resID = 7;

                resID = Integer.parseInt(params[0]) > 0 ? Integer.parseInt(params[0]) : 7;
                res_id = resID;

                try {

                    hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "/getSchedule.php?res_id=" + resID);
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
                    Log.e("URL", "" + totalBuilder);
                    JSONObject jsonObject = new JSONObject(totalBuilder.toString());
                    String success = jsonObject.getString("status");
                    if (success.equals("Success")) {


                        JSONObject scheduleDetails = jsonObject.getJSONObject("Schedule");
                        Calendar currentDate = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");


                        Date finalDay = (Date) sdf.parse(scheduleDetails.getString("end_date"));
                        Date startDay = (Date) sdf.parse(scheduleDetails.getString("start_date"));
                        startMilliSeconds = startDay.getTime();
                        endMilliSeconds = finalDay.getTime();
                        //numberOfDays = (int) ((finalDay.getTime() - currentTime.getTime()) / (3600 * 24 * 1000));


                    }
                }catch(Exception e){
                    //CANNOT GET SCHEDULED ITEMS CONTINUE NORMALLY
                }

                try {
                    hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "/getAnnouncements.php?res_id=" + resID);
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
                    Log.e("URL", "" + totalBuilder);
                    JSONObject jsonObject = new JSONObject(totalBuilder.toString());
                    String success = jsonObject.getString("status");
                    if (success.equals("Success")) {
                        announcement = jsonObject.getJSONObject("Announcement").getString("Announcement");


                    }
                }catch(Exception e){
                    //do nothing continue its not important
                }











                //END OF SCHEDULED ORDERS AND NOTIFICATIONS




                //OAKSPRO END









                productlist.clear();
                hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "/homecatres.php?res_id="+params[0]);
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
                        JSONArray data = Obj1.getJSONArray("Home_Category");
                        for (int iq = 0; iq < data.length(); iq++) {
                            JSONObject jdat = data.getJSONObject(iq);
                            menugetset temp = new menugetset();
                            temp.setId(jdat.getString("id"));
                            temp.setName(jdat.getString("name"));
                            temp.setCreated_at(jdat.getString("created_at"));
                            temp.setPhoto(jdat.getString("Photo"));
                            temp.setRes_id(jdat.getString("res_id"));
                            productlist.add(temp);
                        }

                        Log.e("productlist", productlist.get(0).getId());
                    } else {
                        Log.e("success", "Failed:No data available ");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(MainActivity.this, Obj1.getString("error"), Toast.LENGTH_SHORT).show();
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


            //OAKSPRO SETTING UI OF SCHEDULE

            //OAKSPRO START
            if(announcement!=null && !announcement.equals("")){
                notificationtxt.setText(announcement);
                notificationPanel.setVisibility(View.VISIBLE);
                notificationtxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                notificationtxt.setSelected(true);
            }
            if(startMilliSeconds>0) {
                meatMutton.setVisibility(View.VISIBLE);
                meatMutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mutton_intent = new Intent(MainActivity.this, MuttonOrderActivity.class);
                        mutton_intent.putExtra("res_id", res_id);
                        startActivity(mutton_intent);
                    }
                });
                presentMilliSeconds = System.currentTimeMillis();
                if (endMilliSeconds < presentMilliSeconds) {
                    openCloseTime.setText("Orders Closed");
                    meatMutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Sorry, Too late orders are closed", Toast.LENGTH_LONG).show();

                        }
                    });

                    openCloseTime.setVisibility(View.VISIBLE);
                } else {
                    final long countTime;
                    String temp;
                    if (startMilliSeconds > presentMilliSeconds) {
                        countTime = startMilliSeconds;
                        temp = "Open";
                        meatMutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "Hola !! Orders are not yet opened", Toast.LENGTH_LONG).show();

                            }
                        });

                    } else {
                        countTime = endMilliSeconds;
                        temp = "Close";

                    }
                    final String countType = temp;


                    CountDownTimer mCountDownTimer = new CountDownTimer(countTime, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {


                            presentMilliSeconds = presentMilliSeconds - 1;
                            Long serverUptimeSeconds =
                                    (millisUntilFinished - presentMilliSeconds) / 1000;
                            if (serverUptimeSeconds < 0) {
                                if (countType.equals("Open")) {
                                    openCloseTime.setText("Orders Opened");
                                    meatMutton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent mutton_intent = new Intent(MainActivity.this, MuttonOrderActivity.class);
                                            startActivity(mutton_intent);
                                        }
                                    });
                                    return;
                                } else {
                                    openCloseTime.setText("Orders Closed");
                                    meatMutton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(getApplicationContext(), "Sorry, Too late orders are closed", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                    return;
                                }
                            }

                            String daysLeft = String.format("%d", serverUptimeSeconds / 86400);


                            String hoursLeft = String.format("%d", (serverUptimeSeconds % 86400) / 3600);


                            String minutesLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);


                            String secondsLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);


                            openCloseTime.setVisibility(View.VISIBLE);
                            openCloseTime.setText("Orders Will " + countType + " In " + daysLeft + " D :" + hoursLeft + " Hrs:" + minutesLeft + " Mins:" + secondsLeft + "s");

                        }

                        @Override
                        public void onFinish() {
                            if (countType.equals("Open")) {
                                openCloseTime.setText("Orders Opened");
                            } else {
                                openCloseTime.setText("Orders Closed");
                            }
                        }
                    }.start();
                    //OAKSPRO END
                }
            }

            //OAKSPRO END OF UI SETTING







            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            //set layout manager and adapter for "GridView"
            GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);

            gridView.setLayoutManager(layoutManager);
            gridView.addItemDecoration(new GridDividerDecoration(MainActivity.this));
            if (productlist!=null) {
                GridViewAdapter gridViewAdapter = new GridViewAdapter(MainActivity.this, productlist, new GridItemClickListener() {
                    @Override
                    public void onClickListener(int position) {
                        if (adapter!=null){
                            Intent iv = new Intent(MainActivity.this, DetailPage.class);
                            iv.putExtra("res_id", productlist.get(position).getRes_id());
                            iv.putExtra("distance", "" + adapter.moviesList.get(0).getDistance());
                            iv.putExtra("currentTab", productlist.get(position).getId());
                            Log.e("MainActivity", position + "");
                            startActivity(iv);
                        }
                        else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this,R.style.CustomAlertDialog);
                            dialog.setTitle("Notification");
                            dialog.setMessage("Please select your city from location settings");
                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent iv = new Intent(MainActivity.this, SettingPage.class);
                                    iv.putExtra("key", "main");
                                    startActivity(iv);
                                }
                            });
                            dialog.create().show();
//                            Toast.makeText(MainActivity.this, "No Restaurant found at this location select any other location from setting", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                gridView.setAdapter(gridViewAdapter);
            }

        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, permission_location, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, permission_location1, PERMISSION_REQUEST_CODE1);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void changeStatsBarColor(@NotNull Activity activity) {
        Window window = activity.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                    finish();
                } else requestPermission();
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE1) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                    finish();
                } else requestPermission();
            }
        }
    }

    public static String doubleToDegree(double value) {
        int degree = (int) value;
        double rawMinute = Math.abs((value % 1.0d) * 60.0d);
        Log.e("Raw min", " " + rawMinute);
        String s1 = String.format("%d\u00b0 %d\u2032 %d\u2033", new Object[]{Integer.valueOf(degree), Integer.valueOf((int) rawMinute), Integer.valueOf((int) Math.round((rawMinute % 1.0d) * 60.0d))});
        return "\u03b1 = " + s1 + "\n" + "2\u03b1 = " + findSum(Integer.valueOf((int) Math.round((rawMinute % 1.0d) * 60.0d)), Integer.valueOf((int) rawMinute), degree);
    }

    public static String findSum(int second, int minute, int degree) {
        int s = (second + second) % 60;
        int m = ((((second + second) / 60) + minute) + minute) % 60;
        int d = (((minute + minute) / 60) + degree) + degree;
        return String.format(Locale.ENGLISH, "%d\u00b0 %d\u2032 %d\u2033", new Object[]{Integer.valueOf(d), Integer.valueOf(m), Integer.valueOf(s)});
    }

    public class RegisterMobile extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            URL hp;
            try {

                http:
                hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "token.php?token=" + regId + "&type=android&user_id=" + userId1 + "&delivery_boyid=null");
                Log.d("URL", "" + hp);

                // URL Connection

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
                Log.d("URL", "" + total);

                // Json Parsing

                JSONObject jObject = new JSONObject(total.toString());
                Log.d("URL12", "" + jObject);


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } catch (NullPointerException e) {
                // TODO: handle exception
                e.printStackTrace();

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


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

    @Override
    public void onButtonClickListener(int position, EditText editText, int value) {
        editText.setText(value + "Kg");
        Log.e("quantity", "" + quantity);
    }

    class GetDataAsyncTask1 extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            orderlist.clear();
            URL hp = null;
            String error;
            try {
                orderlist.clear();
                hp = new URL(getString(R.string.link) + getString(R.string.servicepath) + "productsearch.php?searchterm=" + search);
                Log.e("menucategory_id", "" + hp);
                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                Log.d("input", "" + input);
                BufferedReader r = new BufferedReader(new InputStreamReader(input));
                String x = "";
                x = r.readLine();
                StringBuilder total = new StringBuilder();
                while (x != null) {
                    total.append(x);
                    x = r.readLine();
                }
                Log.d("URL", "" + total);
                JSONArray jObject = new JSONArray(total.toString());
                //Log.d("URL12", "" + jObject);
                JSONObject Obj;
                Obj = jObject.getJSONObject(0);
               // Log.e("Obj", Obj.toString());
                ordergetset temp1 = new ordergetset();
               // temp1.setStatus(Obj.getString("status"));
                JSONArray jarr = Obj.getJSONArray("Menu_List");
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject Obj1;
                    Obj1 = jarr.getJSONObject(i);
                    //Log.e("Obj1", Obj1.toString());
                    ordergetset temp = new ordergetset();
                    temp.setId(Obj1.getString("id"));
                    temp.setImage(Obj1.getString("photo"));
                    temp.setName(Obj1.getString("name"));
                    temp.setOldprice(Obj1.getString("oldprice"));
                    temp.setPrice(Obj1.getString("price"));
                    temp.setDesc(Obj1.getString("desc"));
                    temp.setCreated_at(Obj1.getString("created_at"));
                    String value = "0";
                    temp.setCounting(value);
                    orderlist.add(temp);
                    //Log.e("orderlist", orderlist.get(0).getId());
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                error = e.getMessage();
            } catch (NullPointerException e) {
                // TODO: handle exception
                error = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            orderadapter1 orderadapter1 = new orderadapter1(orderlist, "0", adapter.moviesList.get(0).getId(), quantity);
            mainSearchRecyclerView.setAdapter(orderadapter1);
            orderadapter1.setCustomButtonListener(MainActivity.this);
            orderadapter1.notifyDataSetChanged();

        }
    }

    public class orderadapter1 extends RecyclerView.Adapter<orderadapter1.MyViewHolder> {
        String id;
        final String menuid1;
        Cursor cur = null;
        String resid, foodid, foodname, fooddesc, foodkg;
        final int va = 0;
        final String detail_id1;
        String menuid321, foodprice321, restcurrency321, kg,foodpriceOld, foodImage;


        String de;
        boolean b;
        CustomButtonListener customButtonListener;
        private final ArrayList<ordergetset> data1;
        private ArrayList<cartgetset> cartlist;

        private LayoutInflater inflater = null;
        final ArrayList<Integer> quantity = new ArrayList<>();
        final int quen;
        int desk;
        String sa;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            final TextView txt_name;
            final TextView txt_desc;
            final TextView txt_price_old;
            final TextView txt_price;
            public ImageView imageview;
            final ImageButton btn_minus;
            final ImageButton btn_plus;
            final EditText edTextQuantity;
            final ImageView img_categoryImage;
            final EditText txt_kg;
            final CardView card_CategoryAdd;
            final RelativeLayout rel_CategoryAdd;

            MyViewHolder(View view) {
                super(view);
                txt_name = view.findViewById(R.id.txt_name);
                txt_desc = view.findViewById(R.id.txt_desc);
                txt_price_old = view.findViewById(R.id.txt_price_old);
                txt_price = view.findViewById(R.id.txt_price);
                btn_minus = view.findViewById(R.id.btn_minus);
                btn_plus = view.findViewById(R.id.btn_plus);
                edTextQuantity = view.findViewById(R.id.edTextQuantity);
                img_categoryImage = view.findViewById(R.id.img_categoryImage);
                txt_kg = view.findViewById(R.id.txt_kg);
                rel_CategoryAdd = view.findViewById(R.id.rel_CategoryAdd);
                card_CategoryAdd = view.findViewById(R.id.card_CategoryAdd);

            }
        }

        void setCustomButtonListener(CustomButtonListener customButtonListner) {
            this.customButtonListener = customButtonListner;
        }

        orderadapter1(ArrayList<ordergetset> orderlist, String menu_id, String detail_id, int quantity) {

            data1 = orderlist;
            menuid1 = menu_id;
            detail_id1 = detail_id;
            quen = quantity;

            for (int i = quen; i < data1.size(); i++) {
                this.quantity.add(i);
            }
        }

        @Override
        public orderadapter1.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_category, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final int pos = position;
            final MyViewHolder holder1 = holder;

            holder.txt_name.setText(data1.get(position).getName());
            holder.txt_desc.setText(data1.get(position).getDesc());
            holder.txt_price_old.setPaintFlags(holder.txt_price_old.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            try {
                holder.txt_price_old.setText(AppClass.preferences.getCurrency() + Double.parseDouble(data1.get(position).getOldprice()) + "");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                holder.txt_price_old.setText(AppClass.preferences.getCurrency() + data1.get(position).getOldprice() + "");
            }
            try {
                holder.txt_price.setText(AppClass.preferences.getCurrency() + Double.parseDouble(data1.get(position).getPrice()) + "");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                holder.txt_price.setText(AppClass.preferences.getCurrency() + data1.get(position).getPrice() + "");
            }
            //getlist(position);
            if (data1.get(position).getId().equals(menuid321)) {
                de = foodprice321;
                int var = Integer.parseInt(de);
                //Log.e("stringde123", "" + var);
                holder.edTextQuantity.setText("" + de);
                b = true;
                //Log.e("hello", "" + b);
            } else {
                holder.edTextQuantity.setText("" + va);
                b = false;
                //Log.e("hello123", "" + b);
            }

            Picasso.with(MainActivity.this).load(getResources().getString(R.string.link) + getString(R.string.imagepath) + data1.get(position).getImage()).into(holder.img_categoryImage);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

            holder.rel_CategoryAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogAddItemPopup popup = new DialogAddItemPopup(MainActivity.this,
                            data1.get(position).getName(),
                            data1.get(position).getImage(),
                            data1.get(position).getPrice(),
                            data1.get(position).getId(),
                            data1.get(position).getDesc(),
                            new DialogAddItemPopup.OnDataAddClickLisstener() {
                                @Override
                                public void onDataAdd(String price, String mQty, String mSize) {
                                    if (customButtonListener != null) {
                                        desk = Integer.parseInt(mQty);
                                        Log.e("dexk", "" + desk);
                                        try {

                                            if (quantity.size() > 0) {

                                                quantity.set(pos, desk);
                                               // getlist(pos);

                                                if (data1.get(pos).getId().equals(menuid321)) {

                                                    sqliteHelper = new sqliteHelper(MainActivity.this);
                                                    SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
                                                    try {
                                                        cur = db1.rawQuery("UPDATE cart SET resid ='" + detail_id1 + "', foodid='" + menuid1 + "',foodprice ='" + mQty + "',restcurrency='" + data1.get(pos).getPrice() + "',foodpriceOld='" + data1.get(pos).getOldprice() + "',foodImage='" + data1.get(pos).getImage() + "',kg='" + mSize + "' Where menuid ='" + data1.get(pos).getId() + "';", null);
                                                        Log.e("updatequeryalready", "" + "UPDATE cart SET resid ='" + detail_id1 + "', foodid='" + menuid1 + "',foodprice ='" + desk + "',restcurrency='" + data1.get(pos).getPrice() + "' Where menuid ='" + data1.get(pos).getId() + "';");
                                                        if (cur.getCount() != 0) {
                                                            if (cur.moveToFirst()) {
                                                                do {
                                                                    cartgetset obj = new cartgetset();
                                                                    resid = cur.getString(cur.getColumnIndex("resid"));
                                                                    menuid321 = cur.getString(cur.getColumnIndex("menuid"));
                                                                    foodid = cur.getString(cur.getColumnIndex("foodid"));
                                                                    foodname = cur.getString(cur.getColumnIndex("foodname"));
                                                                    foodprice321 = cur.getString(cur.getColumnIndex("foodprice"));
                                                                    fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
                                                                    restcurrency321 = cur.getString(cur.getColumnIndex("restcurrency"));
                                                                    foodpriceOld = cur.getString(cur.getColumnIndex("foodpriceOld"));
                                                                    foodImage = cur.getString(cur.getColumnIndex("foodImage"));
                                                                    kg = cur.getString(cur.getColumnIndex("kg"));
                                                                    obj.setResid(resid);
                                                                    obj.setFoodid(foodid);
                                                                    obj.setMenuid(menuid321);
                                                                    obj.setFoodname(foodname);
                                                                    obj.setFoodprice(foodprice321);
                                                                    obj.setFooddesc(fooddesc);
                                                                    obj.setRestcurrency(restcurrency321);
                                                                    obj.setFoodpriceOld(foodpriceOld);
                                                                    obj.setFoodImage(foodImage);
                                                                    obj.setKg(kg);
//                                                                    Log.e("menuid321updated", "" + menuid321);
//                                                                    Log.e("foodp321updated", "" + foodprice321);
                                                                    cartlist.add(obj);
                                                                } while (cur.moveToNext());
                                                            }
                                                        }
                                                        cur.close();
                                                        db1.close();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    if (data1.get(pos).getId().equals(menuid321)
                                                    ) {
                                                    } else {
                                                        //getlist(pos);
                                                        sqliteHelper = new sqliteHelper(MainActivity.this);
                                                        SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
                                                        ContentValues values = new ContentValues();
                                                        values.put("menuid", "" + data1.get(pos).getId());
                                                        values.put("foodprice", desk);
                                                        values.put("foodname", "" + data1.get(pos).getName());
                                                        values.put("fooddesc", "" + data1.get(pos).getDesc());
                                                        values.put("foodid", menuid1);
                                                        values.put("resid", detail_id1);
                                                        values.put("restcurrency", data1.get(pos).getPrice());
                                                        values.put("foodpriceOld", data1.get(pos).getOldprice());
                                                        values.put("foodImage", data1.get(pos).getImage());
                                                        values.put("kg", mSize);
                                                        db1.insert("cart", null, values);
                                                       // Log.e("inserted values", values.toString());
                                                    }
                                                }
                                            }
                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //((DetailPage)getActivity()).getCart();
                                }
                            });
                    popup.setCanceledOnTouchOutside(true);
                    popup.setCancelable(true);
                    popup.show();
                }
            });
        }

        private void getlist(int position) {

            sqliteHelper = new sqliteHelper(MainActivity.this);
            SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();
            try {
                cur = db1.rawQuery("select * from cart where menuid=" + data1.get(position).getId() + ";", null);
                Log.e("selectmenuid", "select * from cart where menuid=" + data1.get(position).getId() + ";");
                if (cur.getCount() != 0) {

                    if (cur.moveToFirst()) {
                        do {
                            cartgetset obj = new cartgetset();
                            resid = cur.getString(cur.getColumnIndex("resid"));
                            menuid321 = cur.getString(cur.getColumnIndex("menuid"));
                            foodid = cur.getString(cur.getColumnIndex("foodid"));
                            foodname = cur.getString(cur.getColumnIndex("foodname"));
                            foodprice321 = cur.getString(cur.getColumnIndex("foodprice"));
                            fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
                            foodkg = cur.getString(cur.getColumnIndex("kg"));
                            foodImage = cur.getString(cur.getColumnIndex("foodImage"));
                            obj.setResid(resid);
                            obj.setFoodid(foodid);
                            obj.setMenuid(menuid321);
                            obj.setFoodname(foodname);
                            obj.setFooddesc(fooddesc);
                            obj.setFoodImage(foodImage);
                            obj.setKg(foodkg);
                            Log.e("menuid321", menuid321);
                            Log.e("foodp321", "" + foodprice321);
                            cartlist.add(obj);
                        } while (cur.moveToNext());
                    }
                }

                cur.close();
                db1.close();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }


        @Override
        public int getItemCount() {
            //Log.e("sizedata", "" + data1.size());
            return data1.size();
        }
    }

    public void writefile(String log) {
        StringBuilder text = new StringBuilder();
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard,"Log.txt");

            BufferedReader br = new BufferedReader(new FileReader(file));

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            Log.i("Test Log", "text : "+text+" : end");
            FileOutputStream fostream = new FileOutputStream(file);
            BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(fostream));
            text.append(date).append(" ").append(log);
            text.append('\n');
            bwriter.append("Amit Sharma");
            bwriter.newLine();
            Log.i("Test Try", "text : "+text+" : end");
        }
        catch (IOException e) {
            e.printStackTrace();

        }


//        File externalStorageDir = Environment.getExternalStorageDirectory();
//        File myFile = new File(externalStorageDir, "Log.txt");
//        StringBuilder text = new StringBuilder();
//        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
//        String date = df.format(Calendar.getInstance().getTime());
//        if (myFile.exists()) {
//            try {
//                BufferedReader br = new BufferedReader(new FileReader(myFile));
////                FileOutputStream fostream = new FileOutputStream(myFile);
////                OutputStreamWriter oswriter = new OutputStreamWriter(fostream);
//                BufferedWriter bwriter = new BufferedWriter(new FileWriter(myFile));
//
//                String line;
//
//                while ((line = br.readLine()) != null) {
//                    text.append(line);
//                    Log.e("LogB",text.toString());
//                    text.append('\n');
//                }
//                br.close();
//                text.append(date+" "+log);
//                Log.e("Log",text.toString());
//                bwriter.write(date+" "+log);
//                bwriter.newLine();
//                bwriter.close();
////                oswriter.close();
////                fostream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            try {
//                myFile.createNewFile();
////                FileOutputStream fostream = new FileOutputStream(myFile);
////                OutputStreamWriter oswriter = new OutputStreamWriter(fostream);
//                BufferedWriter bwriter = new BufferedWriter(new FileWriter(myFile));
//                BufferedReader br = new BufferedReader(new FileReader(myFile));
//                String line;
//                while ((line = br.readLine()) != null) {
//                    text.append(line);
//                    text.append('\n');
//                }
//                br.close();
//                text.append(date+" "+log);
//                bwriter.write("Hi welcome ");
//                bwriter.newLine();
//                bwriter.write(date+" "+log);
//                bwriter.newLine();
//                bwriter.close();
////                oswriter.close();
////                fostream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
