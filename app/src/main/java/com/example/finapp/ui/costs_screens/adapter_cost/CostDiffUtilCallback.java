package com.example.finapp.ui.costs_screens.adapter_cost;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.finapp.model.Cost;

public class CostDiffUtilCallback extends DiffUtil.ItemCallback<Cost> {
    @Override
    public boolean areItemsTheSame(@NonNull Cost oldItem, @NonNull Cost newItem) {
        return oldItem.getCostId().equals(newItem.getCostId());
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull Cost oldItem, @NonNull Cost newItem) {
        return oldItem.equals(newItem);
    }
}
