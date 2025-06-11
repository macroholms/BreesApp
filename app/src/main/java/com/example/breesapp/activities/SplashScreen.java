package com.example.breesapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.breesapp.R;
import com.example.breesapp.models.DataBinding;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        DataBinding.init(getApplicationContext());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= null;
                if (DataBinding.getStatus() == null){
                    intent = new Intent(SplashScreen.this,
                            OnBoardingActivity.class);
                }else if (DataBinding.getStatus().equals("logined")) {
                    intent = new Intent(SplashScreen.this,
                            PinRegActivity.class);
                }
                else if(DataBinding.getStatus().equals("bookAll") ||
                        DataBinding.getStatus().equals("unlogined")) {
                    intent = new Intent(SplashScreen.this,
                            LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}