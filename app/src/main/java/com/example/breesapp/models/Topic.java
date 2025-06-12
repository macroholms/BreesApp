package com.example.breesapp.models;

public class Topic {
    private String Tittle;
    private String Content;

    public Topic(String tittle, String content) {
        Tittle = tittle;
        Content = content;
    }

    public String getTittle() {
        return Tittle;
    }

    public void setTittle(String tittle) {
        Tittle = tittle;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
