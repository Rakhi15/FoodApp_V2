package com.healthy.basket.utils;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class sqliteHelper extends SQLiteAssetHelper {
        private static final String DB_NAME = "restaurant.db";
        private static final int DB_VER = 1;

    public sqliteHelper(Context context) {
        super(context, DB_NAME, null,DB_VER );
    }

}
