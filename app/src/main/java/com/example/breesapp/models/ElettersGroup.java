package com.example.breesapp.models;

public class ElettersGroup {
    private int id;
    private String tittle;
    private String color;
    private String user_id;

    public ElettersGroup() {
    }

    public ElettersGroup(int id, String tittle, String color, String user_id) {
        this.id = id;
        this.tittle = tittle;
        this.color = color;
        this.user_id = user_id;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return tittle;
    }

    public void setTitle(String tittle) {
        this.tittle = tittle;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "ElettersGroup{" +
                "id=" + id +
                ", tittle='" + tittle + '\'' +
                ", color='" + color + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
