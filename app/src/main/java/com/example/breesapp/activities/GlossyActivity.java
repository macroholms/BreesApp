package com.example.breesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breesapp.R;

public class GlossyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossy);

        TextView title = findViewById(R.id.title_text_glossy);
        TextView content = findViewById(R.id.text_content_glossy);
        Button btn = findViewById(R.id.btn_top_up_cont);

        if (getIntent().getStringExtra("status").equals("topup")){
            title.setText(getString(R.string.top_up_balance));
            content.setText(getString(R.string.successfully_topped_up));
        }else{
            title.setText(getString(R.string.transaction));
            content.setText(getString(R.string.money_transfer));
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}