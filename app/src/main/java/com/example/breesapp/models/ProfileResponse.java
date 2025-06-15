package com.example.breesapp.models;

public class ProfileResponse {
    private String id;
    private String full_name;
    private String avatar_url;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }

    public String getAvatar_url() { return avatar_url; }
    public void setAvatar_url(String avatar_url) { this.avatar_url = avatar_url; }
}
