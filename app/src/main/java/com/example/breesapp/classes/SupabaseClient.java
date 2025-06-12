package com.example.breesapp.classes;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.breesapp.models.DataBinding;
import com.example.breesapp.models.LogRegRequest;
import com.example.breesapp.models.ProfileResponse;
import com.example.breesapp.models.ProfileUpdate;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

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

    public void updateProfile(Context context, String name, final SBC_Callback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("full_name", name);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json")
        );
        SessionManager sessionManager = new SessionManager(context);

        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "profiles?id=eq." + sessionManager.getUserId())
                .method("PATCH", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + sessionManager.getBearerToken())
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

    public void verifyPasswordResetOtp(Context context, String email, String otp, final SBC_Callback callback) {
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
                        try (ResponseBody responseBody = response.body()){
                            String responseBodyString = response.body().string();

                            try {
                                JsonElement jsonElement = new JsonParser().parse(responseBodyString);

                                Gson gson = new Gson();
                                String json = gson.toJson(jsonElement);

                                JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

                                String accessToken = jsonObject.get("access_token").getAsString();

                                SessionManager sessionManager = new SessionManager(context);
                                sessionManager.setBearer(accessToken);
                            } catch (Exception e) {
                                Log.e("JSON_PARSE_ERROR", "Ошибка при парсинге JSON", e);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
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

    public void updateUserPassword(Context context, String newPassword, final SBC_Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("password", newPassword);

            RequestBody body = RequestBody.create(
                    jsonObject.toString(),
                    MediaType.parse("application/json")
            );

            SessionManager sessionManager = new SessionManager(context);

            Request request = new Request.Builder()
                    .url(DOMAIN_NAME + AUTH_PATH + "user")
                    .method("PUT", body)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Authorization", "Bearer " +
                            sessionManager.getBearerToken())
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

    public void changeEmail(Context context, String newEmail, SBC_Callback callback) {
        JSONObject jsonBody = new JSONObject();
        SessionManager sessionManager = new SessionManager(context);
        try {
            jsonBody.put("target_user_id", sessionManager.getUserId());
            jsonBody.put("new_email", newEmail);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH +"rpc/change_user_email_verified")
                .post(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + sessionManager.getBearerToken())
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
    }

    public void uploadAvatar(Context context, Uri uri, String fileName, SBC_Callback callback) {
        String realPath = RealPathUtil.getRealPath(context, uri);
        SessionManager sessionManager = new SessionManager(context);
        if (realPath == null) {
            callback.onFailure(new IOException("Не удалось получить путь файла"));
            return;
        }

        File file = new File(realPath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);

        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, requestBody)
                .build();

        String url = DOMAIN_NAME + "/storage/v1/object/avatars/" + fileName;

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + sessionManager.getBearerToken())
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
                    String errorBody = response.body() != null ? response.body().string() : "Empty response";
                    callback.onFailure(new IOException("Upload failed: " + response.code() + ", Body: " + errorBody));
                }
            }
        });
    }

    public void updateFileUrl(Context context,String url, final SBC_Callback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("avatar_url", url);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json")
        );
        SessionManager sessionManager = new SessionManager(context);

        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "profiles?id=eq." + sessionManager.getUserId())
                .method("PATCH", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + sessionManager.getBearerToken())
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

    public void fetchUserProfile(Context context, SBC_Callback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            callback.onFailure(new IOException("Пользователь не авторизован"));
            return;
        }

        String url = DOMAIN_NAME + REST_PATH + "profiles?id=eq." + userId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " +
                        sessionManager.getBearerToken())
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    callback.onResponse(body);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Неизвестная ошибка";
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code() + ", " + errorBody));
                }
            }
        });
    }

    public interface SBC_Callback {
        void onFailure(IOException e);
        void onResponse(String responseBody);
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) return "";
        return name.substring(lastIndexOf);
    }
}
