package com.example.finapp.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.finapp.model.Cost;
import com.example.finapp.model.Goal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditCostViewModel extends ViewModel {

    private final DataRepository dataRepository = new DataRepository();
    private final CostViewModel viewModel = new CostViewModel();
    private Cost selectCost;
    private double balance = 0.0;
    private List<Goal> goalsMutableList;
    private String answerException = "";

    public boolean checkIncomeData(String title, String sumCost, String category, String comment, double balanceNow) {
        if (!title.isEmpty() && !sumCost.isEmpty() && !category.isEmpty()) {
            balance = balanceNow;

            if (!checkIsTitle(title)) {
                answerException = "Некорректный ввод названия!";
                return false;
            }
            if (!checkIsNumber(sumCost)) {
                answerException = "Некорректный ввод суммы!";
                return false;
            }
            if (title.length() > 25) {
                answerException = "Слишком длинное название!";
                return false;
            }
            float sum = Float.parseFloat(sumCost);
            if (sum > 1000000000000F) {
                answerException = "Укажите реальную сумму";
                return false;
            }
            if (comment.length() > 240) {
                answerException = "Слишком длинный комментарий";
                return false;
            }

            if (sum < selectCost.getMoneyCost() && selectCost.getMoneyCost() - sum > balance) {
                answerException = "Недостаточно средств";
                return false;
            } else {
                Map<String, Object> newIncome = Map.of(
                        "costId", selectCost.getCostId(),
                        "titleOfCost", title,
                        "moneyCost", sum,
                        "date", selectCost.getDate(),
                        "category", category,
                        "comment", comment,
                        "isExpense", false
                );

                editIncomeToBase(newIncome, selectCost);
                return true;
            }
        }
        answerException = "Заполните все поля";
        return false;
    }

    public void editIncomeToBase(Map<String, Object> newIncome, Cost selectIncome) {
        Object newSum = newIncome.get("moneyCost");
        if (!newSum.equals(selectIncome.getMoneyCost())) {
            double diff;
            if (Double.parseDouble(newSum.toString()) > selectIncome.getMoneyCost()) {
                diff = Double.parseDouble(newSum.toString()) - selectIncome.getMoneyCost();
                dataRepository.updateUserBalance(balance + diff);
            } else {
                diff = selectIncome.getMoneyCost() - Double.parseDouble(newSum.toString());
                dataRepository.updateUserBalance(balance - diff);
            }
        }
        selectIncome.setTitleOfCost(newIncome.get("titleOfCost").toString());
        selectIncome.setMoneyCost(Float.parseFloat(newIncome.get("moneyCost").toString()));
        selectIncome.setCategory(newIncome.get("category").toString());
        selectIncome.setComment(newIncome.get("comment").toString());
        dataRepository.editIncomeToBase(newIncome, selectIncome);
    }

    public boolean checkIsNumber(String sum) {
        return sum.matches("^\\d+(\\.\\d{1,2})?$");
    }

    public boolean checkIsTitle(String title) {
        return title.matches("[a-zA-Zа-яА-Я0-9.,\\s]+");
    }

    public boolean checkExpenseData(
            String title,
            String sumCost,
            String category,
            String comment,
            String titleOfGoal,
            double balanceNow) {

        if (!title.isEmpty() && !sumCost.isEmpty() && !category.isEmpty()) {
            balance = balanceNow;

            if (!checkIsTitle(title)) {
                answerException = "Некорректный ввод названия!";
                return false;
            }

            if (!checkIsNumber(sumCost)) {
                answerException = "Некорректный ввод суммы!";
                return false;
            }

            if (title.length() > 25) {
                answerException = "Слишком длинное название!";
                return false;
            }

            float sum = Float.parseFloat(sumCost);

            if (sum > 1000000000000L) {
                answerException = "Укажите реальную сумму";
                return false;
            }

            if (comment.length() > 240) {
                answerException = "Слишком длинный комментарий";
                return false;
            }

            if (sum > selectCost.getMoneyCost() && sum - selectCost.getMoneyCost() > balance) {
                answerException = "Недостаточно средств на балансе";
                return false;
            }

            if ("Цель".equals(selectCost.getCategory())) {
                Goal goalOld = goalsMutableList.stream()
                        .filter(goal -> goal.getTitleOfGoal().equals(selectCost.getGoal()))
                        .findFirst()
                        .orElse(null);

                if ("Цель".equals(category)) {
                    Goal goalNew = goalsMutableList.stream()
                            .filter(goal -> goal.getTitleOfGoal().equals(titleOfGoal))
                            .findFirst()
                            .orElse(null);

                    if (goalOld != null && goalNew != null && !goalOld.getTitleOfGoal().equals(goalNew.getTitleOfGoal())) {
                        if (goalNew.getMoneyGoal() < sum + goalNew.getProgressOfMoneyGoal()) {
                            answerException = "Сумма больше, чем нужно для достижения выбранной цели";
                            return false;
                        }
                        viewModel.minusProgressGoal(goalOld, selectCost.getMoneyCost());
                        viewModel.addProgressGoal(goalNew, sum);
                    } else if (goalNew != null) {
                        if (selectCost.getMoneyCost() > sum) {
                            viewModel.minusProgressGoal(goalNew, selectCost.getMoneyCost() - sum);
                        } else if (selectCost.getMoneyCost() < sum) {
                            if (goalNew.getMoneyGoal() < sum + goalNew.getProgressOfMoneyGoal() - selectCost.getMoneyCost()) {
                                answerException = "Сумма больше, чем нужно для достижения цели";
                                return false;
                            }
                            viewModel.addProgressGoal(
                                    goalNew,
                                    sum - selectCost.getMoneyCost()
                            );
                        }
                    }
                } else {
                    viewModel.minusProgressGoal(
                            goalOld,
                            selectCost.getMoneyCost()
                    );
                }
            } else if (category.equals("Цель")) {
                Goal goalNew = goalsMutableList.stream()
                        .filter(goal -> goal.getTitleOfGoal().equals(titleOfGoal))
                        .findFirst()
                        .orElse(null);
                viewModel.addProgressGoal(
                        goalNew,
                        sum
                );
            }
            Map<String, Object> newExpense = new HashMap<>();
            newExpense.put("costId", selectCost.getCostId());
            newExpense.put("titleOfCost", title);
            newExpense.put("moneyCost", Float.parseFloat(String.valueOf(sum)));
            newExpense.put("date", selectCost.getDate());
            newExpense.put("category", category);
            newExpense.put("goal", titleOfGoal);
            newExpense.put("comment", comment);

            editExpenseToBase(newExpense, selectCost);
            return true;

        } else {
            answerException = "Заполните все поля";
            return false;
        }
    }

    public void editExpenseToBase(Map<String, Object> newExpense, Cost selectExpense) {
        float newSum = Float.parseFloat(newExpense.get("moneyCost").toString());
        float pastSum = selectExpense.getMoneyCost();

        if (newSum > pastSum) {
            dataRepository.updateUserBalance(balance - (newSum - pastSum));
        } else {
            dataRepository.updateUserBalance(balance + (pastSum - newSum));
        }

        selectExpense.setTitleOfCost(newExpense.get("titleOfCost").toString());
        selectExpense.setMoneyCost(Float.parseFloat(newExpense.get("moneyCost").toString()));
        selectExpense.setCategory(newExpense.get("category").toString());
        selectExpense.setComment(newExpense.get("comment").toString());

        if ("Цель".equals(selectExpense.getCategory())) {
            selectExpense.setGoal(newExpense.get("goal").toString());
        }

        dataRepository.editExpenseToBase(newExpense, selectExpense);
    }

    public String getAnswerException() {
        return answerException;
    }

    public void setAnswerException(String answerException) {
        this.answerException = answerException;
    }

    public void setGoalsMutableList(List<Goal> goalsMutableList) {
        this.goalsMutableList = goalsMutableList;
    }

    public void setSelectCost(Cost selectCost) {
        this.selectCost = selectCost;
    }

    public Cost getSelectCost() {
        return selectCost;
    }
}