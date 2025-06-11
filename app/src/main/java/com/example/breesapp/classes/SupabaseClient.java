package com.example.breesapp.classes;

import androidx.annotation.NonNull;

import com.example.breesapp.models.DataBinding;
import com.example.breesapp.models.LogRegRequest;
import com.example.breesapp.models.ProfileUpdate;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseClient {
    public static String REST_PATH = "rest/v1/";
    public static String AUTH_PATH = "auth/v1/";
    public static String DOMAIN_NAME = "https://nrwxeidhsclzffyojfvq.supabase.co/";
    private static String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5yd3hlaWRoc2NsemZmeW9qZnZxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5MjE3MTgsImV4cCI6MjA2NDQ5NzcxOH0.b_mijWc_Pm_rU0Glt8GYwkB_IKeu2tIVaLSzQJ_txR4";
    OkHttpClient client = new OkHttpClient();

    public void registr(LogRegRequest loginRequest, final SBC_Callback callback){
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(loginRequest);
        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(DOMAIN_NAME + AUTH_PATH + "signup")
                .method("POST", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    callback.onFailure(new IOException("Ошибка сервера: " + response));
                }
            }
        });
    }

    public void updateProfile(ProfileUpdate profile, final SBC_Callback callback) {
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(profile);
        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "profiles?id=eq." + DataBinding.getUuidUser())
                .method("PATCH", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code()));
                }
            }
        });
    }

    public void login(LogRegRequest loginRequest, SBC_Callback callback) {
        MediaType mediaType = MediaType.get("application/json");
        String json = new Gson().toJson(loginRequest);
        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(DOMAIN_NAME + AUTH_PATH + "token?grant_type=password")
                .post(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    callback.onResponse(body);
                } else {
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code()));
                }
            }
        });
    }

    public void sendPasswordResetOtp(String email, final SBC_Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);

            RequestBody body = RequestBody.create(
                    jsonObject.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(DOMAIN_NAME + AUTH_PATH + "recover")
                    .post(body)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onResponse(response.body().string());
                    } else {
                        callback.onFailure(new IOException("Ошибка сервера: " + response.code()));
                    }
                }
            });
        } catch (Exception e) {
            callback.onFailure(new IOException("Ошибка создания запроса: " + e.getMessage()));
        }
    }

    public void verifyPasswordResetOtp(String email, String otp, final SBC_Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("token", otp);
            jsonObject.put("type", "recovery");

            RequestBody body = RequestBody.create(
                    jsonObject.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(DOMAIN_NAME + AUTH_PATH + "verify")
                    .post(body)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onResponse(response.body().string());
                    } else {
                        callback.onFailure(new IOException("Ошибка сервера: " + response.code()));
                    }
                }
            });
        } catch (Exception e) {
            callback.onFailure(new IOException("Ошибка создания запроса: " + e.getMessage()));
        }
    }

    public void updateUserPassword(String newPassword, final SBC_Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("password", newPassword);

            RequestBody body = RequestBody.create(
                    jsonObject.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(DOMAIN_NAME + AUTH_PATH + "user")
                    .method("PUT", body)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Authorization", DataBinding.getBearerToken())
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onResponse(response.body().string());
                    } else {
                        callback.onFailure(new IOException("Ошибка сервера: " + response.code()));
                    }
                }
            });
        } catch (Exception e) {
            callback.onFailure(new IOException("Ошибка создания запроса: " + e.getMessage()));
        }
    }

    public interface SBC_Callback {
        void onFailure(IOException e);
        void onResponse(String responseBody);
    }
}
