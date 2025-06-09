package com.example.breesapp.classes;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class SupabaseAuth {

    private static Context context;

    public SupabaseAuth(Context context) {
        this.context = context;
    }

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5yd3hlaWRoc2NsemZmeW9qZnZxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5MjE3MTgsImV4cCI6MjA2NDQ5NzcxOH0.b_mijWc_Pm_rU0Glt8GYwkB_IKeu2tIVaLSzQJ_txR4";
    private static final String AUTH_SIGNUP_URL = "https://nrwxeidhsclzffyojfvq.supabase.co/auth/v1/token?grant_type=password";
    private static final String AUTH_TOKEN_URL = "https://nrwxeidhsclzffyojfvq.supabase.co/auth/v1/token?grant_type=password";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    public Response signUp(String email, String password) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password));
        Request request = new Request.Builder()
                .url(AUTH_SIGNUP_URL)
                .method("POST", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response;

        /*Log.e("!!!!!!!!!!!!!", String.valueOf(response.code()));
        Log.e("!!!!!!!!!!!!!", response.body().toString());
        if (response.isSuccessful()) {
            try {
                String responseData = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseData);
                String accessToken = jsonResponse.optString("access_token");
                JSONObject userObject = jsonResponse.optJSONObject("user");
                String userId = userObject != null ? userObject.optString("id") : null;
                if (!accessToken.isEmpty() && userId != null) {
                    SupabaseData.saveAccessToken(context, accessToken);
                    SupabaseData.saveUserId(context ,userId);
                    Log.d("SupabaseReg", "Access token and User ID saved.");
                    Log.d("SupabaseReg", "Token: " + accessToken);
                    Log.d("SupabaseReg", "User ID: " + userId);
                } else {
                    Log.w("SupabaseReg", "Missing access token or user ID");
                }
            } catch (JSONException e) {
                Log.e("SupabaseReg", "JSON parsing error: " + e.getMessage());
            }
        } else {
            System.out.println(response.body().string());
        }*/
    }

    public void signIn(String email, String password) {
        String jsonBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(AUTH_TOKEN_URL)
                .post(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);

                        String accessToken = jsonResponse.optString("access_token");
                        JSONObject userObject = jsonResponse.optJSONObject("user");
                        String userId = userObject != null ? userObject.optString("id") : null;

                        if (!accessToken.isEmpty() && userId != null) {
                            SupabaseData.saveAccessToken(context, accessToken);
                            SupabaseData.saveUserId(context ,userId);
                            Log.d("SupabaseAuth", "Access token and User ID saved.");
                            Log.d("SupabaseAuth", "Token: " + accessToken);
                            Log.d("SupabaseAuth", "User ID: " + userId);
                        } else {
                            Log.w("SupabaseAuth", "Missing access token or user ID");
                        }

                    } catch (JSONException e) {
                        Log.e("SupabaseAuth", "JSON parsing error: " + e.getMessage());
                    }
                } else {
                    System.out.println("Sign in failed: " + response.code());
                    System.out.println(response.body().string());
                }
            }
        });
    }
}
