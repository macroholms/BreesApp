package com.example.breesapp.models;

public class UserModel {

    private float balance;
    private String full_name;
    private String avatar_url;

    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }

    public String getAvatar_url() { return avatar_url; }
    public void setAvatar_url(String avatar_url) { this.avatar_url = avatar_url; }

    public UserModel() {
    }

    public UserModel(float balance, String full_name, String avatar_url) {
        this.balance = balance;
        this.full_name = full_name;
        this.avatar_url = avatar_url;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
