package com.example.breesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breesapp.R;
import com.example.breesapp.classes.SessionManager;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.classes.Validator;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

public class TransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        ImageButton back = findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SupabaseClient supabaseClient = new SupabaseClient();

        TextInputEditText email, amount;

        email = findViewById(R.id.EmailTextField1);
        amount = findViewById(R.id.AmountTextField1);

        Button btn = findViewById(R.id.btn_Conf);
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().trim().equals("")){
                    if (Validator.isValidEmail(email.getText().toString().trim())){
                        if (!amount.getText().toString().trim().equals("")){
                            if (!email.getText().toString().trim().equals(sessionManager.getEmail())){
                                btn.setEnabled(false);
                                supabaseClient.transferMoney(getApplicationContext(),
                                        email.getText().toString().trim(),
                                        Float.parseFloat(amount.getText().toString().trim()),
                                        new SupabaseClient.SBC_Callback() {
                                            @Override
                                            public void onFailure(IOException e) {
                                                runOnUiThread(()->{
                                                    btn.setEnabled(true);
                                                    Toast.makeText(getApplicationContext(),
                                                            "Transfer error", Toast.LENGTH_SHORT).show();
                                                });
                                            }

                                            @Override
                                            public void onResponse(String responseBody) {
                                                Intent i = new Intent(new Intent(getApplicationContext(),
                                                        GlossyActivity.class));
                                                i.putExtra("status", "transaction");
                                                startActivity(i);
                                                finish();
                                            }
                                        });
                            }else{
                                Toast.makeText(getApplicationContext(),
                                        R.string.you_can_t_transfer_money_to_yourself, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),
                                    R.string.please_enter_an_amount_money_for_transaction, Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),
                                R.string.email_error, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            R.string.please_enter_an_email_of_recipient, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}