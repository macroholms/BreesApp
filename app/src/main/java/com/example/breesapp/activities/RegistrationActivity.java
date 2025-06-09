package com.example.breesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breesapp.R;
import com.example.breesapp.classes.SupabaseAuth;
import com.example.breesapp.classes.SupabaseHands;
import com.example.breesapp.classes.Validator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import okhttp3.Response;

public class RegistrationActivity extends AppCompatActivity {

    Button RegBtn, LoginBtn;
    TextInputEditText Name, Email, Password, ConfPassword;
    CheckBox Terms;
    TextInputLayout Pass;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String USER_NAME_KEY = "user_name";
    private static final String USER_EMAIL_KEY = "user_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        RegBtn = findViewById(R.id.reg_btn);
        LoginBtn = findViewById(R.id.login_btn);

        Name = findViewById(R.id.NameTextField1);
        Email = findViewById(R.id.EmailTextField1);
        Password = findViewById(R.id.PasswordTextField1);
        Pass = findViewById(R.id.PasswordTextField);
        ConfPassword = findViewById(R.id.ConfPasswordTextField1);
        Terms = findViewById(R.id.check_terms);

        RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registration();
            }
        });
    }

    private void Registration(){
        boolean empty_cond = Validator.isNotEmpty(Name.getText().toString()) &&
                Validator.isNotEmpty(Email.getText().toString()) &&
                Validator.isNotEmpty(Password.getText().toString()) &&
                Validator.isNotEmpty(ConfPassword.getText().toString());
        if (empty_cond){
            if (Terms.isChecked()){
                if (Validator.isValidLength(Password.getText().toString())){
                    if (Validator.doPasswordsMatch(Password.getText().toString(),
                            ConfPassword.getText().toString())){
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                                .edit()
                                .putString(USER_NAME_KEY, Name.getText().toString())
                                .putString(USER_EMAIL_KEY, Email.getText().toString())
                                .apply();
                        new Thread(()->{
                            SupabaseAuth auth = new SupabaseAuth(getApplicationContext());
                            try {
                                Response response = auth.signUp(Email.getText().toString(), Password.getText().toString());
                                Log.e("!!!!!!!!!!!!!", String.valueOf(response.code()));
                                Log.e("!!!!!!!!!!!!!", response.body().toString());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            SupabaseHands hands = new SupabaseHands();
                            try {
                                hands.updateProfiles(getApplicationContext(), Name.getText().toString());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            runOnUiThread(()->{
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            });
                        });
                    }
                    else{
                        Pass.setHelperTextEnabled(true);
                        Pass.setError(getResources().getString(R.string.password_match_error));
                    }
                }
                else{
                    Pass.setHelperTextEnabled(true);
                    Pass.setError(getResources().getString(R.string.password_lenght_error));
                }
            }
            else {
                Toast.makeText(this, getResources().getString(R.string.terms_error),
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, getResources().getString(R.string.empty_error),
                    Toast.LENGTH_SHORT).show();
        }
    }
}