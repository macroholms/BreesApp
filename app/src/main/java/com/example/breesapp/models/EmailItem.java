package com.example.breesapp.models;

import java.util.Date;

import java.util.Date;

public class EmailItem {
    private int id;
    private Date createdAt;
    private String senderFullName;
    private String senderAvatarUrl;
    private String recipientFullName;
    private String recipientAvatarUrl;
    private String theme;
    private String content;
    private boolean senderVisible;
    private boolean recipientVisible;
    private Integer groupID; // Может быть null
    private boolean readed;
    private Object fileUrl;

    public EmailItem(int id, Date createdAt, String senderFullName, String senderAvatarUrl,
                     String recipientFullName, String recipientAvatarUrl, String theme,
                     String content, boolean senderVisible, boolean recipientVisible,
                     Integer groupID, boolean readed, Object fileUrl) {
        this.id = id;
        this.createdAt = createdAt;
        this.senderFullName = senderFullName;
        this.senderAvatarUrl = senderAvatarUrl;
        this.recipientFullName = recipientFullName;
        this.recipientAvatarUrl = recipientAvatarUrl;
        this.theme = theme;
        this.content = content;
        this.senderVisible = senderVisible;
        this.recipientVisible = recipientVisible;
        this.groupID = groupID;
        this.readed = readed;
        this.fileUrl = fileUrl;
    }

    public int getId() { return id; }
    public Date getCreatedAt() { return createdAt; }
    public String getSenderFullName() { return senderFullName; }
    public String getSenderAvatarUrl() { return senderAvatarUrl; }
    public String getRecipientFullName() { return recipientFullName; }
    public String getRecipientAvatarUrl() { return recipientAvatarUrl; }
    public String getTheme() { return theme; }
    public String getContent() { return content; }
    public boolean isSenderVisible() { return senderVisible; }
    public boolean isRecipientVisible() { return recipientVisible; }
    public Integer getGroupID() { return groupID; }
    public boolean isReaded() { return readed; }
    public Object getFileUrl() { return fileUrl; }

    public void setReaded(boolean readed) { this.readed = readed; }
    public void setImportant(boolean important) { this.important = important; }

    private boolean important;

    public boolean isImportant() {
        return important;
    }
}