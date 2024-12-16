package com.example.finapp.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.finapp.model.Goal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EditGoalViewModel extends ViewModel {
    private final DataRepository dataRepository = new DataRepository();
    public Goal selectGoal;
    public String exception = "Заполните все поля";
    private final GoalViewModel viewModel;

    public EditGoalViewModel() {
        viewModel = new GoalViewModel();
        viewModel.loadGoal();
    }

    public boolean checkData(String title, String sumCost, String category, String comment) {
        if (!title.isEmpty() && !sumCost.isEmpty() && !category.isEmpty()) {
            if (!checkIsTitle(title)) {
                exception = "Некорректный ввод названия!";
                return false;
            }
            if (!checkIsNumber(sumCost)) {
                exception = "Некорректный ввод суммы!";
                return false;
            }
            if (title.length() > 25) {
                exception = "Слишком длинное название!";
                return false;
            }
            float sum = Float.parseFloat(sumCost);
            if (sum > 1000000000000L) {
                exception = "Укажите реальную сумму";
                return false;
            }
            if (comment.length() > 240) {
                exception = "Слишком длинный комментарий";
                return false;
            }
            if (!title.equals(selectGoal.getTitleOfGoal()) && getGoalsList().contains(title)) {
                exception = "Цель с таким названием уже существует";
                return false;
            }
            if (sum != selectGoal.getMoneyGoal()) {
                if (sum < selectGoal.getProgressOfMoneyGoal()) {
                    exception = "Сумма, которую нужно накопить не может быть меньше накопленной суммы";
                    return false;
                }
                if (sum == selectGoal.getProgressOfMoneyGoal()) {
                    selectGoal.setStatus("Achieved");
                }
                if (selectGoal.getStatus().equals("Achieved") && sum > selectGoal.getMoneyGoal()) {
                    selectGoal.setStatus("Active");
                }
            }
            Map<String, Object> newGoalData = new HashMap<>();
            newGoalData.put("goalId", selectGoal.getGoalId());
            newGoalData.put("titleOfGoal", title);
            newGoalData.put("moneyGoal", sum);
            newGoalData.put("progressOfMoneyGoal", selectGoal.getProgressOfMoneyGoal());
            newGoalData.put("date", selectGoal.getDate());
            newGoalData.put("category", category);
            newGoalData.put("comment", comment);
            newGoalData.put("status", selectGoal.getStatus().toString());

            selectGoal.setTitleOfGoal(title);
            selectGoal.setMoneyGoal(sum);
            selectGoal.setCategory(category);
            selectGoal.setComment(comment);
            editGoalToBase(newGoalData, selectGoal);
            return true;
        }
        return false;
    }

    public void editGoalToBase(Map<String, Object> newGoal, Goal selectGoal) {
        dataRepository.editGoalToBase(newGoal, selectGoal);
    }

    public List<String> getGoalsList() {
        return viewModel.getGoalsLiveData().getValue() != null
                ? viewModel.getGoalsLiveData().getValue().stream()
                .map(goal -> goal.getTitleOfGoal())
                .collect(Collectors.toList())
                : List.of();
    }

    public boolean checkIsNumber(String sum) {
        return sum.matches("^\\d+(\\.\\d{1,2})?$");
    }

    public boolean checkIsTitle(String title) {
        return title.matches("[a-zA-Zа-яА-Я0-9.,\\s]+");
    }
}
