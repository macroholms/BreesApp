package com.example.breesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breesapp.R;
import com.example.breesapp.classes.SupabaseClient;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

public class TopUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        TextInputEditText amountTextField = findViewById(R.id.AmountTextField1);

        ImageButton back = findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button conf = findViewById(R.id.btn_top_up_conf);
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = amountTextField.getText().toString();

                float value = 0.0f;

                if (!text.isEmpty()) {
                    try {
                        conf.setEnabled(false);
                        value = Float.parseFloat(text);
                        SupabaseClient supabaseClient = new SupabaseClient();
                        supabaseClient.increaseBalance(getApplicationContext(), value,
                                new SupabaseClient.SBC_Callback() {
                            @Override
                            public void onFailure(IOException e) {
                                runOnUiThread(()->{
                                    conf.setEnabled(true);
                                });
                            }

                            @Override
                            public void onResponse(String responseBody) {
                                Intent i = new Intent(getApplicationContext(),
                                        GlossyActivity.class);
                                i.putExtra("status", "topup");
                                startActivity(i);
                                finish();
                            }
                        });
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}