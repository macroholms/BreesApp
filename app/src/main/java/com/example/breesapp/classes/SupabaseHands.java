package com.example.breesapp.classes;

import android.content.Context;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseHands {

    public Response updateProfiles(Context context, String full_name) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"full_name\": \"" + full_name
                + "\" }");
        Request request = new Request.Builder()
                .url("https://nrwxeidhsclzffyojfvq.supabase.co/rest/v1/profiles?id=eq." +
                SupabaseData.getUserId(context))
                .method("PATCH", body)
                .addHeader("apikey",
                        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5yd3hlaWRoc2NsemZmeW9qZnZxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5MjE3MTgsImV4cCI6MjA2NDQ5NzcxOH0.b_mijWc_Pm_rU0Glt8GYwkB_IKeu2tIVaLSzQJ_txR4")
                .addHeader("Authorization", "Bearer " +
                SupabaseData.getAccessToken(context))
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
