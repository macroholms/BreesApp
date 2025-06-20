package com.example.breesapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaos.view.PinView;
import com.example.breesapp.R;
import com.example.breesapp.classes.SessionManager;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.models.AuthResponse;
import com.example.breesapp.models.LogRegRequest;
import com.google.gson.Gson;

import java.io.IOException;

public class PinRegActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView title;
    private Button forgot;
    private String password = "";
    private String confPass = "";
    private PinView pinView, pinView2;
    private boolean confirm = false;
    private static final String PREFS_NAME = "PinPrefs";
    private static final String PIN_KEY = "user_pin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_reg);
        sessionManager = new SessionManager(this);
        title = findViewById(R.id.titleText);

        title.setText(getString(R.string.pin_en));
        forgot = findViewById(R.id.forg_btn);
        pinView = findViewById(R.id.pinView);
        pinView2 = findViewById(R.id.pinViewBack);
        pinView.setTextColor(
                ResourcesCompat.getColor(getResources(), R.color.main, getTheme()));
        pinView2.setTextColor(
                ResourcesCompat.getColor(getResources(), R.color.gray, getTheme()));
        pinView.setCursorVisible(false);
        pinView.setLineColor(
                ResourcesCompat.getColor(getResources(), R.color.transperent, getTheme()));
        pinView2.setLineColor(
                ResourcesCompat.getColor(getResources(), R.color.transperent, getTheme()));

        if (sessionManager.isPinSet()){
            init_reg();
        }
    }

    private void init_reg(){
        forgot.setText(getResources().getString(R.string.forgot_pin));

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    public void number_click(View view){
        Button tmp = (Button)view;
        password += tmp.getText().toString();
        pinView.setText(password);
        if (password.length() == 4){
            check_pin();
        }
    }

    public void backspace_click(View view){
        if (password.length() == 1){
            password = "";
            pinView.setText(password);
        }else if (password.length() > 0){
            password = password.substring(password.length() - 1, password.length());
            pinView.setText(password);
        }
    }

    public void check_pin(){
        if (!sessionManager.isPinSet() && !confirm){
            pinView.setText("");
            title.setText(getResources().getString(R.string.conf_pin));
            confirm = true;
            confPass = password;
            password = "";
        } else if (!sessionManager.isPinSet() && confirm) {
            if (password.equals(confPass)){
                sessionManager.savePin(password);
                toMain();
            }
            else{
                shakeView();
                password = "";
                confirm = false;
                confPass = "";
                pinView.setText(password);
                title.setText(getString(R.string.pin_en));
            }
        }
        else{
            if (sessionManager.getPin().equals(password)){
                SupabaseClient supabaseClient = new SupabaseClient();
                LogRegRequest request = new LogRegRequest(
                        sessionManager.getEmail(),
                        sessionManager.getPassword()
                );
                logIN(supabaseClient, request);
            }
            else{
                shakeView();
                password = "";
                confirm = false;
                confPass = "";
                pinView.setText(password);
            }
        }
    }

    private void toMain(){
        pinView.setTextColor(
                ResourcesCompat.getColor(getResources(), R.color.green, getTheme()));
        pinView2.setTextColor(
                ResourcesCompat.getColor(getResources(), R.color.green, getTheme()));

        new Handler().postDelayed(() -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }, 100);
    }

    private void shakeView() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        pinView.startAnimation(shake);
        pinView2.startAnimation(shake);

        pinView.setTextColor(
                ResourcesCompat.getColor(getResources(), R.color.red, getTheme()));
        pinView2.setTextColor(
                ResourcesCompat.getColor(getResources(), R.color.red, getTheme()));
        new Handler().postDelayed(() -> {
            pinView.setTextColor(
                    ResourcesCompat.getColor(getResources(), R.color.main, getTheme()));
            pinView2.setTextColor(
                    ResourcesCompat.getColor(getResources(), R.color.gray, getTheme()));
        }, 250);
    }

    public void logIN(SupabaseClient supabaseClient, LogRegRequest request){
        supabaseClient.login(request, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                logIN(supabaseClient, request);
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(()->{
                    Gson gson = new Gson();
                    AuthResponse auth = gson.fromJson(responseBody, AuthResponse.class);

                    if (auth == null || auth.getAccess_token() == null) {
                        return;
                    }

                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.setBearer(auth.getAccess_token());
                    toMain();
                });
            }
        });
    }
}