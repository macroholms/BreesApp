package com.example.breesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.breesapp.R;

public class onBoardingThird extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_third);
    }

    public void click_btn(View view){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
