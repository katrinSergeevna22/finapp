package com.example.finapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.finapp.model.Goal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddGoalViewModel extends ViewModel {
    private final DataRepository dataRepository = new DataRepository();
    public String textOfToast = "";
    private final GoalViewModel viewModel;

    public AddGoalViewModel() {
        viewModel = new GoalViewModel();
        viewModel.loadGoal();
    }

    public boolean checkData(String title, String sum, String category, String comment) {
        if (!title.isEmpty() && !sum.isEmpty() && !category.isEmpty()) {
            if (!checkIsTitle(title)) {
                textOfToast = "Некорректный ввод названия!";
                return false;
            }
            if (!checkIsNumber(sum)) {
                textOfToast = "Некорректный ввод суммы!";
                return false;
            }
            if (title.length() > 25) {
                textOfToast = "Слишком длинное название!";
                return false;
            }
            float moneyGoal = Float.parseFloat(sum);
            if (moneyGoal > 1000000000000F) {
                textOfToast = "Укажите реальную сумму";
                return false;
            }
            if (comment.length() > 240) {
                textOfToast = "Слишком длинный комментарий";
                return false;
            }
            if (getGoalsList().contains(title)) {
                textOfToast = "Цель с таким названием уже существует";
                return false;
            }
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            Goal goal = new Goal("", title, moneyGoal, 0, date, category, comment, "Active");

            saveGoalToBase(goal);
            return true;
        }
        textOfToast = "Заполните все поля";
        return false;
    }

    public void saveGoalToBase(Goal newGoal) {
        dataRepository.writeGoalData(newGoal);
    }

    List<String> list = new ArrayList<String>();

    public List<String> getGoalsList() {
        LiveData<List<Goal>> goalsLiveData = viewModel.getGoalsLiveData();
        if (goalsLiveData.getValue() != null) {
            for (Goal goal : goalsLiveData.getValue()) {
                list.add(goal.getTitleOfGoal());
            }
        }
        return list;
    }

    public boolean checkIsNumber(String sum) {
        return sum.matches("^\\d+(\\.\\d{1,2})?$");
    }

    public boolean checkIsTitle(String title) {
        return title.matches("[a-zA-Zа-яА-Я0-9.,\\s]+");
    }
}
