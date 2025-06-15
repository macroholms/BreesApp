package com.example.breesapp.activities;

import android.content.Intent;
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

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TransacDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transac_details);

        TextView title, amount, date, time;

        title = findViewById(R.id.title_transac_details);
        amount = findViewById(R.id.amountInp);
        date = findViewById(R.id.dateInp);
        time = findViewById(R.id.timeInp);

        Intent i = getIntent();
        if(i.getStringExtra("status").equals("topup")){
            title.setText(i.getStringExtra("name"));
        }else{
            title.setText(getString(R.string.top_up_balance));
        }

        amount.setText(i.getStringExtra("amount"));

        OffsetDateTime odt = OffsetDateTime.parse(i.getStringExtra("date"));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM, yyyy",
                Locale.getDefault());

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        date.setText(odt.format(dateFormatter));
        time.setText(odt.format(timeFormatter));

        ImageButton back_btn = findViewById(R.id.back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}