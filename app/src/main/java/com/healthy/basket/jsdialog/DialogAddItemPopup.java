package com.healthy.basket.jsdialog;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.healthy.basket.R;
import com.healthy.basket.utils.sqliteHelper;
import com.squareup.picasso.Picasso;

public class DialogAddItemPopup extends Dialog implements View.OnClickListener {

    private String mName = "";
    private String mImageUrl = "";
    private String mPrice = "";
    private String mQuantity = "";
    private String mPriceOrig = "";
    private String mMenuId = "";
    private int kg = 1;
    private int qty = 1;

    ImageView img_DialogAddToCart;
    ImageView img_DialogAddToCart_close;
    TextView txt_DialogAddToCart;
    TextView txt_DialogAddToCart_kg;
    TextView txt_DialogAddToCart_kgMinus;
    TextView txt_DialogAddToCart_kgPlus;
    TextView txt_DialogAddToCart_qty;
    TextView txt_DialogAddToCart_qtyMinus;
    TextView txt_DialogAddToCart_qtyPlus;
    TextView txt_DialogAddToCart_amount;
    TextView txt_DialogQuantity;
    CardView card_DialogAddToCart_add;
    OnDataAddClickLisstener lisstener;
    sqliteHelper sqliteHelper;

    public DialogAddItemPopup(@NonNull Context context, String mName, String mImageUrl, String mPrice, String mMenuId,
                              String mQuantity, OnDataAddClickLisstener lisstener) {
        super(context);
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.mPrice = mPrice;
        this.mQuantity = mQuantity;
        this.mMenuId = mMenuId;
        this.lisstener = lisstener;
        this.mPriceOrig = mPrice;
    }

    public DialogAddItemPopup(@NonNull Context context) {
        super(context);
    }

    public DialogAddItemPopup(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogAddItemPopup(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private class GetMenuFromData extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            sqliteHelper = new sqliteHelper(getContext());
            SQLiteDatabase db1 = sqliteHelper.getWritableDatabase();

            try {
                Cursor cur = db1.rawQuery("select * from cart where menuid ='" + mMenuId + "';", null);
                Log.e("cartlisting", "" + ("select * numberOfRecords cart where foodprice <=0;"));
                Log.d("SIZWA", "" + cur.getCount());
                if (cur.getCount() != 0) {
                    if (cur.moveToFirst()) {
                        do {

                            String resid = cur.getString(cur.getColumnIndex("resid"));
                            String foodid = cur.getString(cur.getColumnIndex("foodname"));
                            String menuid = cur.getString(cur.getColumnIndex("menuid"));
                            String foodname = cur.getString(cur.getColumnIndex("foodname"));
                            String foodprice = cur.getString(cur.getColumnIndex("foodprice"));
                            String fooddesc = cur.getString(cur.getColumnIndex("fooddesc"));
                            String restcurrency = cur.getString(cur.getColumnIndex("restcurrency"));
                            String foodpriceOld = cur.getString(cur.getColumnIndex("foodpriceOld"));
                            String kgs = cur.getString(cur.getColumnIndex("kg"));
                            String foodImage = cur.getString(cur.getColumnIndex("foodImage"));

                            qty = Integer.parseInt(foodprice);
                            kg = Integer.parseInt(kgs);

                            calculatePrice();

                            Log.e("resid", resid);
                            Log.e("foodid", foodid);
                            Log.e("menuid", menuid);
                            Log.e("foodname", foodname);
                            Log.e("foodprice", foodprice);
                            Log.e("fooddesc", fooddesc);
                            Log.e("restcurrency", restcurrency);
                            Log.e("foodpriceOld", foodpriceOld);
                            Log.e("kg", kgs);

                        } while (cur.moveToNext());
                    }
                }
                cur.close();
                db1.close();

            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contant_dialog_add_item);
        // Set Height and width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        this.getWindow().setAttributes(lp);

        // If you want to set backgroud resource then remove comment from below line
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Animation for Display Dialog (Enter - Exit)

        if (android.os.Build.VERSION.SDK_INT < 21) {
            // Remove Divider after header (If lollipop OS run below code then it will give error)
            // Dialog Title Remove
            this.findViewById(android.R.id.title).setVisibility(View.GONE);

            int divierId = this.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = this.findViewById(divierId);
            divider.setVisibility(View.GONE);
        }

        img_DialogAddToCart = findViewById(R.id.img_DialogAddToCart);
        img_DialogAddToCart_close = findViewById(R.id.img_DialogAddToCart_close);
        txt_DialogAddToCart = findViewById(R.id.txt_DialogAddToCart);
        txt_DialogAddToCart_kg = findViewById(R.id.txt_DialogAddToCart_kg);
        txt_DialogAddToCart_kgMinus = findViewById(R.id.txt_DialogAddToCart_kgMinus);
        txt_DialogAddToCart_kgPlus = findViewById(R.id.txt_DialogAddToCart_kgPlus);
        txt_DialogAddToCart_qty = findViewById(R.id.txt_DialogAddToCart_qty);
        txt_DialogAddToCart_qtyMinus = findViewById(R.id.txt_DialogAddToCart_qtyMinus);
        txt_DialogAddToCart_qtyPlus = findViewById(R.id.txt_DialogAddToCart_qtyPlus);
        txt_DialogAddToCart_amount = findViewById(R.id.txt_DialogAddToCart_amount);
        txt_DialogQuantity = findViewById(R.id.txt_DialogQuantity);
        card_DialogAddToCart_add = findViewById(R.id.card_DialogAddToCart_add);


        Picasso.with(getContext()).load(getContext().getResources().getString(R.string.link) + getContext().getString(R.string.imagepath) + mImageUrl).into(img_DialogAddToCart);


        txt_DialogAddToCart.setText(mName);
        txt_DialogQuantity.setText(mQuantity);
        txt_DialogAddToCart_amount.setText(String.format("%s %s", getContext().getString(R.string.rs), mPrice));
        txt_DialogAddToCart_kg.setText(String.valueOf(kg));
        txt_DialogAddToCart_qty.setText(String.valueOf(qty));

        txt_DialogAddToCart_kgMinus.setOnClickListener(this);
        txt_DialogAddToCart_kgPlus.setOnClickListener(this);
        txt_DialogAddToCart_qtyMinus.setOnClickListener(this);
        txt_DialogAddToCart_qtyPlus.setOnClickListener(this);
        card_DialogAddToCart_add.setOnClickListener(this);
        img_DialogAddToCart_close.setOnClickListener(this);

        new GetMenuFromData().execute();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_DialogAddToCart_kgMinus:
                if (kg > 1) {
                    kg--;
                    calculatePrice();
                }

                break;
            case R.id.txt_DialogAddToCart_kgPlus:
                kg++;
                calculatePrice();
                break;
            case R.id.txt_DialogAddToCart_qtyMinus:
                if (qty > 1) {
                    qty--;
                    calculatePrice();
                }

                break;
            case R.id.txt_DialogAddToCart_qtyPlus:
                qty++;
                calculatePrice();
                break;
            case R.id.card_DialogAddToCart_add:
                lisstener.onDataAdd(mPrice, String.valueOf(qty), String.valueOf(kg));
                dismiss();
                break;
            case R.id.img_DialogAddToCart_close:
                dismiss();
                break;
        }
    }

    private void calculatePrice() {
        double price = Double.parseDouble(mPriceOrig);
        price = price * kg;
        price = price * qty;
        mPrice = String.valueOf(price);

        txt_DialogAddToCart_amount.setText(String.format("%s %s", getContext().getString(R.string.rs), price));
        txt_DialogAddToCart_kg.setText(String.valueOf(kg));
        txt_DialogAddToCart_qty.setText(String.valueOf(qty));
    }

    public interface OnDataAddClickLisstener {
        public void onDataAdd(String price, String mQty, String mSize);
    }
}
