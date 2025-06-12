package com.example.breesapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breesapp.R;

public class FAQ_Element_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_element);

        ImageButton btn = findViewById(R.id.back_btn);

        TextView textView = findViewById(R.id.topic_title);
        TextView textView1 = findViewById(R.id.topic_content);

        textView.setText(getIntent().getStringExtra("tittle"));
        textView1.setText(getIntent().getStringExtra("content"));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}