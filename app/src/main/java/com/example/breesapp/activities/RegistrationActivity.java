package com.example.breesapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.breesapp.R;
import com.example.breesapp.classes.SessionManager;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.classes.Validator;
import com.example.breesapp.models.AuthResponse;
import com.example.breesapp.models.DataBinding;
import com.example.breesapp.models.LogRegRequest;
import com.example.breesapp.models.ProfileUpdate;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    Button RegBtn, LoginBtn;
    TextInputEditText Name, Email, Password, ConfPassword;
    CheckBox Terms;
    TextInputLayout Pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        RegBtn = findViewById(R.id.reg_btn);
        LoginBtn = findViewById(R.id.login_btn);

        DataBinding.init(getApplicationContext());

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

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        LoginActivity.class));
                finish();
            }
        });
    }

    private void Registration(){
        boolean empty_cond = Validator.isNotEmpty(Name.getText().toString()) &&
                Validator.isNotEmpty(Email.getText().toString()) &&
                Validator.isNotEmpty(Password.getText().toString()) &&
                Validator.isNotEmpty(ConfPassword.getText().toString());
        if (empty_cond){
            if (Validator.isValidEmail(Email.getText().toString())){
                if (Validator.isValidLength(Password.getText().toString())){
                    if (Validator.doPasswordsMatch(Password.getText().toString(),
                    ConfPassword.getText().toString())){
                        if (Terms.isChecked()){
                                String name = Name.getText().toString().trim();
                                String email = Email.getText().toString().trim();
                                String password = Password.getText().toString().trim();
                                SupabaseClient supabaseClient = new SupabaseClient();
                                LogRegRequest loginRequest = new LogRegRequest(email, password);
                                SessionManager sessionManager = new SessionManager(getApplicationContext());
                                sessionManager.setEmail(email);
                                sessionManager.setName(name);

                                supabaseClient.registr(loginRequest, new SupabaseClient.SBC_Callback() {
                                    @Override
                                    public void onFailure(IOException e) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(RegistrationActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                                            Log.e("ERROR", e.getMessage().toString());
                                        });
                                    }

                                    @Override
                                    public void onResponse(String responseBody) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(RegistrationActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                                            Log.d("registr:onResponse", responseBody);

                                            Gson gson = new Gson();
                                            AuthResponse auth = gson.fromJson(responseBody, AuthResponse.class);

                                            if (auth == null || auth.getAccess_token() == null) {
                                                Toast.makeText(RegistrationActivity.this, "Не удалось получить токен", Toast.LENGTH_LONG).show();
                                                return;
                                            }

                                            sessionManager.setBearer("Bearer " + auth.getAccess_token());
                                            sessionManager.setUserId(auth.getUser().getId());

                                            supabaseClient.updateProfile(getApplicationContext(), name, new SupabaseClient.SBC_Callback() {
                                                @Override
                                                public void onFailure(IOException e) {
                                                    runOnUiThread(() -> {
                                                        Log.e("updateProfile:onFailure", e.getLocalizedMessage());
                                                        Toast.makeText(RegistrationActivity.this, "Ошибка обновления профиля", Toast.LENGTH_SHORT).show();
                                                    });
                                                }

                                                @Override
                                                public void onResponse(String response) {
                                                    runOnUiThread(() -> {
                                                        Log.d("updateProfile:onResponse", response);
                                                        Toast.makeText(RegistrationActivity.this, "Профиль обновлён", Toast.LENGTH_SHORT).show();
                                                        DataBinding.logined();
                                                        SharedPreferences sharedPref = getSharedPreferences("user_session", MODE_PRIVATE);
                                                        if (sharedPref.contains("pin")){
                                                            sharedPref.edit().remove("pin").apply();
                                                        }
                                                        startActivity(new Intent(RegistrationActivity.this, PinRegActivity.class));
                                                        finish();
                                                    });
                                                }
                                            });
                                        });
                                    }
                                });
                            }else{
                            Toast.makeText(this, getResources().getString(R.string.terms_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                        }
                        else{
                            Pass.setHelperTextEnabled(true);
                            Pass.setError(getResources().getString(R.string.password_match_error));
                        }
                    }
                    else {
                        Pass.setHelperTextEnabled(true);
                        Pass.setError(getResources().getString(R.string.password_lenght_error));

                    }
                }
                else{
                Toast.makeText(this, getResources().getString(R.string.email_error),
                        Toast.LENGTH_SHORT).show();
                }
            }
        else{
            Toast.makeText(this, getResources().getString(R.string.empty_error),
                    Toast.LENGTH_SHORT).show();
        }
    }
}