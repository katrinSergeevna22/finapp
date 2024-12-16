package com.example.finapp.ui.tips_screens.adapter_tips;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.finapp.model.Tip;

public class TipDiffUtilCallback extends DiffUtil.ItemCallback<Tip> {
    @Override
    public boolean areItemsTheSame(Tip oldItem,Tip newItem) {
        return oldItem.getTipId() == newItem.getTipId();
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull Tip oldItem, @NonNull Tip newItem) {
        return oldItem.equals(newItem);
    }
}
