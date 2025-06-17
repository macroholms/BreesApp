package com.example.breesapp.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.breesapp.interfaces.DataItem;
import com.example.breesapp.models.BalanceHistory;
import com.example.breesapp.models.DataBinding;
import com.example.breesapp.models.LogRegRequest;
import com.example.breesapp.models.ProfileResponse;
import com.example.breesapp.models.ProfileUpdate;
import com.example.breesapp.models.Transaction;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
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
                        String responseBodyString;
                        try (ResponseBody responseBody = response.body()) {
                            if (responseBody == null) {
                                callback.onFailure(new IOException("Пустой ответ от сервера"));
                                return;
                            }

                            responseBodyString = responseBody.string();
                        }

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

                        callback.onResponse(responseBodyString);
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

    public void uploadAvatarAndUpdateUser(Context context, Uri uri, SBC_Callback callback) {
        String realPath = RealPathUtil.getRealPath(context, uri);
        SessionManager sessionManager = new SessionManager(context);

        if (realPath == null) {
            callback.onFailure(new IOException("Не удалось получить путь файла"));
            return;
        }

        String userId = sessionManager.getUserId();
        String fileName = "profile_" + userId + ".png";

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
                    updateAvatarUrlInDatabase(context, url, callback);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Empty response";
                    callback.onFailure(new IOException("Upload failed: " + response.code() + ", Body: " + errorBody));
                }
            }
        });
    }

    public void updateAvatarUrlInDatabase(Context context, String avatarUrl, SBC_Callback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("avatar_url", avatarUrl);
        } catch (JSONException e) {
            return;
        }

        RequestBody body = RequestBody.create(
                MediaType.get("application/json"), jsonBody.toString());

        String updateUrl = DOMAIN_NAME + "/rest/v1/users?id=eq." + userId;

        Request request = new Request.Builder()
                .url(updateUrl)
                .patch(body)
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
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if (response.isSuccessful()) {
                        callback.onResponse("Аватар успешно обновлен");
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Empty response";
                        callback.onFailure(new IOException("DB update failed: " + response.code() + ", Body: " + errorBody));
                    }
                } catch (IOException e) {
                    callback.onFailure(e);
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

    public void increaseBalance(Context context, Float amount, SBC_Callback callback) {
        JSONObject jsonBody = new JSONObject();
        SessionManager sessionManager = new SessionManager(context);
        try {
            jsonBody.put("userid", sessionManager.getUserId());
            jsonBody.put("amount", amount);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonBody.toString());
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "rpc/increase_balance")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
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
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code()));
                }
            }
        });
    }

    public void fetchProfiles(Context context, SBC_Callback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            return;
        }

        String url = DOMAIN_NAME + REST_PATH + "profiles?select=*";

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

    public List<DataItem> fetchTransactions(Context context) throws IOException {
        List<DataItem> combinedData = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(context);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("input_uuid", sessionManager.getUserId());

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(DOMAIN_NAME + REST_PATH + "rpc/get_transactions_by_uuid")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("apikey", API_KEY)
                    .addHeader("Authorization", "Bearer "
                            + sessionManager.getBearerToken())
                    .build();

            Response transactionsResponse = client.newCall(request).execute();
            if (transactionsResponse.isSuccessful() && transactionsResponse.body() != null) {
                String responseBody = transactionsResponse.body().string();
                Log.e("Transactions Response", responseBody);

                try {
                    JSONArray jsonArray = new JSONArray(responseBody);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject transactionObj = jsonArray.getJSONObject(i);
                        Transaction transaction = new Transaction(
                                transactionObj.getInt("id"),
                                transactionObj.getString("created_at"),
                                transactionObj.getString("id_sender"),
                                transactionObj.getString("id_recipient"),
                                transactionObj.getLong("money")
                        );
                        combinedData.add(new TransactionItem(transaction));
                    }
                } catch (JSONException e) {
                    Log.e("JSON Error", "Ошибка разбора транзакций: " + e.getMessage());
                }
            } else {
                Log.e("HTTP Error", "Не удалось получить транзакции: " + transactionsResponse.code());
            }
        } catch (IOException e) {
            Log.e("Network Error", "Ошибка сети при запросе транзакций: " + e.getMessage());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            HttpUrl balanceHistoryUrl = HttpUrl.parse(DOMAIN_NAME + REST_PATH + "balance_history").newBuilder()
                    .build();

            Request balanceHistoryRequest = new Request.Builder()
                    .url(balanceHistoryUrl)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Authorization", "Bearer " + sessionManager.getBearerToken())
                    .build();
            Response balanceHistoryResponse = client.newCall(balanceHistoryRequest).execute();
            if (balanceHistoryResponse.isSuccessful() && balanceHistoryResponse.body() != null) {
                String responseBody = balanceHistoryResponse.body().string();
                Log.e("Balance History Response", responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(responseBody);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject balanceHistoryObj = jsonArray.getJSONObject(i);
                        BalanceHistory balanceHistory = new BalanceHistory(
                                balanceHistoryObj.getInt("id"),
                                balanceHistoryObj.getString("created_at"),
                                balanceHistoryObj.getString("user_id"),
                                balanceHistoryObj.getString("type"),
                                balanceHistoryObj.getLong("value")
                        );
                        combinedData.add(new BalanceHistoryItem(balanceHistory));
                    }
                } catch (JSONException e) {
                    Log.e("JSON Error", "Ошибка разбора истории баланса: " + e.getMessage());
                }
            } else {
                Log.e("HTTP Error", "Не удалось получить историю баланса: " + balanceHistoryResponse.code());
            }
        } catch (IOException e) {
            Log.e("Network Error", "Ошибка сети при запросе истории баланса: " + e.getMessage());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        Collections.sort(combinedData, new Comparator<DataItem>() {
            @Override
            public int compare(DataItem item1, DataItem item2) {
                Instant date1 = null;
                Instant date2 = null;

                try {
                    if (item1.getItemType() == 0) {
                        date1 = Instant.from(formatter.parse(((TransactionItem) item1).getTransaction().getCreatedAt()));
                    } else {
                        date1 = Instant.from(formatter.parse(((BalanceHistoryItem) item1).getBalanceHistory().getCreatedAt()));
                    }

                    if (item2.getItemType() == 0) {
                        date2 = Instant.from(formatter.parse(((TransactionItem) item2).getTransaction().getCreatedAt()));
                    } else {
                        date2 = Instant.from(formatter.parse(((BalanceHistoryItem) item2).getBalanceHistory().getCreatedAt()));
                    }

                    return date2.compareTo(date1);

                } catch (Exception e) {
                    Log.e("Date Parse Error", "Ошибка парсинга даты", e);
                    return 0;
                }
            }
        });

        return combinedData;
    }

    public void transferMoney(Context context, String Email, Float amount, SBC_Callback callback) {
        JSONObject jsonBody = new JSONObject();
        SessionManager sessionManager = new SessionManager(context);
        try {
            jsonBody.put("amount", amount);
            jsonBody.put("recipient_email", Email);
            jsonBody.put("sender_id", sessionManager.getUserId());

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonBody.toString());
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "rpc/transfer_money")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
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
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code()));
                }
            }
        });
    }

    public void getMonthlyStatistic(Context context, Integer Month, Integer Year, SBC_Callback callback) {
        JSONObject jsonBody = new JSONObject();
        SessionManager sessionManager = new SessionManager(context);
        try {
            jsonBody.put("p_user_id", sessionManager.getUserId());
            jsonBody.put("p_month", Month);
            jsonBody.put("p_year", Year);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonBody.toString());
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "rpc/get_monthly_statistics")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
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
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code()));
                }
            }
        });
    }

    public void getReceivedMails(Context context, SBC_Callback callback) {
        JSONObject jsonBody = new JSONObject();
        SessionManager sessionManager = new SessionManager(context);
        try {
            jsonBody.put("user_id", sessionManager.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonBody.toString());
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "rpc/get_received_letters")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
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
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code()));
                }
            }
        });
    }

    public void getSentMails(Context context, SBC_Callback callback) {
        JSONObject jsonBody = new JSONObject();
        SessionManager sessionManager = new SessionManager(context);
        try {
            jsonBody.put("user_id", sessionManager.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonBody.toString());
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "rpc/get_sent_letters")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
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
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code()));
                }
            }
        });
    }

    public void updateRecipientVisibility(Context context, String id, SBC_Callback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            callback.onFailure(new IOException("User ID не найден"));
            return;
        }

        String url = DOMAIN_NAME + REST_PATH + "eletters?id=eq." + id;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("recipient_visibility", false);
        } catch (JSONException e) {
            return;
        }

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
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
                    callback.onResponse("Обновление прошло успешно");
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Неизвестная ошибка";
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code() + ", " + errorBody));
                }
            }
        });
    }

    public void updateFavouriteStatus(Context context, String id, Boolean status, SBC_Callback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            callback.onFailure(new IOException("User ID не найден"));
            return;
        }

        String url = DOMAIN_NAME + REST_PATH + "eletters?id=eq." + id;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("favoutrite", status);
        } catch (JSONException e) {
            return;
        }

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
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
                    callback.onResponse("Обновление прошло успешно");
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Неизвестная ошибка";
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code() + ", " + errorBody));
                }
            }
        });
    }

    public void fetchGroupsByUserId(Context context, SBC_Callback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            callback.onFailure(new IOException("User ID не найден"));
            return;
        }

        String url = DOMAIN_NAME + "rest/v1/elettersGroup?select=id,tittle,color,user_id&user_id=eq." + userId;

        Request request = new Request.Builder()
                .url(url)
                .get()
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

    public void addNewGroup(Context context, String title, String color, SBC_Callback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            callback.onFailure(new IOException("User ID не найден"));
            return;
        }

        String url = DOMAIN_NAME + "/rest/v1/elettersGroup";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tittle", title);
            jsonObject.put("color", color);
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            return;
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
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
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Неизвестная ошибка";
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code() + ", " + errorBody));
                }
            }
        });
    }

    public void updateGroupById(Context context, int groupId, String newTitle, String newColor, SBC_Callback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            callback.onFailure(new IOException("User ID не найден"));
            return;
        }

        String url = DOMAIN_NAME + "rest/v1/elettersGroup?id=eq." + groupId;

        JSONObject jsonObject = new JSONObject();
        try {
            if (newTitle != null) jsonObject.put("tittle", newTitle);
            if (newColor != null) jsonObject.put("color", newColor);
        } catch (JSONException e) {
            return;
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + sessionManager.getBearerToken())
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Неизвестная ошибка";
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code() + ", " + errorBody));
                }
            }
        });
    }

    public void deleteGroupWithCleanup(Context context, int groupId, SBC_Callback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            callback.onFailure(new IOException("User ID не найден"));
            return;
        }

        String updateUrl = DOMAIN_NAME + "/rest/v1/eletters?groupID=eq." + groupId;

        JSONObject updateData = new JSONObject();
        try {
            updateData.put("groupID", JSONObject.NULL);
        } catch (JSONException e) {
            return;
        }

        RequestBody updateBody = RequestBody.create(
                updateData.toString(),
                MediaType.get("application/json")
        );

        Request updateRequest = new Request.Builder()
                .url(updateUrl)
                .patch(updateBody)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + sessionManager.getBearerToken())
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build();

        client.newCall(updateRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response updateResponse) throws IOException {
                if (!updateResponse.isSuccessful()) {
                    String err = updateResponse.body() != null ? updateResponse.body().string() : "Неизвестная ошибка";
                    callback.onFailure(new IOException("Ошибка при обнулении group_id: " + updateResponse.code() + ", " + err));
                    return;
                }

                String deleteUrl = DOMAIN_NAME + "/rest/v1/elettersGroup?id=eq." + groupId;

                Request deleteRequest = new Request.Builder()
                        .url(deleteUrl)
                        .delete()
                        .addHeader("apikey", API_KEY)
                        .addHeader("Authorization", "Bearer " + sessionManager.getBearerToken())
                        .addHeader("Content-Type", "application/json")
                        .build();

                client.newCall(deleteRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        callback.onFailure(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response deleteResponse) throws IOException {
                        if (deleteResponse.isSuccessful()) {
                            callback.onResponse("Группа успешно удалена");
                        } else {
                            String err = deleteResponse.body() != null ? deleteResponse.body().string() : "Неизвестная ошибка";
                            callback.onFailure(new IOException("Ошибка при удалении группы: " + deleteResponse.code() + ", " + err));
                        }
                    }
                });
            }
        });
    }

    public void updateGroupId(Context context, String id, String groupid, SBC_Callback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            callback.onFailure(new IOException("User ID не найден"));
            return;
        }

        String url = DOMAIN_NAME + REST_PATH + "eletters?id=eq." + id;

        JSONObject jsonBody = new JSONObject();
        if (groupid == null){
            try {
                jsonBody.put("groupID", JSONObject.NULL);
            } catch (JSONException e) {
                return;
            }
        }
        else{
            try {
                jsonBody.put("groupID", groupid);
            } catch (JSONException e) {
                return;
            }
        }


        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
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
                    callback.onResponse("Обновление прошло успешно");
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Неизвестная ошибка";
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code() + ", " + errorBody));
                }
            }
        });
    }

    public void createEletter(Context context, String content, String fileUrl, String recipientEmail, String theme, SBC_Callback callback) {
        JSONObject jsonBody = new JSONObject();
        SessionManager sessionManager = new SessionManager(context);

        try {
            jsonBody.put("content", content);
            if (!fileUrl.equals(""))
                jsonBody.put("file_url", fileUrl);
            else
                jsonBody.put("file_url", "{}");
            jsonBody.put("id_sender", sessionManager.getUserId());
            jsonBody.put("recipient_email", recipientEmail);
            jsonBody.put("theme", theme);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onFailure(new IOException("Ошибка формирования JSON"));
            return;
        }

        MediaType mediaType = MediaType.get("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonBody.toString());

        String url = DOMAIN_NAME + REST_PATH + "rpc/create_eletternew";

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
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
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    callback.onFailure(new IOException("Ошибка сервера: " + response.code()));
                }
            }
        });
    }

    public void uploadFileToSupabaseStorage(Context context, String content, String recipientEmail, String theme, Uri fileUri, SBC_Callback callback) {
        String realPath = RealPathUtil.getRealPath(context, fileUri);
        SessionManager sessionManager = new SessionManager(context);
        if (realPath == null) {
            callback.onFailure(new IOException("Не удалось получить путь файла"));
            return;
        }

        File file = new File(realPath);

        String uniqueFileName = "file_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + getFileExtension(file);

        String url = DOMAIN_NAME + "storage/v1/object/elettersfiles/" + uniqueFileName;

        String mimeType = getMimeTypeByExtension(file);
        if (mimeType == null) mimeType = "application/octet-stream";

        MediaType mediaType = MediaType.get(mimeType);
        RequestBody body = RequestBody.create(mediaType, file);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + sessionManager.getBearerToken())
                .addHeader("apikey", API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String fileUrl = DOMAIN_NAME + "storage/v1/object/public/elettersfiles/" + uniqueFileName;
                    createEletter(context, content, fileUrl, recipientEmail, theme, new SBC_Callback() {
                        @Override
                        public void onFailure(IOException e) {

                        }

                        @Override
                        public void onResponse(String responseBody) {

                        }
                    });
                    callback.onResponse(fileUrl);
                } else {
                    callback.onFailure(new IOException("Ошибка загрузки файла: " + response.code()));
                }
            }
        });
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) return "";
        return name.substring(lastIndexOf);
    }

    public String getMimeTypeByExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "application/octet-stream"; // Нет расширения
        }

        String ext = fileName.substring(dotIndex + 1).toLowerCase();

        switch (ext) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }

    public interface SBC_Callback {
        void onFailure(IOException e);
        void onResponse(String responseBody);
    }
}
