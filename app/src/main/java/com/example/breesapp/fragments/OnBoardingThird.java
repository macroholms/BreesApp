package com.example.breesapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.breesapp.R;
import com.example.breesapp.activities.OnBoardingActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnBoardingThird#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnBoardingThird extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OnBoardingThird() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnBoardingThird.
     */
    // TODO: Rename and change types and number of parameters
    public static OnBoardingThird newInstance(String param1, String param2) {
        OnBoardingThird fragment = new OnBoardingThird();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_boarding_third, container, false);

        Button btnGetStarted = view.findViewById(R.id.btn_get_started);

        btnGetStarted.setOnClickListener(v -> {
            if (getActivity() instanceof OnBoardingActivity) {
                ((OnBoardingActivity) getActivity()).goToRegistration();
            }
        });

        return view;
    }
}