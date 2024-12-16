package com.example.finapp.model;

public class Cost {
    private String costId;
    private String titleOfCost;
    private float moneyCost;
    private String date;
    private String category;
    private String comment;
    private boolean isExpense;
    private String goal;

    // Конструктор по умолчанию
    public Cost() {
        this("", "", 0, "", null, "", "");
    }

    // Основной конструктор
    public Cost(String costId, String titleOfCost, float moneyCost, String date, String category, String comment, boolean isExpense) {
        this.costId = costId;
        this.titleOfCost = titleOfCost;
        this.moneyCost = moneyCost;
        this.date = date;
        this.category = category;
        this.comment = comment;
        this.isExpense = isExpense; // значение по умолчанию
    }

    // Дополнительный конструктор с параметром goal
    public Cost(String costId, String titleOfCost, float moneyCost, String date, String category,  String comment, String goal) {
        this(costId, titleOfCost, moneyCost, date, category, comment, true);
        this.goal = goal;
    }

    // Геттеры и сеттеры
    public String getCostId() {
        return costId;
    }

    public void setCostId(String costId) {
        this.costId = costId;
    }

    public String getTitleOfCost() {
        return titleOfCost;
    }

    public void setTitleOfCost(String titleOfCost) {
        this.titleOfCost = titleOfCost;
    }

    public float getMoneyCost() {
        return moneyCost;
    }

    public void setMoneyCost(float moneyCost) {
        this.moneyCost = moneyCost;
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

    public boolean isExpense() {
        return isExpense;
    }

    public void setExpense(boolean expense) {
        isExpense = expense;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}

