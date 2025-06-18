package com.example.breesapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.breesapp.R;
import com.example.breesapp.activities.TopUpActivity;
import com.example.breesapp.activities.TransactionActivity;
import com.example.breesapp.activities.TransactionsHistoryActivity;
import com.example.breesapp.adapters.CombinedMoneyAdapter;
import com.example.breesapp.classes.NumberFormatter;
import com.example.breesapp.classes.SessionManager;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.databinding.FragmentHomeBinding;
import com.example.breesapp.interfaces.DataItem;
import com.example.breesapp.models.ProfileResponse;
import com.example.breesapp.models.UserModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    TextView name;
    TextView balance;
    ImageView avatar;
    RecyclerView recyclerView;
    CombinedMoneyAdapter combinedMoneyAdapter;
    SupabaseClient supabaseClient;
    ImageButton transac_btn, btn_transac_intent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        name = root.findViewById(R.id.name_place_holder);
        balance = root.findViewById(R.id.balance_place_holder);
        avatar = root.findViewById(R.id.avatar_place_holder);
        recyclerView = root.findViewById(R.id.recent_rv);

        btn_transac_intent = root.findViewById(R.id.transac_btn);
        btn_transac_intent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TransactionActivity.class));
            }
        });

        transac_btn = root.findViewById(R.id.forward_btn);
        transac_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TransactionsHistoryActivity.class));
            }
        });

        Button top_btn = root.findViewById(R.id.btn_top_up);
        top_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TopUpActivity.class));
            }
        });

        init();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        supabaseClient = new SupabaseClient();

        supabaseClient.fetchUserProfile(getContext(), new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                requireActivity().runOnUiThread(()->{
                    name.setText(getString(R.string.error));
                });
            }

            @Override
            public void onResponse(String responseBody) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        Gson gson = new Gson();
                        UserModel[] profiles = gson.fromJson(responseBody, UserModel[].class);

                        if (profiles != null && profiles.length > 0) {
                            UserModel profile = profiles[0];

                            SessionManager sessionManager = new SessionManager(requireContext());
                            sessionManager.setName(profile.getFull_name());

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


                                name.setText(profile.getFull_name());
                                balance.setText(NumberFormatter
                                        .formatBalanceCustom(profile.getBalance()));

                                Glide.with(getContext())
                                        .load(avatarUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(avatar);
                            }
                        } else {
                            requireActivity().runOnUiThread(()->{
                                name.setText(getString(R.string.error));
                            });
                        }
                    } catch (Exception e) {
                    }
                });
            }
        });

        new FetchDataAsync().execute();
    }

    private class FetchDataAsync extends AsyncTask<Void, Void, List<DataItem>> {
        @Override
        protected List<DataItem> doInBackground(Void... voids) {
            try {
                return supabaseClient.fetchTransactions(getContext());
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<DataItem> dataItems) {
            if (dataItems != null) {
                List<DataItem> firstThreeItems = dataItems.subList(0, Math.min(3, dataItems.size()));

                SupabaseClient supabaseClient = new SupabaseClient();
                supabaseClient.fetchProfiles(getContext(), new SupabaseClient.SBC_Callback() {
                    @Override
                    public void onFailure(IOException e) {
                    }

                    @Override
                    public void onResponse(String responseBody) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            try {
                                Gson gson = new Gson();
                                ProfileResponse[] profiles = gson.fromJson(responseBody, ProfileResponse[].class);
                                combinedMoneyAdapter = new CombinedMoneyAdapter(firstThreeItems, getContext(), profiles);
                                recyclerView.setAdapter(combinedMoneyAdapter);
                            } catch (Exception e) {
                            }
                        });
                    }
                });
            } else {
            }
        }
    }
}