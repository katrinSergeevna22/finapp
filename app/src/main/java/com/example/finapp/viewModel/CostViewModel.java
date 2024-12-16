package com.example.finapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finapp.model.Cost;
import com.example.finapp.model.Goal;
import com.example.finapp.model.Tip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostViewModel extends ViewModel {

    private final DataRepository dataRepository = new DataRepository();
    private final MutableLiveData<Cost> _selectedCost = new MutableLiveData<>();
    public LiveData<Cost> selectedCost = _selectedCost;

    private final List<Cost> incomesList = new ArrayList<>();
    private final MutableLiveData<List<Cost>> incomesLiveData = new MutableLiveData<>();
    private final List<Cost> expensesList = new ArrayList<>();
    private final MutableLiveData<List<Cost>> expensesLiveData = new MutableLiveData<>();

    private final MutableLiveData<Tip> oneRandomTipLiveData = new MutableLiveData<>();
    private final MutableLiveData<Goal> oneRandomGoalLiveData = new MutableLiveData<>();
    private double balance = 0.0;
    public final MutableLiveData<Double> balanceLiveData = new MutableLiveData<>();

    public final MutableLiveData<List<Goal>> goalsListLiveData = new MutableLiveData<>();
    private List<Goal> goalsList = new ArrayList<>();

    public CostViewModel() {
        getBalanceNow();
    }

    public double getBalanceNow() {
        dataRepository.getUserBalance().observeForever(it -> {
            balance = it;
            balanceLiveData.setValue(it);
        });
        return balance;
    }

    public void updateBalance(double newBalance) {
        dataRepository.updateUserBalance(newBalance);
    }

    public void deleteIncome() {
        double currentBalance = balanceLiveData.getValue() != null ? balanceLiveData.getValue() : 0.0;
        double sum = selectedCost.getValue() != null ? selectedCost.getValue().getMoneyCost() : 0.0;

        double newBalance = currentBalance - sum;
        updateBalance(newBalance);
        dataRepository.deleteIncome(selectedCost.getValue());
    }

    public void setSelectedCost(Cost cost) {
        _selectedCost.setValue(cost);
    }

    public LiveData<List<Cost>> getIncomeLiveData() {
        return incomesLiveData;
    }

    public void loadIncomes() {
        dataRepository.getIncomes().observeForever(income -> {
            incomesList.clear();
            incomesList.addAll(income);
            incomesLiveData.setValue(incomesList);
        });
    }

    public LiveData<List<Cost>> getExpenseLiveData() {
        return expensesLiveData;
    }

    public void loadExpenses() {
        dataRepository.getExpenses().observeForever(expense -> {
            expensesList.clear();
            expensesList.addAll(expense);
            expensesLiveData.setValue(expensesList);
        });
    }

    public LiveData<Tip> getOneRandomTipLiveData() {
        return oneRandomTipLiveData;
    }

    public void loadOneRandomTipLiveData(String category) {
        dataRepository.getOneTip(category).observeForever(oneRandomTipLiveData::setValue);
    }

    public LiveData<Goal> getOneRandomGoalLiveData() {
        return oneRandomGoalLiveData;
    }

    public void loadOneRandomGoalLiveData() {
        dataRepository.getOneGoal().observeForever(oneRandomGoalLiveData::setValue);
    }

    public void addProgressGoal(Goal goal, double sum) {
        double newSumProgress = goal.getProgressOfMoneyGoal() + sum;
        String status = goal.getStatus();

        if (newSumProgress == goal.getMoneyGoal()) {
            status = "Achieved";
        }

        Map<String, Object> newGoalData = new HashMap<>();
        newGoalData.put("goalId", goal.getGoalId());
        newGoalData.put("titleOfGoal", goal.getTitleOfGoal());
        newGoalData.put("moneyGoal", goal.getMoneyGoal());
        newGoalData.put("progressOfMoneyGoal", newSumProgress);
        newGoalData.put("date", goal.getDate());
        newGoalData.put("category", goal.getCategory());
        newGoalData.put("comment", goal.getComment());
        newGoalData.put("status", status);

        dataRepository.editGoalToBase(newGoalData, goal);
    }

    public void minusProgressGoal(Goal goal, double sum) {
        double newSumProgress = goal.getProgressOfMoneyGoal() - sum;
        String status = goal.getStatus();

        if ("Achieved".equals(status) && newSumProgress < goal.getMoneyGoal()) {
            status = "Active";
        }

        Map<String, Object> newGoalData = new HashMap<>();
        newGoalData.put("goalId", goal.getGoalId());
        newGoalData.put("titleOfGoal", goal.getTitleOfGoal());
        newGoalData.put("moneyGoal", goal.getMoneyGoal());
        newGoalData.put("progressOfMoneyGoal", newSumProgress);
        newGoalData.put("date", goal.getDate());
        newGoalData.put("category", goal.getCategory());
        newGoalData.put("comment", goal.getComment());
        newGoalData.put("status", status);

        dataRepository.editGoalToBase(newGoalData, goal);
    }

    public void deleteExpense() {
        double currentBalance = getBalanceNow();
        Cost selectExpense = selectedCost.getValue();

        if (selectExpense != null) {
            double sum = selectExpense.getMoneyCost();
            dataRepository.updateUserBalance(currentBalance + sum);
            dataRepository.deleteExpense(selectExpense);
        }
    }

    public void loadGoals() {
        dataRepository.getGoals().observeForever(goalsItems -> {
            goalsList = goalsItems;
            goalsListLiveData.setValue(goalsItems);
        });
    }
}