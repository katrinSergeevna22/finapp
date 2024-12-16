package com.example.finapp.ui.tips_screens.adapter_tips;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.example.finapp.databinding.TipItemBinding;
import com.example.finapp.model.Tip;

public class TipAdapter extends ListAdapter<Tip, TipViewHolder> {

    public TipAdapter() {
        super(new TipDiffUtilCallback());
    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TipItemBinding binding = TipItemBinding.inflate(inflater, parent, false);
        return new TipViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }
}
