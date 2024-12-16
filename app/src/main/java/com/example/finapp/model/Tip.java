package com.example.finapp.model;

public class Tip {
    private String tipId;
    private String title;
    private String text;
    private String theme;

    // Конструктор по умолчанию
    public Tip() {
        this("", "", "", "");
    }

    // Основной конструктор
    public Tip(String tipId, String title, String text, String theme) {
        this.tipId = tipId;
        this.title = title;
        this.text = text;
        this.theme = theme;
    }

    // Геттеры и сеттеры
    public String getTipId() {
        return tipId;
    }

    public void setTipId(String tipId) {
        this.tipId = tipId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
