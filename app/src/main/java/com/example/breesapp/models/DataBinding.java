package com.example.breesapp.models;

import android.content.Context;
import android.content.SharedPreferences;

public class DataBinding {

    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public static void start(){
        sharedPreferences.edit().putString("status", "bookAll").apply();
    }

    public static void logined(){
        sharedPreferences.edit().putString("status", "logined").apply();
    }

    public static void unlogined(){
        sharedPreferences.edit().putString("status", "unlogined").apply();
    }

    public static String getStatus(){
        return sharedPreferences.getString("status", null);
    }
}
