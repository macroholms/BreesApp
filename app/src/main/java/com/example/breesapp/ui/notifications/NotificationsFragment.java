package com.example.breesapp.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.breesapp.R;
import com.example.breesapp.activities.MainActivity;
import com.example.breesapp.adapters.DrawerAdapter;
import com.example.breesapp.adapters.EmailAdapter;
import com.example.breesapp.classes.DrawerItem;
import com.example.breesapp.classes.EmailBottomSheetDialog;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.databinding.FragmentNotificationsBinding;
import com.example.breesapp.models.EmailItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsFragment extends Fragment {

    private DrawerLayout drawerLayout;
    private RecyclerView drawerRecyclerView, emailsRv;
    private DrawerAdapter drawerAdapter;
    private List<DrawerItem> drawerItems;
    private List<EmailItem> emailItems = new ArrayList<>();
    private EmailAdapter adapter;
    SupabaseClient supabaseClient = new SupabaseClient();
    private SwipeRefreshLayout swipeRefreshLayout;

    private EmailBottomSheetDialog emailBottomSheetDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        emailsRv = root.findViewById(R.id.eletter_rv);

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::refreshEmails);

        email_init();

        ImageButton emailBSD = root.findViewById(R.id.elleter_write_btn);
        emailBSD.setOnClickListener(v -> {
            emailBottomSheetDialog = new EmailBottomSheetDialog(this);
            emailBottomSheetDialog.show();
        });

        drawerLayout = root.findViewById(R.id.drawer_layout);
        drawerRecyclerView = root.findViewById(R.id.drawer_recycler);

        drawerItems = new ArrayList<>();
        drawerItems.add(new DrawerItem(getString(R.string.inbox), R.drawable.inbox_ic));
        drawerItems.add(new DrawerItem(getString(R.string.sent), R.drawable.sent_ic));
        drawerItems.add(new DrawerItem(getString(R.string.starred), R.drawable.star_ic));

        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        drawerAdapter = new DrawerAdapter(drawerItems, position -> {
            DrawerItem selectedItem = drawerItems.get(position);
            Toast.makeText(requireContext(), "Выбрано: " + selectedItem.getTitle(), Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawers();
        });
        drawerRecyclerView.setAdapter(drawerAdapter);

        root.findViewById(R.id.toggle_drawer).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (emailBottomSheetDialog != null) {
            emailBottomSheetDialog.handleActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());

    public static EmailItem parseEmail(JSONObject json) {
        try {
            int id = json.getInt("id");

            Date createdAt;
            try {
                createdAt = sdf.parse(json.getString("created_at"));
            } catch (ParseException e) {
                createdAt = new Date(0);
            }

            String senderFullName = json.optString("sender_full_name", "");
            String senderAvatarUrl = json.optString("sender_avatar_url", "");
            String recipientFullName = json.optString("recipient_full_name", "");
            String recipientAvatarUrl = json.optString("recipient_avatar_url", "");
            String theme = json.optString("theme", "");
            String content = json.optString("content", "");

            boolean senderVisible = json.optBoolean("sender_visibility", false);
            boolean recipientVisible = json.optBoolean("recipient_visibility", false);

            if (!recipientVisible){
                return null;
            }

            Integer groupID = null;
            if (!json.isNull("groupid")) {
                groupID = json.getInt("groupid");
            }

            boolean readed = json.optBoolean("readed", false);
            Object fileUrl = json.opt("file_url");

            return new EmailItem(
                    id,
                    createdAt,
                    senderFullName,
                    senderAvatarUrl,
                    recipientFullName,
                    recipientAvatarUrl,
                    theme,
                    content,
                    senderVisible,
                    recipientVisible,
                    groupID,
                    readed,
                    fileUrl
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void email_init(){
        supabaseClient.getReceivedMails(getContext(), new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                getActivity().runOnUiThread(()->{
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                try{
                    JSONArray array = new JSONArray(responseBody);
                    for (int i = 0; i < array.length(); i++){
                        JSONObject jsonObject = array.getJSONObject(i);
                        if (parseEmail(jsonObject) != null){
                            emailItems.add(parseEmail(jsonObject));
                        }
                    }
                    adapter = new EmailAdapter(emailItems, new EmailAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(EmailItem item) {

                        }

                        @Override
                        public void onDeleteClick(EmailItem item) {
                            supabaseClient.updateRecipientVisibility(getContext(),
                                    String.valueOf(item.getId()), new SupabaseClient.SBC_Callback() {
                                @Override
                                public void onFailure(IOException e) {
                                    getActivity().runOnUiThread(()->{
                                        Toast.makeText(getContext(), "ERRor", Toast.LENGTH_SHORT).show();
                                    });
                                }

                                @Override
                                public void onResponse(String responseBody) {
                                    getActivity().runOnUiThread(()->{
                                        refreshEmails();
                                    });
                                }
                            });
                        }

                        @Override
                        public void onAddToGroupClick(EmailItem item) {

                        }
                    });
                    getActivity().runOnUiThread(()->{
                        emailsRv.setAdapter(adapter);
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void refreshEmails() {
        emailItems.clear();
        adapter.notifyDataSetChanged();

        supabaseClient.getReceivedMails(getContext(), new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
            }

            @Override
            public void onResponse(String responseBody) {
                try {
                    JSONArray array = new JSONArray(responseBody);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        if (parseEmail(jsonObject) != null){
                            emailItems.add(parseEmail(jsonObject));
                        }
                    }

                    getActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    });

                } catch (JSONException e) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Ошибка парсинга", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }
        });
    }
}