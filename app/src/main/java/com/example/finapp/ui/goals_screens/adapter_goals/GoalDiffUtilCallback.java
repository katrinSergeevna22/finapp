package com.example.finapp.ui.goals_screens.adapter_goals;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.finapp.model.Goal;

public class GoalDiffUtilCallback extends DiffUtil.ItemCallback<Goal> {
    @Override
    public boolean areItemsTheSame(@NonNull Goal oldItem, @NonNull Goal newItem) {
        return oldItem.getGoalId().equals(newItem.getGoalId());
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull Goal oldItem, @NonNull Goal newItem) {
        return oldItem.equals(newItem);
    }
}
