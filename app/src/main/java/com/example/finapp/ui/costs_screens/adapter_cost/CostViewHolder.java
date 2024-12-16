package com.example.finapp.ui.costs_screens.adapter_cost;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finapp.R;
import com.example.finapp.databinding.CostItemBinding;
import com.example.finapp.model.Cost;

public class CostViewHolder extends RecyclerView.ViewHolder {
    private final CostItemBinding binding;
    private final CostAdapter.OnInfoClickListener onInfoClicked;
    private Cost cost;

    public CostViewHolder(@NonNull CostItemBinding binding, CostAdapter.OnInfoClickListener onInfoClicked) {
        super(binding.getRoot());
        this.binding = binding;
        this.onInfoClicked = onInfoClicked;
    }

    @SuppressLint("SetTextI18n")
    public void onBind(Cost data) {
        this.cost = data;
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInfoClicked.onInfoClicked(cost);
            }
        });

        binding.tvTitle.setText(cost.getTitleOfCost());
        binding.tvMoneyGoal.setText(String.valueOf(cost.getMoneyCost()));

        if (!cost.isExpense()) {
            binding.tvMoneyGoal.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.green));
            binding.tvMoneyGoal.setText("+" + cost.getMoneyCost());
        } else {
            binding.tvMoneyGoal.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.red));
            binding.tvMoneyGoal.setText("-" + cost.getMoneyCost());
        }
    }
}
