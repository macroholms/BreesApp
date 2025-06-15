package com.example.breesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breesapp.R;
import com.example.breesapp.classes.DrawerItem;

import java.util.List;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    private List<DrawerItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public DrawerAdapter(List<DrawerItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DrawerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item, parent, false);
        return new DrawerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerViewHolder holder, int position) {
        DrawerItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.icon.setImageResource(item.getIconResId());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class DrawerViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public DrawerViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.drawer_item_title);
            icon = itemView.findViewById(R.id.drawer_item_icon);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
