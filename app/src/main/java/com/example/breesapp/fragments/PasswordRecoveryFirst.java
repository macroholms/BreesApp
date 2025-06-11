package com.example.breesapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.breesapp.R;
import com.example.breesapp.activities.OnBoardingActivity;
import com.example.breesapp.activities.PasswordRecoveryActivity;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordRecoveryFirst#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordRecoveryFirst extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PasswordRecoveryFirst() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasswordRecoveryFirst.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordRecoveryFirst newInstance(String param1, String param2) {
        PasswordRecoveryFirst fragment = new PasswordRecoveryFirst();
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
        View view = inflater.inflate(R.layout.fragment_password_recovery_first, container, false);

        ImageButton back_btn = view.findViewById(R.id.back_btn);
        TextInputEditText email = view.findViewById(R.id.emailInput);
        Button btn = view.findViewById(R.id.recovery_first_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof PasswordRecoveryActivity) {
                    ((PasswordRecoveryActivity) getActivity()).goToLogin();
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof PasswordRecoveryActivity) {
                    PasswordRecoverySecond passwordRecoverySecond = new PasswordRecoverySecond();
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email.getText().toString().trim());
                    passwordRecoverySecond.setArguments(bundle);
                    ((PasswordRecoveryActivity) getActivity()).loadFragment(passwordRecoverySecond);
                }
            }
        });

        return view;
    }
}