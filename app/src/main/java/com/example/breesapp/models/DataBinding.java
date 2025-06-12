package com.example.breesapp.models;

import android.content.Context;
import android.content.SharedPreferences;

public class DataBinding {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences sharedPreferences2;

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences("auth_data", Context.MODE_PRIVATE);
        sharedPreferences2 = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public static void saveBearerToken(String token) {
        sharedPreferences.edit().putString("bearer_token", token).apply();
    }


    public static String getBearerToken() {
        return sharedPreferences.getString("bearer_token", null);
    }

    public static void saveUuidUser(String uuid) {
        sharedPreferences.edit().putString("user_uuid", uuid).apply();
    }

    public static void start(){
        sharedPreferences2.edit().putString("status", "bookAll").apply();
    }

    public static void logined(){
        sharedPreferences2.edit().putString("status", "logined").apply();
    }

    public static void unlogined(){
        sharedPreferences2.edit().putString("status", "unlogined").apply();
    }

    public static String getStatus(){
        return sharedPreferences2.getString("status", null);
    }

    public static String getUuidUser() {
        return sharedPreferences.getString("user_uuid", null);
    }
}
