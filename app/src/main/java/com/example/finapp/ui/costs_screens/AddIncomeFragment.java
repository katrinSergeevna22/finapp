package com.example.finapp.ui.costs_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finapp.databinding.FragmentAddIncomeBinding;
import com.example.finapp.model.Cost;
import com.example.finapp.viewModel.AddCostViewModel;
import com.example.finapp.viewModel.CostViewModel;

public class AddIncomeFragment extends Fragment {
    private FragmentAddIncomeBinding binding;
    private CostViewModel viewModel;
    private AddCostViewModel addCostViewModel;
    private Cost selectIncome = new Cost();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddIncomeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(CostViewModel.class);
        addCostViewModel = new ViewModelProvider(this).get(AddCostViewModel.class);
        setupUI();

        return binding.getRoot();
    }

    public static AddIncomeFragment newInstance() {
        return new AddIncomeFragment();
    }

    private void setupUI() {
        binding.ibSave.setOnClickListener(v -> {
            String title = binding.etTitle.getText().toString().trim();
            String sum = binding.etSum.getText().toString().replace(" ", "");
            String category = binding.spinnerCategory.getSelectedItem().toString();
            String comment = binding.etMultyLineComment.getText().toString();

            if (addCostViewModel.checkDataIncome(title, sum, category, comment, viewModel.getBalanceNow())) {
                ((IncomeActivity) getActivity()).closeFragments();
            } else {
                // Обработка ошибок
                Toast.makeText(getActivity(), "Заполните все необходимые поля", Toast.LENGTH_SHORT).show();
            }
        });

        binding.ibClose.setOnClickListener(v -> {
            ((IncomeActivity) getActivity()).closeFragments();
        });
    }
}
