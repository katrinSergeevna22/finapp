package com.example.finapp.ui.costs_screens.adapter_cost;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.example.finapp.databinding.CostItemBinding;
import com.example.finapp.model.Cost;

public class CostAdapter extends ListAdapter<Cost, CostViewHolder> {

    private final OnInfoClickListener onInfoClicked;

    public interface OnInfoClickListener {
        void onInfoClicked(Cost cost);
    }

    public CostAdapter(OnInfoClickListener onInfoClicked) {
        super(new CostDiffUtilCallback());
        this.onInfoClicked = onInfoClicked;
    }

    @NonNull
    @Override
    public CostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CostItemBinding binding = CostItemBinding.inflate(inflater, parent, false);
        return new CostViewHolder(binding, onInfoClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull CostViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }
}
