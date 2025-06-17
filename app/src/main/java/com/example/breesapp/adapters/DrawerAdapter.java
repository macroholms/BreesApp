package com.example.breesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breesapp.R;
import com.example.breesapp.classes.BaseDrawerItem;
import com.example.breesapp.classes.DrawerGroupItem;
import com.example.breesapp.classes.DrawerMenuItem;
import com.example.breesapp.models.ElettersGroup;

import java.util.List;

public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnMenuItemClickListener {
        void onMenuItemClick(DrawerMenuItem item);
    }

    public interface OnGroupItemClickListener {
        void onGroupItemClick(DrawerGroupItem item);
        void onGroupItemLongClick(DrawerGroupItem item, View anchorView);
    }

    private List<BaseDrawerItem> items;
    private OnMenuItemClickListener menuListener;
    private OnGroupItemClickListener groupListener;

    public DrawerAdapter(List<BaseDrawerItem> items,
                         OnMenuItemClickListener menuListener,
                         OnGroupItemClickListener groupListener) {
        this.items = items;
        this.menuListener = menuListener;
        this.groupListener = groupListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item, parent, false);
            return new MenuItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_group_item, parent, false);
            return new GroupViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BaseDrawerItem item = items.get(position);
        if (item.getType() == 0) {
            ((MenuItemViewHolder) holder).bind((DrawerMenuItem) item);
        } else {
            ((GroupViewHolder) holder).bind((DrawerGroupItem) item);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.drawer_item_title);
            icon = itemView.findViewById(R.id.drawer_item_icon);
        }

        public void bind(DrawerMenuItem item) {
            title.setText(item.getTitle());
            icon.setImageResource(item.getIconResId());

            itemView.setOnClickListener(v -> {
                if (menuListener != null) {
                    menuListener.onMenuItemClick(item);
                }
            });
        }
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        View colorDot;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.group_title);
            this.colorDot = itemView.findViewById(R.id.group_color_dot);
        }

        public void bind(DrawerGroupItem item) {
            ElettersGroup group = item.getGroup();
            title.setText(group.getTitle());

            try {
                colorDot.setBackgroundColor(android.graphics.Color.parseColor(group.getColor()));
            } catch (Exception e) {
                colorDot.setBackgroundColor(android.graphics.Color.GRAY);
            }

            itemView.setOnClickListener(v -> {
                if (groupListener != null) {
                    groupListener.onGroupItemClick(item);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (groupListener != null) {
                    groupListener.onGroupItemLongClick(item, itemView);
                    return true;
                }
                return false;
            });
        }
    }
}