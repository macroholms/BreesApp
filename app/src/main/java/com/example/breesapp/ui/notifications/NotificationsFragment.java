package com.example.breesapp.ui.notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
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
import com.example.breesapp.activities.EmailItemViewActivity;
import com.example.breesapp.activities.MainActivity;
import com.example.breesapp.adapters.DrawerAdapter;
import com.example.breesapp.adapters.EmailAdapter;
import com.example.breesapp.classes.BaseDrawerItem;
import com.example.breesapp.classes.DrawerGroupItem;
import com.example.breesapp.classes.DrawerMenuItem;
import com.example.breesapp.classes.EmailBottomSheetDialog;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.databinding.FragmentNotificationsBinding;
import com.example.breesapp.models.ElettersGroup;
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

    private TextView groupTittle;
    private DrawerLayout drawerLayout;
    private RecyclerView drawerRecyclerView, emailsRv;
    private DrawerAdapter drawerAdapter;
    private List<BaseDrawerItem> drawerItems = new ArrayList<>();
    private List<EmailItem> filtered = new ArrayList<>();
    private List<EmailItem> emailItems = new ArrayList<>();
    private List<EmailItem> sentEmailsItems = new ArrayList<>();
    private EmailAdapter adapter;
    SupabaseClient supabaseClient = new SupabaseClient();
    private SwipeRefreshLayout swipeRefreshLayout;
    private EmailBottomSheetDialog emailBottomSheetDialog;
    private List<ElettersGroup> groupList = new ArrayList<>();

    private void loadGroups() {

        drawerItems.clear();

        drawerItems.add(new DrawerMenuItem(getString(R.string.inbox), R.drawable.inbox_ic));
        drawerItems.add(new DrawerMenuItem(getString(R.string.sent), R.drawable.sent_ic));
        drawerItems.add(new DrawerMenuItem(getString(R.string.starred), R.drawable.star_ic));

        supabaseClient.fetchGroupsByUserId(getContext(), new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                if (isAdded() && getActivity() != null && !getActivity().isFinishing()) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), R.string.error_group_load, Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(String responseBody) {
                try {

                    JSONArray array = new JSONArray(responseBody);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        ElettersGroup group = new ElettersGroup(
                                obj.getInt("id"),
                                obj.getString("tittle"),
                                obj.getString("color"),
                                obj.getString("user_id")
                        );
                        groupList.add(group);
                        drawerItems.add(new DrawerGroupItem(group));
                    }
                    Log.d("GROUPS", "Loaded groups count: " + groupList.size());
                    Log.d("DRAWER_ITEMS", "Total items in drawer: " + drawerItems.size());
                    if (isAdded() && !isDetached()) {
                        requireActivity().runOnUiThread(() -> {
                            drawerAdapter.notifyDataSetChanged();
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        getSentEmails();
        emailsRv = root.findViewById(R.id.eletter_rv);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::refreshEmails);
        email_init();
        groupTittle = root.findViewById(R.id.groupTitle);

        ImageButton emailBSD = root.findViewById(R.id.elleter_write_btn);
        emailBSD.setOnClickListener(v -> {
            emailBottomSheetDialog = new EmailBottomSheetDialog(this);
            emailBottomSheetDialog.show();
        });

        EditText editText = root.findViewById(R.id.search_ed);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        drawerLayout = root.findViewById(R.id.drawer_layout);
        drawerRecyclerView = root.findViewById(R.id.drawer_recycler);

        drawerLayout.findViewById(R.id.btn_add_group).setOnClickListener(v -> showCreateGroupDialog());
        drawerLayout.findViewById(R.id.btn_close_drawer).setOnClickListener(v -> drawerLayout.closeDrawers());

        loadGroups();

        drawerAdapter = new DrawerAdapter(
                drawerItems,
                new DrawerAdapter.OnMenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(DrawerMenuItem item) {
                        if (adapter != null){
                        if (item.getTitle().equals("Inbox")) {
                            groupTittle.setText("Inbox");
                            adapter.setData(emailItems);
                            adapter.notifyDataSetChanged();
                        }
                        else if (item.getTitle().equals("Sent")) {
                            groupTittle.setText("Sent");
                            adapter.setData(sentEmailsItems);
                            adapter.notifyDataSetChanged();
                        }
                        else if (item.getTitle().equals("Starred")) {
                            filtered.clear();
                            groupTittle.setText("Starred");
                            for (EmailItem emailItem: emailItems){
                                if (emailItem.isFavourite()){
                                    filtered.add(emailItem);
                                }
                            }
                            adapter.setData(filtered);
                            adapter.notifyDataSetChanged();
                        }
                        }
                    }
                },
                new DrawerAdapter.OnGroupItemClickListener() {
                    @Override
                    public void onGroupItemClick(DrawerGroupItem item) {
                        filtered.clear();
                        for (EmailItem emailItem: emailItems){
                            if (emailItem.getGroupID() != null){
                                if (emailItem.getGroupID() == item.getGroup().getId()){
                                    filtered.add(emailItem);
                                }
                            }
                        }
                        groupTittle.setText(item.getGroup().getTitle());
                        adapter.setData(filtered);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onGroupItemLongClick(DrawerGroupItem item, View popupAnchorView) {
                        showGroupPopupMenu(popupAnchorView.getContext(), item, popupAnchorView);
                    }
                }
        );

        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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
            boolean favourite = json.optBoolean("favoutrite", false);

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
                    favourite,
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
                requireActivity().runOnUiThread(()->{
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
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
                    if (isAdded() && getActivity() != null && !getActivity().isFinishing()) {
                        getActivity().runOnUiThread(() -> {
                            adapter = new EmailAdapter(getContext(), requireActivity(), emailItems, new EmailAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(EmailItem item) {
                                    Intent intent = new Intent(getContext(), EmailItemViewActivity.class);
                                    intent.putExtra("email_item", item);
                                    startActivity(intent);
                                }

                                @Override
                                public void onDeleteClick(EmailItem item) {
                                    supabaseClient.updateRecipientVisibility(getContext(),
                                            String.valueOf(item.getId()), new SupabaseClient.SBC_Callback() {
                                                @Override
                                                public void onFailure(IOException e) {
                                                }

                                                @Override
                                                public void onResponse(String responseBody) {
                                                    requireActivity().runOnUiThread(()->{
                                                        refreshEmails();
                                                    });
                                                }
                                            });
                                }

                                @Override
                                public void onAddToGroupClick(EmailItem item) {
                                    requireActivity().runOnUiThread(()->{
                                        showGroupDialog(item);
                                    });
                                }

                                @Override
                                public void onDeleteFromGroupClick(EmailItem item) {
                                    if (item.getGroupID() != null){
                                        supabaseClient.updateGroupId(getContext(), String.valueOf(item.getId()), null, new SupabaseClient.SBC_Callback() {
                                            @Override
                                            public void onFailure(IOException e) {

                                            }

                                            @Override
                                            public void onResponse(String responseBody) {
                                                item.setGroupID(null);
                                                filtered.remove(item);
                                                requireActivity().runOnUiThread(()->{
                                                    adapter.notifyDataSetChanged();
                                                    Toast.makeText(getContext(), R.string.success1, Toast.LENGTH_SHORT).show();
                                                });
                                            }
                                        });
                                    }
                                    else{
                                        requireActivity().runOnUiThread(()->{
                                            Toast.makeText(getContext(), R.string.letter_not_in_group, Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }
                            });
                        });
                    }

                    if (isAdded() && !isDetached() && requireActivity() != null && !requireActivity().isFinishing()) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                emailsRv.setAdapter(adapter);
                            }
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void getSentEmails(){
        supabaseClient.getSentMails(getContext(), new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onResponse(String responseBody) {
                try {
                    JSONArray array = new JSONArray(responseBody);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        if (parseEmail(jsonObject) != null){
                            sentEmailsItems.add(parseEmail(jsonObject));
                        }
                    }

                } catch (JSONException e) {
                }
            }
        });
    }

    private void refreshEmails() {
        emailItems.clear();
        sentEmailsItems.clear();
        adapter.notifyDataSetChanged();

        getSentEmails();

        supabaseClient.getReceivedMails(getContext(), new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
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

                    requireActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    });

                } catch (JSONException e) {
                }
            }
        });
    }

    private void showCreateGroupDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_group, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.et_group_title);
        Spinner spinnerColor = dialogView.findViewById(R.id.spinner_color);
        Button btnSave = dialogView.findViewById(R.id.btn_save_group);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.group_colors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(adapter);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String colorName = spinnerColor.getSelectedItem().toString();
            String hexColor = getColorHex(colorName);

            if (!title.isEmpty()) {
                ElettersGroup newGroup = new ElettersGroup(0, title, hexColor, "current_user_id"); // замените ID пользователя
                groupList.add(newGroup);
                drawerItems.add(new DrawerGroupItem(newGroup));
                drawerAdapter.notifyDataSetChanged();

                saveGroupToSupabase(title, hexColor);

                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), R.string.enter_group_name, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showEditGroupDialog(ElettersGroup elettersGroup) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_group, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.et_group_title);
        Spinner spinnerColor = dialogView.findViewById(R.id.spinner_color);
        Button btnSave = dialogView.findViewById(R.id.btn_save_group);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.group_colors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(adapter);
        etTitle.setText(elettersGroup.getTitle());
        spinnerColor.setSelection(getColorName(elettersGroup.getColor()));

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String colorName = spinnerColor.getSelectedItem().toString();
            String hexColor = getColorHex(colorName);

            if (!title.equals(elettersGroup.getTitle()) || !hexColor.equals(elettersGroup.getColor())) {
                elettersGroup.setColor(hexColor);
                elettersGroup.setTitle(title);
                drawerAdapter.notifyDataSetChanged();

                updateGroupToSupabase(elettersGroup.getId(), title, hexColor);

                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), R.string.enter_group_name, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showGroupDialog(EmailItem emailItem) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_group, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        RecyclerView rv = dialogView.findViewById(R.id.groups);

        List<BaseDrawerItem> gropues = new ArrayList<>();

        for (ElettersGroup group: groupList){
            gropues.add(new DrawerGroupItem(group));
        }

        DrawerAdapter drawerAdapter1 = new DrawerAdapter(gropues,
                new DrawerAdapter.OnMenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(DrawerMenuItem item) {

                    }
                }, new DrawerAdapter.OnGroupItemClickListener() {
            @Override
            public void onGroupItemClick(DrawerGroupItem item) {
                supabaseClient.updateGroupId(getContext(), String.valueOf(emailItem.getId()),
                        String.valueOf(item.getGroup().getId()), new SupabaseClient.SBC_Callback() {
                    @Override
                    public void onFailure(IOException e) {

                    }

                    @Override
                    public void onResponse(String responseBody) {
                        emailItem.setGroupID(item.getGroup().getId());
                        requireActivity().runOnUiThread(()->{
                            dialog.dismiss();
                        });
                    }
                });
            }

            @Override
            public void onGroupItemLongClick(DrawerGroupItem item, View anchorView) {

            }
        });

        rv.setAdapter(drawerAdapter1);

        dialog.show();
    }

    private String getColorHex(String colorName) {
        switch (colorName) {
            case "Red": return getString(R.string.color_red);
            case "Orange": return getString(R.string.color_orange);
            case "Yellow": return getString(R.string.color_yellow);
            case "Green": return getString(R.string.color_green);
            case "Cyan": return getString(R.string.color_cyan);
            case "Blue": return getString(R.string.color_blue);
            case "Purple": return getString(R.string.color_purple);
            default: return "#000000";
        }
    }

    private int getColorName(String hex){
        switch (hex){
            case "#FF0000": return 0;
            case "#FFA500": return 1;
            case "#FFFF00": return 2;
            case "#00FF00": return 3;
            case "#00FFFF": return 4;
            case "#0000FF": return 5;
            case "#800080": return 6;
            default: return 0;
        }
    }

    private void saveGroupToSupabase(String Tittle, String Color) {
        supabaseClient.addNewGroup(getContext(), Tittle, Color, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                requireActivity().runOnUiThread(()->{
                    Toast.makeText(requireContext(), requireContext().getString(R.string.error_in_group_create),Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                requireActivity().runOnUiThread(()->{
                    groupList.clear();
                    loadGroups();
                    Toast.makeText(requireContext(), requireContext().getString(R.string.success_group_create),Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void updateGroupToSupabase(int id, String Tittle, String Color) {
        supabaseClient.updateGroupById(getContext(), id,Tittle, Color, new SupabaseClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
            }

            @Override
            public void onResponse(String responseBody) {
            }
        });
    }

    private void showGroupPopupMenu(Context context, DrawerGroupItem item, View anchorView) {
        PopupMenu popupMenu = new PopupMenu(context, anchorView);
        popupMenu.inflate(R.menu.group_item_menu);

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.menu_edit) {
                showEditGroupDialog(item.getGroup());
                return true;
            } else if (menuItem.getItemId() == R.id.menu_delete) {
                ElettersGroup item1 = item.getGroup();
                DrawerGroupItem item2 = item;
                supabaseClient.deleteGroupWithCleanup(getContext(), item.getGroup().getId(), new SupabaseClient.SBC_Callback() {
                    @Override
                    public void onFailure(IOException e) {
                        requireActivity().runOnUiThread(()->{
                            Toast.makeText(requireContext(), requireContext().getString(R.string.error), Toast.LENGTH_SHORT);
                        });
                    }

                    @Override
                    public void onResponse(String responseBody) {
                        requireActivity().runOnUiThread(()->{
                            groupList.remove(item1);
                            drawerItems.remove(item2);
                            requireActivity().runOnUiThread(()->{
                                drawerAdapter.notifyDataSetChanged();
                            });
                        });
                    }
                });
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
}