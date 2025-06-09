package com.example.breesapp.classes;

import android.content.Context;

public class SupabaseData {

    // Сохранение access_token
    public static void saveAccessToken(Context context, String token) {
        android.content.SharedPreferences sharedPref = context.getSharedPreferences(
                "supabase_auth", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("access_token", token);
        editor.apply();
    }

    // Сохранение user.id
    public static void saveUserId(Context context, String userId) {
        android.content.SharedPreferences sharedPref = context.getSharedPreferences(
                "supabase_auth", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user_id", userId);
        editor.apply();
    }

    // Получение access_token
    public static String getAccessToken(Context context) {
        android.content.SharedPreferences sharedPref = context.getSharedPreferences(
                "supabase_auth", Context.MODE_PRIVATE);
        return sharedPref.getString("access_token", null);
    }

    // Получение user.id
    public static String getUserId(Context context) {
        android.content.SharedPreferences sharedPref = context.getSharedPreferences(
                "supabase_auth", Context.MODE_PRIVATE);
        return sharedPref.getString("user_id", null);
    }
}
