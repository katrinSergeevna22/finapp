package com.example.finapp.ui.costs_screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.finapp.R;
import com.example.finapp.databinding.FragmentInfoExpenseBinding;
import com.example.finapp.model.Cost;
import com.example.finapp.model.Goal;
import com.example.finapp.ui.BackgroundFragment;
import com.example.finapp.viewModel.CostViewModel;
import com.example.finapp.viewModel.DataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InfoExpenseFragment extends Fragment {
    private FragmentInfoExpenseBinding binding;
    private CostViewModel viewModel;
    private final DataRepository dataRepository = new DataRepository();
    private List<Goal> goalMutableList = new ArrayList<>();
    private final BackgroundFragment newBackgroundFragment = BackgroundFragment.newInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfoExpenseBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(CostViewModel.class);

        viewModel.selectedCost.observe(getViewLifecycleOwner(), cost -> {
            binding.tvTitleInfo.setText(cost.getTitleOfCost());
            binding.tvMoneyExpenseInfo.setText(String.valueOf(cost.getMoneyCost()));
            binding.tvDateExpenseInfo.setText(cost.getDate());
            binding.tvExpenseCategory.setText(cost.getCategory());

            if (cost.getCategory().equals("Цель")) {
                binding.tvCategoryGoal.setVisibility(View.VISIBLE);
                binding.tvExpenseCategoryGoal.setVisibility(View.VISIBLE);
                binding.tvExpenseCategoryGoal.setText(cost.getGoal());
            } else {
                binding.tvCategoryGoal.setVisibility(View.GONE);
                binding.tvExpenseCategoryGoal.setVisibility(View.GONE);
            }

            binding.tvExpenseComment.setText(cost.getComment());
        });

        LiveData<List<Goal>> goalLiveData = dataRepository.getGoals();
        goalLiveData.observe(requireActivity(), goalsItem -> {
            goalMutableList.clear();
            goalMutableList.addAll(goalsItem);
        });

        binding.ibEdit.setOnClickListener(v -> {
            requireFragmentManager().beginTransaction()
                    .replace(R.id.backgroundFragment, newBackgroundFragment)
                    .addToBackStack(null)
                    .commit();

            requireFragmentManager().beginTransaction()
                    .replace(R.id.place_holder_addExpenseFragment, EditExpenseFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        });

        binding.ibDelete.setOnClickListener(v -> {
            //Expense selectedExpense = viewModel.selectedCost.getValue();
            Cost selectedExpense = viewModel.selectedCost.getValue();
            if (selectedExpense != null) {
                if (selectedExpense.getCategory().equals("Цель")) {
                    List<Goal> goalList = goalMutableList.stream()
                            .filter(goal -> goal.getTitleOfGoal().equals(selectedExpense.getGoal()))
                            .collect(Collectors.toList());

                    if (!goalList.isEmpty()) {
                        Goal goal = goalList.get(0);
                        viewModel.minusProgressGoal(goal, selectedExpense.getMoneyCost());
                    }
                }
                viewModel.deleteExpense();
                ((ExpensesActivity) getActivity()).closeFragments();
            }
        });

        binding.ibClose.setOnClickListener(v -> ((ExpensesActivity) getActivity()).closeFragments());

        return binding.getRoot();
    }

    public static InfoExpenseFragment newInstance() {
        return new InfoExpenseFragment();
    }
}