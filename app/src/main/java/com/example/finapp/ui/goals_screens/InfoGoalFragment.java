package com.example.finapp.ui.goals_screens;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finapp.R;
import com.example.finapp.databinding.FragmentInfoGoalBinding;
import com.example.finapp.model.Goal;
import com.example.finapp.ui.BackgroundFragment;
import com.example.finapp.viewModel.EditGoalViewModel;
import com.example.finapp.viewModel.GoalViewModel;

import java.util.HashMap;
import java.util.Map;

public class InfoGoalFragment extends Fragment {
    private FragmentInfoGoalBinding binding;
    private GoalViewModel viewModel;
    private EditGoalViewModel editViewModel;
    private final BackgroundFragment backgroundFragment = BackgroundFragment.newInstance();

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfoGoalBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(GoalViewModel.class);
        editViewModel = new ViewModelProvider(requireActivity()).get(EditGoalViewModel.class);

        viewModel.selectedGoal().observe(getViewLifecycleOwner(), goal -> {
            binding.tvTitleInfo.setText(goal.getTitleOfGoal());
            binding.tvMoneyGoalInfo.setText(String.valueOf(goal.getMoneyGoal()));
            binding.tvDateInfo.setText(goal.getDate());
            binding.tvProgressMoneyInfo.setText(goal.getProgressOfMoneyGoal() + " из " + goal.getMoneyGoal());
            binding.tvGoalCategory.setText(goal.getCategory());
            binding.tvGoalComment.setText(goal.getComment());

            float totalAmountToSave = goal.getMoneyGoal(); // Общая сумма для накопления
            float currentAmountSaved = goal.getProgressOfMoneyGoal(); // Текущая сумма, которая уже накоплена
            int progress = (int) ((currentAmountSaved / totalAmountToSave) * 100);
            binding.progressBarInfo.setProgress(progress);
        });


        String selectedCategory = viewModel.getSelectedCategory().getValue();
        if ("Deleted".equals(selectedCategory)) {
            binding.ibAddMoney.setVisibility(View.GONE);
            binding.tvAddMoney.setVisibility(View.GONE);
            binding.tvDelete.setText("Восстановить");
            binding.tvEdit.setText("Удалить");
        } else if ("Achieved".equals(selectedCategory)) {
            binding.ibAddMoney.setVisibility(View.GONE);
            binding.tvAddMoney.setVisibility(View.GONE);
        }

        binding.ibEdit.setOnClickListener(v -> {
            if ("Deleted".equals(viewModel.getSelectedCategory().getValue())) {
                if (viewModel.selectedGoal().getValue() != null && viewModel.selectedGoal().getValue().getProgressOfMoneyGoal() != 0L) {
                    Toast.makeText(requireActivity(),
                            "Нельзя удалить цель, по которой у тебя есть прогресс! Продолжайте двигаться к своей цели!",
                            Toast.LENGTH_LONG).show();
                } else {
                    viewModel.deleteGoal();
                    ((GoalsActivity) requireActivity()).closeFragments();
                }
            } else {
                requireFragmentManager().beginTransaction()
                        .replace(R.id.backgroundFragment, backgroundFragment)
                        .addToBackStack(null)
                        .commit();
                requireFragmentManager().beginTransaction()
                        .replace(R.id.place_holder_addFragment, EditGoalFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Обработчик клика для ibDelete
        binding.ibDelete.setOnClickListener(v -> {
            Goal selectGoal = viewModel.selectedGoal().getValue();
            String newStatus;

            if ("Deleted".equals(viewModel.getSelectedCategory().getValue())) {
                if (selectGoal != null && selectGoal.getMoneyGoal() == selectGoal.getProgressOfMoneyGoal()) {
                    newStatus = "Achieved";
                } else {
                    newStatus = "Active";
                }
            } else {
                newStatus = "Deleted";
            }

            Map<String, Object> newGoalData = new HashMap<>();
            if (selectGoal != null) {
                newGoalData.put("goalId", selectGoal.getGoalId());
                newGoalData.put("titleOfGoal", selectGoal.getTitleOfGoal());
                newGoalData.put("moneyGoal", selectGoal.getMoneyGoal());
                newGoalData.put("progressOfMoneyGoal", selectGoal.getProgressOfMoneyGoal());
                newGoalData.put("date", selectGoal.getDate());
                newGoalData.put("category", selectGoal.getCategory());
                newGoalData.put("comment", selectGoal.getComment());
                newGoalData.put("status", newStatus);

                selectGoal.setStatus(newStatus);
                editViewModel.editGoalToBase(newGoalData, selectGoal);
                viewModel.fragmentIsOpen = false;

                ((GoalsActivity) requireActivity()).closeFragments();
            }
        });

        // Обработчик клика для ibClose
        binding.ibClose.setOnClickListener(v -> {
            ((GoalsActivity) requireActivity()).closeFragments();
        });

        return binding.getRoot();
    }

    public void closeInfoFragment() {
        this.getParentFragmentManager().popBackStack();
    }

    public static InfoGoalFragment newInstance() {
        return new InfoGoalFragment();
    }

}
