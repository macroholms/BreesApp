package com.example.breesapp.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.breesapp.R;
import com.example.breesapp.classes.ProfileDataFetcher;
import com.example.breesapp.classes.SessionManager;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.classes.UpdateUserRequest;
import com.example.breesapp.classes.Validator;
import com.example.breesapp.models.DataBinding;
import com.example.breesapp.models.ProfileUpdate;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.IOException;

public class MyAccountActivity extends AppCompatActivity {

    String email, name;
    TextInputEditText nameInput, emailInput;
    Uri selectedImageUri;
    ImageView imageView;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        nameInput = findViewById(R.id.NameTextField1);
        emailInput = findViewById(R.id.EmailTextField1);
        imageView = findViewById(R.id.avatar_placeholder);

        ProfileDataFetcher profileDataFetcher = new ProfileDataFetcher();
        profileDataFetcher.loadUserProfile(getApplicationContext(),
                nameInput, emailInput, imageView);

        SupabaseClient supabaseClient = new SupabaseClient();
        ImageButton btn = findViewById(R.id.back_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        ImageButton btn_dialog = findViewById(R.id.btn_dialog);

        btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        Button saveBtn = findViewById(R.id.SaveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nameInput.getText().toString().trim().equals(name)){
                    supabaseClient.updateProfile(getApplicationContext(),
                            nameInput.getText().toString().trim(),
                            new SupabaseClient.SBC_Callback() {
                        @Override
                        public void onFailure(IOException e) {
                        }

                        @Override
                        public void onResponse(String responseBody) {
                        }
                    });
                }
                if (!emailInput.getText().toString().trim().equals(email)){
                    if(Validator.isValidEmail(emailInput.getText().toString().trim())){
                        supabaseClient.changeEmail(getApplicationContext(),
                                emailInput.getText().toString().trim(),
                                new SupabaseClient.SBC_Callback() {
                                    @Override
                                    public void onFailure(IOException e) {
                                        Log.e("!!!", e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(String responseBody) {
                                        Log.e("!!!", responseBody);
                                    }
                                });
                    }
                    else{
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.email_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                if (selectedImageUri != null) {
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    String fileName = "profile_" + sessionManager.getUserId() + ".png";
                    supabaseClient.uploadAvatar(getApplicationContext(), selectedImageUri, fileName, new SupabaseClient.SBC_Callback() {
                        @Override
                        public void onFailure(IOException e) {
                            Log.e("UploadError", "Ошибка загрузки", e);
                            runOnUiThread(() ->
                                    Toast.makeText(getApplicationContext(),
                                            "Ошибка загрузки фото: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show()
                            );
                        }

                        @Override
                        public void onResponse(String responseBody) {
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "Фото загружено"
                                        , Toast.LENGTH_SHORT).show();


                                supabaseClient.updateFileUrl(getApplicationContext(), fileName, new SupabaseClient.SBC_Callback() {
                                    @Override
                                    public void onFailure(IOException e) {
                                        Log.e("AvatarUpdate", "Ошибка обновления ссылки на фото", e);
                                        Toast.makeText(getApplicationContext(), "Не удалось обновить ссылку на фото", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(String responseBody) {
                                        Toast.makeText(getApplicationContext(), "Ссылка на фото обновлена", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        }
                    });
                }

                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            imageView.setImageURI(selectedImageUri);
        }
    }

    public String getRealPathFromURI(Uri contentUri, Context context) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}