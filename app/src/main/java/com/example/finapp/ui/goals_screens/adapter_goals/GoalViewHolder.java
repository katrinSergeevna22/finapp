package com.example.finapp.ui.goals_screens.adapter_goals;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finapp.databinding.GoalItemBinding;
import com.example.finapp.model.Goal;

class GoalViewHolder extends RecyclerView.ViewHolder {

    private final GoalItemBinding binding;
    private final GoalAdapter.OnInfoClickedListener onInfoClicked;
    private Goal goal;

    public GoalViewHolder(GoalItemBinding binding, GoalAdapter.OnInfoClickedListener onInfoClicked) {
        super(binding.getRoot());
        this.binding = binding;
        this.onInfoClicked = onInfoClicked;
    }

    @SuppressLint("SetTextI18n")
    public void onBind(Goal data) {
        this.goal = data;

        binding.getRoot().setOnClickListener(v -> {
            onInfoClicked.onInfoClicked(goal);
        });

        binding.tvTitle.setText(goal.getTitleOfGoal());
        binding.tvMoneyGoal.setText(String.valueOf(goal.getMoneyGoal()));
        binding.tvProgressMoney.setText(goal.getProgressOfMoneyGoal() + " из " + goal.getMoneyGoal());
        binding.tvDate.setText(goal.getDate());

        float totalAmountToSave = goal.getMoneyGoal(); // Общая сумма для накопления
        float currentAmountSaved = goal.getProgressOfMoneyGoal(); // Текущая сумма, которая уже накоплена
        int progress = (int) ((currentAmountSaved / totalAmountToSave) * 100);
        binding.progressBar.setProgress(progress);
    }
}