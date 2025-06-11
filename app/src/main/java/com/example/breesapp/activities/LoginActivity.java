package com.example.breesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breesapp.R;
import com.example.breesapp.classes.SessionManager;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.models.AuthResponse;
import com.example.breesapp.models.DataBinding;
import com.example.breesapp.models.LogRegRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    Button regBtn, loginBtn, forgotBtn;
    TextInputEditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        regBtn = findViewById(R.id.reg_btn);
        loginBtn = findViewById(R.id.login_btn);
        forgotBtn = findViewById(R.id.forgot_btn);
        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        RegistrationActivity.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(email.getText().toString().trim(),
                        password.getText().toString().trim());
            }
        });

        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        PasswordRecoveryActivity.class));
                finish();
            }
        });
    }

    private void login(String email,String password){
        SupabaseClient supabaseClient = new SupabaseClient();
        LogRegRequest request = new LogRegRequest(email, password);

        supabaseClient.login(request, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Ошибка входа: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Gson gson = new Gson();
                    AuthResponse auth = gson.fromJson(responseBody, AuthResponse.class);

                    if (auth == null || auth.getAccess_token() == null) {
                        Toast.makeText(LoginActivity.this, "Ошибка: не удалось получить токен", Toast.LENGTH_LONG).show();
                        return;
                    }

                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.createLoginSession(
                            email,
                            password,
                            auth.getAccess_token(),
                            auth.getUser().getId()
                    );
                    DataBinding.logined();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                });
            }
        });
    }
}