package com.example.breesapp.classes;

public class DrawerMenuItem extends BaseDrawerItem {
    private String title;
    private int iconResId;

    public DrawerMenuItem(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    public String getTitle() { return title; }
    public int getIconResId() { return iconResId; }

    @Override
    public int getType() { return 0; }
}