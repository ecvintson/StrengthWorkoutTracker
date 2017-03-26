package com.example.evan.strengthworkouttracker;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "workouts.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

}
