package com.example.breesapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.breesapp.R;
import com.example.breesapp.models.EmailItem;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EmailItemViewActivity extends AppCompatActivity {

    private TextView tvCreatedAt, tvSenderName, tvTheme, tvContent;
    private ImageView ivSenderAvatar;
    private Button btnDownloadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_item_view);

        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvSenderName = findViewById(R.id.tvSenderName);
        tvTheme = findViewById(R.id.tvTheme);
        tvContent = findViewById(R.id.tvContent);
        ivSenderAvatar = findViewById(R.id.ivSenderAvatar);
        btnDownloadFile = findViewById(R.id.btnDownloadFile);
        ImageButton back_btn = findViewById(R.id.back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("email_item")) {
            EmailItem email = (EmailItem) intent.getSerializableExtra("email_item");

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            tvCreatedAt.setText(sdf.format(email.getCreatedAt()));

            tvSenderName.setText(email.getSenderFullName());

            String avatarUrl = email.getSenderAvatarUrl();

            Glide.with(this)
                    .load("https://nrwxeidhsclzffyojfvq.supabase.co/storage/v1/object/public/avatars//"
                            + avatarUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivSenderAvatar);


            tvTheme.setText(email.getTheme());
            tvContent.setText(email.getContent());

            if (!email.getFileUrl().toString().contains("{}")) {
                btnDownloadFile.setVisibility(android.view.View.VISIBLE);
                String fileUrlObj = (String) email.getFileUrl();
                btnDownloadFile.setOnClickListener(v -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrlObj));
                    startActivity(browserIntent);
                });
            } else {
                btnDownloadFile.setVisibility(android.view.View.GONE);
            }
        } else {
            finish();
        }
    }
}