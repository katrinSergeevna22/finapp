package com.example.finapp.model;

public class User {
    private String login;
    private String password;

    // Конструктор
    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // Геттеры
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
