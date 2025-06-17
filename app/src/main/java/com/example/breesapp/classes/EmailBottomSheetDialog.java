package com.example.breesapp.classes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.breesapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;

public class EmailBottomSheetDialog extends BottomSheetDialog {

    private static final int PICK_FILE_REQUEST = 1;
    private static final float DIALOG_HEIGHT_PERCENT = 0.8f;

    private EditText etEmail, etSubject, etMessage;
    private TextView tvAttachedFile;
    private Uri attachedFileUri;

    public EmailBottomSheetDialog(@NonNull Fragment fragment) {
        super(fragment.requireContext());
        View view = LayoutInflater.from(fragment.requireContext()).inflate(R.layout.eletter_bottom_sheet_dialog, null);
        setContentView(view);

        setupBottomSheetHeight(fragment.requireActivity());

        etEmail = view.findViewById(R.id.et_email);
        etSubject = view.findViewById(R.id.et_subject);
        etMessage = view.findViewById(R.id.et_message);
        ImageButton btnClose = view.findViewById(R.id.btn_close);
        TextView btnSend = view.findViewById(R.id.btn_send);
        Button btnAttach = view.findViewById(R.id.btn_attach);
        tvAttachedFile = view.findViewById(R.id.tv_attached_file);

        btnClose.setOnClickListener(v -> dismiss());

        btnAttach.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            fragment.startActivityForResult(intent, PICK_FILE_REQUEST);
        });

        btnSend.setOnClickListener(v -> sendEmail(fragment));
    }

    private void setupBottomSheetHeight(Activity activity) {
        View bottomSheet = findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
            int dialogHeight = (int) (screenHeight * DIALOG_HEIGHT_PERCENT);

            ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
            if (params != null) {
                params.height = dialogHeight;
                bottomSheet.setLayoutParams(params);
            }

            com.google.android.material.bottomsheet.BottomSheetBehavior<View> behavior =
                    com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet);
            behavior.setMaxHeight(dialogHeight);
            behavior.setPeekHeight(dialogHeight);
            behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            attachedFileUri = data.getData();
            tvAttachedFile.setText("Файл выбран");
        }
    }

    private void sendEmail(Fragment fragment) {
        String emailTo = etEmail.getText().toString();
        String subject = etSubject.getText().toString();
        String message = etMessage.getText().toString();

        SupabaseClient supabaseClient = new SupabaseClient();

        if (Validator.isValidEmail(emailTo)){
            if (attachedFileUri != null){
                supabaseClient.uploadFileToSupabaseStorage(getContext(), message, emailTo, subject, attachedFileUri, new SupabaseClient.SBC_Callback() {
                    @Override
                    public void onFailure(IOException e) {
                        fragment.getActivity().runOnUiThread(()->{
                            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT);
                        });
                    }

                    @Override
                    public void onResponse(String responseBody) {
                        fragment.getActivity().runOnUiThread(()->{
                            dismiss();
                        });
                    }
                });
            }else{
                supabaseClient.createEletter(getContext(), message, "", emailTo, subject, new SupabaseClient.SBC_Callback() {
                    @Override
                    public void onFailure(IOException e) {
                        fragment.getActivity().runOnUiThread(()->{
                            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT);
                        });
                    }

                    @Override
                    public void onResponse(String responseBody) {
                        fragment.getActivity().runOnUiThread(()->{
                            dismiss();
                        });
                    }
                });
            }
        }else{
            Toast.makeText(getContext(), R.string.email_error, Toast.LENGTH_SHORT).show();
        }
    }
}
