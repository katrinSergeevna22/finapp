package com.example.finapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finapp.model.Goal;
import com.example.finapp.model.Tip;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GoalViewModel extends ViewModel {
    private final DataRepository dataRepository = new DataRepository();
    private final List<Goal> goalsList = new ArrayList<>();
    private final MutableLiveData<List<Goal>> goalsLiveData = new MutableLiveData<>();

    private final List<Goal> goalsCategoryList = new ArrayList<>();
    private final MutableLiveData<List<Goal>> goalsCategoryLiveData = new MutableLiveData<>();
    private String categoryGoal = "Active";
    private final MutableLiveData<Tip> oneRandomTipLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>();
    public boolean fragmentIsOpen = false;

    private final MutableLiveData<Goal> _selectedGoal = new MutableLiveData<>();

    public LiveData<Goal> selectedGoal() {
        return _selectedGoal;
    }

    public GoalViewModel() {
        loadGoal();
        loadGoalByCategory();
        loadOneRandomTipLiveData();
    }

    public void setSelectedCategory(String category) {
        selectedCategory.setValue(category);
        categoryGoal = category;
    }

    public LiveData<String> getSelectedCategory() {
        return selectedCategory;
    }

    public LiveData<List<Goal>> getGoalsLiveData() {
        return goalsLiveData;
    }

    public LiveData<List<Goal>> getGoalsCategoryLiveData() {
        return goalsCategoryLiveData;
    }

    public LiveData<Tip> getOneRandomTipLiveData() {
        return oneRandomTipLiveData;
    }

    public void loadGoal() {
        dataRepository.getGoals().observeForever(goals -> {
            goalsList.clear();
            goalsList.addAll(goals);

            categoryGoal = selectedCategory.getValue() != null ? selectedCategory.getValue() : "Active";
            goalsCategoryList.clear();

            for (Goal goal : goalsList) {
                if (Objects.equals(categoryGoal, goal.getStatus())) {
                    goalsCategoryList.add(goal);
                }
            }
            goalsLiveData.setValue(goalsList);
            goalsCategoryLiveData.setValue(goalsCategoryList);
        });
    }

    public void loadGoalByCategory() {
        getSelectedCategory().observeForever(category -> {
            goalsCategoryList.clear();
            // Фильтрация данных по категории и обновление RecyclerView
            for (Goal goal : goalsList) {
                if (category.equals(goal.getStatus())) {
                    goalsCategoryList.add(goal);
                }
            }
            goalsCategoryLiveData.setValue(goalsCategoryList);
        });
    }

    public void loadOneRandomTipLiveData() {
        dataRepository.getOneTip("Goal").observeForever(tip -> oneRandomTipLiveData.setValue(tip));
    }

    public String getTitleOfCategory() {
        categoryGoal = selectedCategory.getValue() != null ? selectedCategory.getValue() : "Active";
        switch (categoryGoal) {
            case "Active":
                return "Активные цели";
            case "Deleted":
                return "Архивные цели";
            case "Achieved":
                return "Достигнутые цели";
            default:
                return "Цели";
        }
    }

    public void deleteGoal() {
        dataRepository.deleteGoal(_selectedGoal.getValue());
    }


    public void setSelectedGoal(Goal goal) {
        _selectedGoal.setValue(goal);
    }
}
