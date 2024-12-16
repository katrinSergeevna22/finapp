package com.example.finapp.model;

public class Goal {
    private String goalId;
    private String titleOfGoal;
    private float moneyGoal;
    private float progressOfMoneyGoal;
    private String date;
    private String category;
    private String comment;
    private String status;

    // Конструктор по умолчанию
    public Goal() {
        this("", "", 0, 0, "", null, null, "Active");
    }

    // Основной конструктор
    public Goal(String goalId, String titleOfGoal, float moneyGoal, float progressOfMoneyGoal, String date, String category, String comment, String status) {
        this.goalId = goalId;
        this.titleOfGoal = titleOfGoal;
        this.moneyGoal = moneyGoal;
        this.progressOfMoneyGoal = progressOfMoneyGoal;
        this.date = date;
        this.category = category;
        this.comment = comment;
        this.status = status != null ? status : "Active"; // значение по умолчанию
    }

    // Геттеры и сеттеры
    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public String getTitleOfGoal() {
        return titleOfGoal;
    }

    public void setTitleOfGoal(String titleOfGoal) {
        this.titleOfGoal = titleOfGoal;
    }

    public float getMoneyGoal() {
        return moneyGoal;
    }

    public void setMoneyGoal(float moneyGoal) {
        this.moneyGoal = moneyGoal;
    }

    public float getProgressOfMoneyGoal() {
        return progressOfMoneyGoal;
    }

    public void setProgressOfMoneyGoal(float progressOfMoneyGoal) {
        this.progressOfMoneyGoal = progressOfMoneyGoal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

