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

public class PinRegActivity extends AppCompatActivity {

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

        if (isPinSet()){
            init_reg();
        }
    }

    private void init_reg(){
        forgot.setText(getResources().getString(R.string.forgot_pin));

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
        if (!isPinSet() && !confirm){
            pinView.setText("");
            title.setText(getResources().getString(R.string.conf_pin));
            confirm = true;
            confPass = password;
            password = "";
        } else if (!isPinSet() && confirm) {
            if (password.equals(confPass)){
                savePin(password);
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
            if (checkPin(password)){
                toMain();
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

    private void savePin(String pin) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(PIN_KEY, pin)
                .apply();
    }

    private boolean isPinSet() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .contains(PIN_KEY);
    }

    private boolean checkPin(String pin) {
        String savedPin = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(PIN_KEY, "");
        return savedPin.equals(pin);
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
}