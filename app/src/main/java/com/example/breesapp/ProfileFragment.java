package com.example.breesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.breesapp.activities.FAQActivity;
import com.example.breesapp.activities.MyAccountActivity;
import com.example.breesapp.activities.SettingsActivity;
import com.example.breesapp.classes.ProfileDataFetcher;
import com.example.breesapp.classes.SessionManager;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.models.ProfileResponse;
import com.google.gson.Gson;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView name_holder, email_holder;
    ImageView avatar;
    ProfileDataFetcher profileDataFetcher = new ProfileDataFetcher();

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button my_account_btn = view.findViewById(R.id.my_account_btn);

        name_holder = view.findViewById(R.id.name_placeholder);
        email_holder = view.findViewById(R.id.email_placeholder);
        avatar = view.findViewById(R.id.avatar_placeholder);

        profileDataFetcher.loadUserProfile(getContext(), name_holder, email_holder, avatar);

        my_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyAccountActivity.class));
            }
        });

        Button btn2 = view.findViewById(R.id.settings_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }
        });

        Button btn3 = view.findViewById(R.id.help_center_btn);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FAQActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        profileDataFetcher.loadUserProfile(getContext(), name_holder, email_holder, avatar);
    }
}