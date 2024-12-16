package com.example.finapp.ui.costs_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.finapp.R;
import com.example.finapp.databinding.FragmentAddExpenseBinding;
import com.example.finapp.model.Goal;
import com.example.finapp.viewModel.AddCostViewModel;
import com.example.finapp.viewModel.CostViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddExpenseFragment extends Fragment {

    private FragmentAddExpenseBinding binding;
    private CostViewModel viewModel;
    private AddCostViewModel addViewModel;
    private ArrayAdapter<String> adapter;
    private List<String> goalForSpinner = new ArrayList<>();
    private List<Goal> goalList = new ArrayList<>();
    private Goal selectGoal = new Goal();
    private String titleOfGoal = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddExpenseBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(CostViewModel.class);
        addViewModel = new ViewModelProvider(this).get(AddCostViewModel.class);
        viewModel.loadGoals();

        observeViewModel();
        setupUI();
        return binding.getRoot();
    }

    public static AddExpenseFragment newInstance() {
        return new AddExpenseFragment();
    }

    private void setupUI() {
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ("Цель".equals(binding.spinnerCategory.getSelectedItem().toString())) {
                    binding.spinnerGoal.setVisibility(View.VISIBLE);
                    binding.tvGoalExpense.setVisibility(View.VISIBLE);
                } else {
                    binding.spinnerGoal.setVisibility(View.GONE);
                    binding.tvGoalExpense.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerGoal.setVisibility(View.VISIBLE);
                binding.tvGoalExpense.setVisibility(View.VISIBLE);
            }
        });

        binding.spinnerGoal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectGoal = goalList.get(position);
                    titleOfGoal = selectGoal.getTitleOfGoal();
                } catch (Exception exception) {
                    onNothingSelected(parent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getActivity(), "Выберите цель", Toast.LENGTH_SHORT).show();
            }
        });

        binding.ibSave.setOnClickListener(v -> saveExpense());
        binding.ibClose.setOnClickListener(v -> ((ExpensesActivity) getActivity()).closeFragments());
    }


    private void saveExpense() {
        String title = binding.etTitle.getText().toString().trim();
        String sum = binding.etSum.getText().toString().replace(" ", "");
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String comment = binding.etMultyLineComment.getText().toString();

        if (addViewModel.checkDataExpense(title, sum, category, comment, selectGoal, viewModel.getBalanceNow())) {
            ((ExpensesActivity) getActivity()).closeFragments();
        } else {
            // Обработка ошибок
            Toast.makeText(getActivity(), addViewModel.getAnswerException(), Toast.LENGTH_SHORT).show();
            if ("Сумма больше, чем нужно для достижения цели".equals(addViewModel.getAnswerException())) {
                binding.etSum.setText(String.valueOf(selectGoal.getMoneyGoal() - selectGoal.getProgressOfMoneyGoal()));
            }
        }

    }

    private void observeViewModel() {
        viewModel.goalsListLiveData.observe(getViewLifecycleOwner(), new Observer<List<Goal>>() {
            @Override
            public void onChanged(List<Goal> goals) {
                goalForSpinner.clear();
                for (Goal goal : goals) {
                    goalForSpinner.add(goal.getTitleOfGoal());
                }
                adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, goalForSpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerGoal.setAdapter(adapter);
                goalList = goals;
                adapter.notifyDataSetChanged();
            }
        });
    }
}