package com.example.finapp.ui.costs_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finapp.R;
import com.example.finapp.databinding.FragmentEditIncomeBinding;
import com.example.finapp.model.Cost;
import com.example.finapp.viewModel.CostViewModel;
import com.example.finapp.viewModel.EditCostViewModel;

public class EditIncomeFragment extends Fragment {
    private FragmentEditIncomeBinding binding;
    private CostViewModel viewModel;
    private EditCostViewModel editViewModel;
    private Cost selectIncome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditIncomeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(CostViewModel.class);
        editViewModel = new ViewModelProvider(requireActivity()).get(EditCostViewModel.class);
        observeSelectCost();
        setupUI();
        return binding.getRoot();
    }

    public static EditIncomeFragment newInstance() {
        return new EditIncomeFragment();
    }

    private void setupUI() {
        binding.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editIncome();
            }
        });

        binding.ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public void observeSelectCost() {
        viewModel.selectedCost.observe(getViewLifecycleOwner(), cost -> {
            binding.etTitle.setText(cost.getTitleOfCost());
            binding.etSum.setText(String.valueOf(cost.getMoneyCost()));
            binding.etMultyLineComment.setText(cost.getComment());
            selectIncome = cost;
            editViewModel.setSelectCost(cost);

            String[] categoriesArray = getResources().getStringArray(R.array.categoriesIncome);
            String categoryToSet = selectIncome.getCategory();
            int categoryIndex = -1;

            for (int i = 0; i < categoriesArray.length; i++) {
                if (categoriesArray[i].equals(categoryToSet)) {
                    categoryIndex = i;
                    break;
                }
            }
            if (categoryIndex != -1) {
                binding.spinnerCategory.setSelection(categoryIndex);
            }
        });
    }

    public void editIncome() {
        String title = binding.etTitle.getText().toString().trim();
        String sum = binding.etSum.getText().toString().replace(" ", "");
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String comment = binding.etMultyLineComment.getText().toString();

        boolean resultChecking = editViewModel.checkIncomeData(title, sum, category, comment, viewModel.getBalanceNow());
        if (resultChecking) {
            viewModel.setSelectedCost(editViewModel.getSelectCost());
            ((IncomeActivity) requireActivity()).closeFragments();
        } else {
            Toast.makeText(requireActivity(), editViewModel.getAnswerException(), Toast.LENGTH_SHORT).show();
        }
    }
}
