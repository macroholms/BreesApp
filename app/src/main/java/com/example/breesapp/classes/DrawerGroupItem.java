package com.example.breesapp.classes;

import com.example.breesapp.models.ElettersGroup;

public class DrawerGroupItem extends BaseDrawerItem {
    private ElettersGroup group;

    public DrawerGroupItem(ElettersGroup group) {
        this.group = group;
    }

    public ElettersGroup getGroup() { return group; }

    @Override
    public int getType() { return 1; }
}