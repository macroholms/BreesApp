package com.example.breesapp.classes;

public class DrawerItem {
    private String title;
    private int iconResId;

    public DrawerItem(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }
}
