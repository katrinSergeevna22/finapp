package com.example.finapp.viewModel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finapp.model.Cost;
import com.example.finapp.model.Goal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddCostViewModel extends ViewModel {
    private final DataRepository dataRepository = new DataRepository();
    private double balance = 0.0;
    private String answerException = "";
    private final CostViewModel viewModel = new CostViewModel();
    public MutableLiveData<List<Goal>> goalsListLiveData = new MutableLiveData<>();
    private List<Goal> goalsList = List.of();
    private long sumNeedToGoal = 0L;

    public boolean checkDataIncome(String title, String sumCost, String category, String comment, double balanceNow) {
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

            float sum = Float.parseFloat(sumCost);

            if (title.length() > 25) {
                answerException = "Слишком длинное название!";
                return false;
            }
            if (sum > 1000000000000F) {
                answerException = "Укажите реальную сумму";
                return false;
            }
            if (comment.length() > 240) {
                answerException = "Слишком длинный комментарий";
                return false;
            }
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            Cost cost = new Cost("", title, sum, date, category, comment, false);
            saveIncomeToBase(cost);
            return true;
        }
        return false;
    }

    public void saveIncomeToBase(Cost newCost) {
        dataRepository.writeIncomeData(newCost);
        dataRepository.updateUserBalance(newCost.getMoneyCost() + balance);
    }

    public boolean checkDataExpense(String title, String sum, String category, String comment, @Nullable Goal selectGoal, double balanceNow) {
        if (!title.isEmpty() && !sum.isEmpty() && !category.isEmpty()) {
            if (!checkIsTitle(title)) {
                answerException = "Некорректный ввод названия!";
                return false;
            }
            if (!checkIsNumber(sum)) {
                answerException = "Некорректный ввод суммы!";
                return false;
            }
            if (title.length() > 25) {
                answerException = "Слишком длинное название!";
                return false;
            }
            float sumCost = Float.parseFloat(sum);
            if (sumCost > 1000000000000F) {
                answerException = "Укажите реальную сумму";
                return false;
            }
            if (comment.length() > 240) {
                answerException = "Слишком длинный комментарий";
                return false;
            }
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            String titleOfGoal = selectGoal.getTitleOfGoal();
            balance = balanceNow;
            if (sumCost > balance) {
                answerException = "Недостаочно средств на балансе";
                return false;
            } else {
                if ("Цель".equals(category)) {
                    Cost cost = new Cost("", title, sumCost, date, category, comment, titleOfGoal);
                    if (selectGoal.getMoneyGoal() < sumCost + selectGoal.getProgressOfMoneyGoal()) {
                        answerException = "Сумма больше, чем нужно для достижения цели";
                        return false;
                    } else {
                        viewModel.addProgressGoal(selectGoal, sumCost);
                        saveExpenseToBase(cost);
                        return true;
                    }
                } else {
                    Cost cost = new Cost("", title, sumCost, date, category, comment, true);
                    saveExpenseToBase(cost);
                    return true;
                }
            }
        } else {
            answerException = "Заполните все поля";
            return false;
        }
    }

    public void saveExpenseToBase(Cost newCost) {
        dataRepository.writeExpenseData(newCost);
        dataRepository.updateUserBalance(balance - newCost.getMoneyCost());
    }

    public boolean checkIsNumber(String sum) {
        return sum.matches("^\\d+(\\.\\d{1,2})?$");
    }

    public boolean checkIsTitle(String title) {
        return title.matches("[a-zA-Zа-яА-Я0-9.,\\s]+");
    }

    public String getAnswerException() {
        return answerException;
    }

    public void setAnswerException(String answerException) {
        this.answerException = answerException;
    }
}