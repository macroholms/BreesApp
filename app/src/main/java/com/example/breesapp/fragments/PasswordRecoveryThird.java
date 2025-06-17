package com.example.breesapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.breesapp.R;
import com.example.breesapp.activities.PasswordRecoveryActivity;
import com.example.breesapp.classes.SupabaseClient;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordRecoveryThird#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordRecoveryThird extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SupabaseClient supabaseClient;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PasswordRecoveryThird() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasswordRecoveryThird.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordRecoveryThird newInstance(String param1, String param2) {
        PasswordRecoveryThird fragment = new PasswordRecoveryThird();
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
        View view = inflater.inflate(R.layout.fragment_password_recovery_third, container, false);

        supabaseClient = new SupabaseClient();

        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey("email")) {
            Toast.makeText(getContext(), "Invalid arguments", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return view;
        }

        final String email = arguments.getString("email");

        TextView text = view.findViewById(R.id.recovery_text);
        text.setText(text.getText().toString() + email);

        supabaseClient.sendPasswordResetOtp(email, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
            }

            @Override
            public void onResponse(String responseBody) {
            }
        });

        Button resend = view.findViewById(R.id.resend_btn);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supabaseClient.sendPasswordResetOtp(email, new SupabaseClient.SBC_Callback() {
                    @Override
                    public void onFailure(IOException e) {
                    }

                    @Override
                    public void onResponse(String responseBody) {
                    }
                });
            }
        });

        PinView otp = view.findViewById(R.id.recoveryPinView);
        otp.setCursorVisible(true);

        Button btn = view.findViewById(R.id.recovery_third_btn);
        btn.setOnClickListener(v -> {
            if (!otp.getText().equals("")) {
                verifyOtp(email, otp.getText().toString());
            } else {
                Toast.makeText(getContext(), "Введите OTP код", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void verifyOtp(String email, String otp) {
        supabaseClient.verifyPasswordResetOtp(getContext(),email, otp, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
            }

            @Override
            public void onResponse(String responseBody) {
                if (getActivity() instanceof PasswordRecoveryActivity) {
                    PasswordRecoveryFourth passwordRecoveryFourth = new PasswordRecoveryFourth();
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email);
                    passwordRecoveryFourth.setArguments(bundle);
                    ((PasswordRecoveryActivity) getActivity()).loadFragment(passwordRecoveryFourth);
                }
            }
        });
    }
}