package com.example.breesapp.classes;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.breesapp.models.ProfileResponse;
import com.google.gson.Gson;

import java.io.IOException;

public class ProfileDataFetcher {

    public void loadUserProfile(Context context, TextView name_holder, TextView email_holder, ImageView avatar) {
        SessionManager sessionManager = new SessionManager(context);
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            return;
        }

        SupabaseClient supabaseClient = new SupabaseClient();

        supabaseClient.fetchUserProfile(context, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
            }

            @Override
            public void onResponse(String responseBody) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        Gson gson = new Gson();
                        ProfileResponse[] profiles = gson.fromJson(responseBody, ProfileResponse[].class);

                        if (profiles != null && profiles.length > 0) {
                            ProfileResponse profile = profiles[0];

                            SessionManager sessionManager = new SessionManager(context);
                            sessionManager.setName(profile.getFull_name());

                            name_holder.setText(profile.getFull_name());

                            email_holder.setText(sessionManager.getEmail());

                            if (profile.getAvatar_url() != null && !profile.getAvatar_url().isEmpty()) {
                                String avatarUrl = profile.getAvatar_url();

                                if (!avatarUrl.startsWith("http")) {
                                    avatarUrl = "https://nrwxeidhsclzffyojfvq.supabase.co/storage/v1/object/public/avatars/"  + avatarUrl;
                                }

                                if (!avatarUrl.equals("images.png")){
                                    sessionManager.setAvatar("custom");
                                }else{
                                    sessionManager.setAvatar("default");
                                }

                                Glide.with(context)
                                        .load(avatarUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(avatar);
                            }
                        } else {
                        }
                    } catch (Exception e) {
                    }
                });
            }
        });
    }
}
