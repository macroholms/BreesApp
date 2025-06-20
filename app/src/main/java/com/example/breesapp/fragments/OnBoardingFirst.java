package com.example.breesapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.breesapp.R;
import com.example.breesapp.activities.OnBoardingActivity;
import com.example.breesapp.models.DataBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnBoardingFirst#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnBoardingFirst extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OnBoardingFirst() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnBoardingFirst.
     */
    // TODO: Rename and change types and number of parameters
    public static OnBoardingFirst newInstance(String param1, String param2) {
        OnBoardingFirst fragment = new OnBoardingFirst();
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
        View view = inflater.inflate(R.layout.fragment_on_boarding_first, container, false);

        Button btnNext = view.findViewById(R.id.btn_next_f);
        Button btnSkip = view.findViewById(R.id.btn_skip_f);

        btnNext.setOnClickListener(v -> {
            if (getActivity() instanceof OnBoardingActivity) {
                ((OnBoardingActivity) getActivity()).loadFragment(new OnBoardingSecond());
            }
        });

        btnSkip.setOnClickListener(v -> {
            DataBinding.start();
            if (getActivity() instanceof OnBoardingActivity) {
                ((OnBoardingActivity) getActivity()).goToRegistration();
            }
        });

        return view;
    }
}