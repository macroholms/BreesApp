package com.example.breesapp.models;

public class BalanceHistory {
    private int id;
    private String createdAt;
    private String userId;
    private String type;
    private float value;

    public BalanceHistory(int id, String createdAt, String userId, String type, float value) {
        this.id = id;
        this.createdAt = createdAt;
        this.userId = userId;
        this.type = type;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public float getValue() {
        return value;
    }
}