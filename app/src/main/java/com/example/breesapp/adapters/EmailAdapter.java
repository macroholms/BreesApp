package com.example.breesapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.breesapp.R;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.models.EmailItem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {

    private List<EmailItem> emailList;
    public Context context;
    public Activity activity;
    private final OnItemClickListener listener;
    private List<EmailItem> originalList;

    public interface OnItemClickListener {
        void onItemClick(EmailItem item);
        void onDeleteClick(EmailItem item);
        void onAddToGroupClick(EmailItem item);
        void onDeleteFromGroupClick(EmailItem item);
    }

    public void setData(List<EmailItem> emailList){
        this.emailList = emailList;
        this.originalList = new ArrayList<>(emailList);
    }

    public EmailAdapter(Context context, Activity activity,List<EmailItem> emailList, OnItemClickListener listener) {
        this.activity = activity;
        this.context = context;
        this.emailList = emailList;
        this.listener = listener;
        this.originalList = emailList;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_email, parent, false);
        EmailViewHolder holder = new EmailViewHolder(view);
        holder.setContext(context, activity);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        EmailItem item = emailList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    static class EmailViewHolder extends RecyclerView.ViewHolder {

        private final TextView textInitials;
        private final TextView textTheme;
        private final TextView textContent;
        private final ImageView imageAvatar;
        private final ImageView imageStar;
        Context context;
        Activity activity;

        public void setContext(Context context, Activity activity){
            this.context =context;
            this.activity = activity;
        }

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            textInitials = itemView.findViewById(R.id.text_initials);
            textTheme = itemView.findViewById(R.id.text_theme);
            textContent = itemView.findViewById(R.id.text_content);
            imageAvatar = itemView.findViewById(R.id.image_avatar);
            imageStar = itemView.findViewById(R.id.image_star);
        }

        public void bind(EmailItem item, OnItemClickListener listener) {

            textInitials.setText(item.getSenderFullName());
            textTheme.setText(item.getTheme());
            textContent.setText(item.getContent());


            Glide.with(itemView)
                    .load("https://nrwxeidhsclzffyojfvq.supabase.co/storage/v1/object/public/avatars//"
                            + item.getSenderAvatarUrl())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageAvatar);


            imageStar.setImageResource(item.isFavourite() ? R.drawable.ic_star_filled : R.drawable.ic_star_outlined);

            imageStar.setOnClickListener(v -> {
                SupabaseClient supabaseClient = new SupabaseClient();
                supabaseClient.updateFavouriteStatus(context, String.valueOf(item.getId()), !item.isFavourite(), new SupabaseClient.SBC_Callback() {
                    @Override
                    public void onFailure(IOException e) {
                        Log.d("!!!!!!!",e.getMessage());
                    }

                    @Override
                    public void onResponse(String responseBody) {
                        item.setFavourite(!item.isFavourite());
                        activity.runOnUiThread(()->{
                            imageStar.setImageResource(item.isFavourite() ? R.drawable.ic_star_filled : R.drawable.ic_star_outlined);
                        });
                    }
                });
            });

            itemView.setOnClickListener(v -> listener.onItemClick(item));

            itemView.setOnLongClickListener(v -> {
                showPopupMenu(v, item, listener);
                return true;
            });
        }

        private void showPopupMenu(View v, EmailItem item, OnItemClickListener listener) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.menu_email_context);

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menu_delete) {
                    listener.onDeleteClick(item);
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_add_to_group) {
                    listener.onAddToGroupClick(item);
                    return true;
                }else if(menuItem.getItemId() == R.id.menu_delete_from_group){
                    listener.onDeleteFromGroupClick(item);
                }
                return false;
            });

            popupMenu.show();
        }
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            emailList = new ArrayList<>(originalList);
        } else {
            List<EmailItem> filteredList = new ArrayList<>();
            String lowerCaseQuery = query.toLowerCase(Locale.getDefault());

            for (EmailItem item : originalList) {
                if (
                        item.getTheme().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
                                item.getContent().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
                                item.getSenderFullName().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)
                ) {
                    filteredList.add(item);
                }
            }

            emailList = filteredList;
        }

        notifyDataSetChanged();
    }
}