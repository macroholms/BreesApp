package com.example.breesapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.breesapp.R;
import com.example.breesapp.databinding.OnboardFirstBinding;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= null;
                if (prefs.getAll().isEmpty()){
                    intent = new Intent(SplashScreen.this,
                            onBoardingFirst.class);
                }
/*                else if(prefs.getAll().get("firstStart").equals("Login")) {
                    intent = new Intent(SplashScreen.this,
                            Login.class);
                }*/
                else if (prefs.getAll().get("firstStart").equals("Authorized")) {
                    intent = new Intent(SplashScreen.this,
                            MainActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}