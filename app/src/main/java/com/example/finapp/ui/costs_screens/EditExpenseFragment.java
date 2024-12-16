package com.example.finapp.ui.costs_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.finapp.R;
import com.example.finapp.databinding.FragmentEditExpenseBinding;
import com.example.finapp.model.Cost;
import com.example.finapp.model.Goal;
import com.example.finapp.viewModel.CostViewModel;
import com.example.finapp.viewModel.EditCostViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EditExpenseFragment extends Fragment {

    private FragmentEditExpenseBinding binding;
    private CostViewModel viewModel;
    private EditCostViewModel editViewModel;
    private Cost selectExpense;
    private List<String> goalForSpinner = new ArrayList<>();
    private List<Goal> goalList = new ArrayList<>();
    private Goal selectGoal = new Goal();
    private String titleOfGoal = "";

    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditExpenseBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(CostViewModel.class);
        editViewModel = new ViewModelProvider(requireActivity()).get(EditCostViewModel.class);
        viewModel.loadGoals();

        observeViewModel();
        setupUI();
        return binding.getRoot();
    }

    public static EditExpenseFragment newInstance() {
        return new EditExpenseFragment();
    }

    private void setupUI() {
        binding.ibSave.setOnClickListener(v -> editGoal());

        binding.ibClose.setOnClickListener(v -> {
            if (getActivity() instanceof ExpensesActivity) {
                ((ExpensesActivity) getActivity()).closeFragments();
            }
        });

        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (binding.spinnerCategory.getSelectedItem().equals("Цель")) {
                    binding.spinnerGoal.setVisibility(View.VISIBLE);
                    binding.tvGoalExpense.setVisibility(View.VISIBLE);

                    List<String> categoriesGoalArray = goalForSpinner;
                    String categoryGoalToSet = selectExpense.getGoal();

                    int categoryGoalIndex = categoriesGoalArray.indexOf(categoryGoalToSet);
                    if (categoryGoalIndex != -1) {
                        binding.spinnerGoal.setSelection(categoryGoalIndex);
                    }
                } else {
                    binding.spinnerGoal.setVisibility(View.GONE);
                    binding.tvGoalExpense.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.spinnerGoal.setVisibility(View.GONE);
                binding.tvGoalExpense.setVisibility(View.GONE);
            }
        });

        binding.spinnerGoal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectGoal = goalList.get(position);
                titleOfGoal = selectGoal.getTitleOfGoal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getActivity(), "Выберите цель", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.selectedCost.observe(getViewLifecycleOwner(), new Observer<Cost>() {
            @Override
            public void onChanged(Cost cost) {
                selectExpense = cost;
                binding.etTitle.setText(cost.getTitleOfCost());
                binding.etSum.setText(String.valueOf(cost.getMoneyCost()));
                binding.etMultyLineComment.setText(cost.getComment());
                editViewModel.setSelectCost(cost);

                String[] categoriesArray = getResources().getStringArray(R.array.categoriesExpense);
                String categoryToSet = selectExpense.getCategory();

                int categoryIndex = Arrays.asList(categoriesArray).indexOf(categoryToSet);
                if (categoryIndex != -1) {
                    binding.spinnerCategory.setSelection(categoryIndex);
                }
            }
        });

        viewModel.goalsListLiveData.observe(getViewLifecycleOwner(), new Observer<List<Goal>>() {
            @Override
            public void onChanged(List<Goal> goals) {
                List<Goal> activeGoals = goals.stream()
                        .filter(g -> "Active".equals(g.getStatus()) || g.getTitleOfGoal().equals(selectExpense.getGoal()))
                        .collect(Collectors.toList());

                goalForSpinner = activeGoals.stream()
                        .map(Goal::getTitleOfGoal)
                        .collect(Collectors.toList());

                adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, goalForSpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                binding.spinnerGoal.setAdapter(adapter);
                editViewModel.setGoalsMutableList(activeGoals);
                goalList = new ArrayList<>(activeGoals);

                List<String> categoriesGoalArray = goalForSpinner;
                String categoryGoalToSet = selectExpense.getGoal();

                int categoryGoalIndex = categoriesGoalArray.indexOf(categoryGoalToSet);
                if (categoryGoalIndex != -1) {
                    binding.spinnerGoal.setSelection(categoryGoalIndex);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void editGoal() {
        String title = binding.etTitle.getText().toString().trim();
        String sum = binding.etSum.getText().toString().replace(" ", "");
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String comment = binding.etMultyLineComment.getText().toString();

        if (editViewModel.checkExpenseData(title, sum, category, comment, titleOfGoal, viewModel.getBalanceNow())) {
            viewModel.setSelectedCost(editViewModel.getSelectCost());
            ((ExpensesActivity) getActivity()).closeFragments();
        } else {
            // Обработка ошибок
            Toast.makeText(getActivity(), editViewModel.getAnswerException(), Toast.LENGTH_SHORT).show();
            if ("Сумма больше, чем нужно для достижения цели".equals(editViewModel.getAnswerException())) {
                double sumTipForGoal = selectGoal.getMoneyGoal() - selectGoal.getProgressOfMoneyGoal() + selectExpense.getMoneyCost();
                binding.etSum.setText(String.valueOf(sumTipForGoal));
            }
            if ("Сумма больше, чем нужно для достижения выбранной цели".equals(editViewModel.getAnswerException())) {
                double sumTipForGoal = selectGoal.getMoneyGoal() - selectGoal.getProgressOfMoneyGoal();
                binding.etSum.setText(String.valueOf(sumTipForGoal));
            }
        }
    }
}
