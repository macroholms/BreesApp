package com.example.breesapp.models;

import com.example.breesapp.interfaces.DataItem;

public class GroupedItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private int type;
    private String header;
    private DataItem dataItem;

    public GroupedItem(String header) {
        this.type = TYPE_HEADER;
        this.header = header;
    }

    public GroupedItem(DataItem dataItem) {
        this.type = TYPE_ITEM;
        this.dataItem = dataItem;
    }

    public int getType() {
        return type;
    }

    public String getHeader() {
        return header;
    }

    public DataItem getDataItem() {
        return dataItem;
    }
}
