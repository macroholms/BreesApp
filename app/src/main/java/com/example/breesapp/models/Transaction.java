package com.example.breesapp.models;

public class Transaction {
    private int id;
    private String createdAt;
    private String idSender;
    private String idRecipient;
    private float money;

    public Transaction(int id, String createdAt, String idSender, String idRecipient, float money) {
        this.id = id;
        this.createdAt = createdAt;
        this.idSender = idSender;
        this.idRecipient = idRecipient;
        this.money = money;
    }

    public int getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getIdSender() {
        return idSender;
    }

    public String getIdRecipient() {
        return idRecipient;
    }

    public float getMoney() {
        return money;
    }
}
