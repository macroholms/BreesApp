package com.example.breesapp.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_BEARER_TOKEN = "bearer_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PIN = "pin";
    private static final String KEY_IMAGE = "avatar";

    private SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void createLoginSession(String email, String password, String token, String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        try {
            editor.putString(KEY_PASSWORD, Crypt.encrypt(password));
            editor.putString(KEY_BEARER_TOKEN, token);
            editor.putString(KEY_USER_ID, userId);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }
    public void setEmail(String email) {
        sharedPreferences.edit().putString(KEY_EMAIL, email).apply();
    }
    public String getName() {
        return sharedPreferences.getString(KEY_NAME, null);
    }
    public void setName(String email) {
        sharedPreferences.edit().putString(KEY_NAME, email).apply();
    }

    public String getPassword() {
        String encrypted = sharedPreferences.getString(KEY_PASSWORD, null);
        if (encrypted != null) {
            try {
                return Crypt.decrypt(encrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setPassword(String password){
        try {
            sharedPreferences.edit().putString(KEY_PASSWORD, Crypt.encrypt(password)).apply();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getBearerToken() {
        return sharedPreferences.getString(KEY_BEARER_TOKEN, null);
    }
    public void setBearer(String Token){
        sharedPreferences.edit().putString(KEY_BEARER_TOKEN, Token).apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void setUserId(String id) {
        sharedPreferences.edit().putString(KEY_USER_ID, id).apply();
    }

    public String getAvatar() {
        return sharedPreferences.getString(KEY_IMAGE, null);
    }

    public void setAvatar(String id) {
        sharedPreferences.edit().putString(KEY_IMAGE, id).apply();
    }

    public void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_BEARER_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_PIN);
        editor.apply();
    }


    public void savePin(String pin) {
        String encodedPin = Base64.encodeToString(pin.getBytes(), Base64.DEFAULT);
        sharedPreferences.edit().putString(KEY_PIN, encodedPin).apply();
    }

    public String getPin() {
        String encodedPin = sharedPreferences.getString(KEY_PIN, null);
        if (encodedPin != null) {
            byte[] decodedBytes = Base64.decode(encodedPin, Base64.DEFAULT);
            return new String(decodedBytes);
        }
        return null;
    }

    public boolean isPinSet() {
        return getPin() != null && !getPin().isEmpty();
    }

    public boolean isLoggedIn() {
        return getEmail() != null && getBearerToken() != null;
    }

    public boolean canAutoLogin() {
        return getEmail() != null && getPassword() != null &&
                !getEmail().isEmpty() && !getPassword().isEmpty();
    }
}
