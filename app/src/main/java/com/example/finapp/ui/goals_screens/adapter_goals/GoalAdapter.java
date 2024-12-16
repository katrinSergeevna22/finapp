package com.example.finapp.ui.goals_screens.adapter_goals;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.example.finapp.databinding.GoalItemBinding;
import com.example.finapp.model.Goal;

public class GoalAdapter extends ListAdapter<Goal, GoalViewHolder> {

    private final OnInfoClickedListener onInfoClicked;

    public GoalAdapter(OnInfoClickedListener onInfoClicked) {
        super(new GoalDiffUtilCallback());
        this.onInfoClicked = onInfoClicked;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        GoalItemBinding binding = GoalItemBinding.inflate(inflater, parent, false);
        return new GoalViewHolder(binding, onInfoClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }

    public interface OnInfoClickedListener {
        void onInfoClicked(Goal goal);
    }
}