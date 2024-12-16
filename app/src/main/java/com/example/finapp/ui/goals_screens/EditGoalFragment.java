package com.example.finapp.ui.goals_screens;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finapp.R;
import com.example.finapp.databinding.FragmentEditGoalBinding;
import com.example.finapp.model.Goal;
import com.example.finapp.viewModel.EditGoalViewModel;
import com.example.finapp.viewModel.GoalViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditGoalFragment extends Fragment {
    private FragmentEditGoalBinding binding;
    private GoalViewModel goalViewModel;
    private EditGoalViewModel editGoalViewModel;
    private Goal selectGoal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditGoalBinding.inflate(inflater);
        editGoalViewModel = new ViewModelProvider(this).get(EditGoalViewModel.class);
        goalViewModel = new ViewModelProvider(requireActivity()).get(GoalViewModel.class);

        observeSelectGoal();
        setupUI();
        return binding.getRoot();
    }

    public static EditGoalFragment newInstance() {
        return new EditGoalFragment();
    }

    private void setupUI() {
        binding.ibSave.setOnClickListener(v -> {
            String title = binding.etTitle.getText().toString().trim();
            String sum = binding.etSum.getText().toString().replace(" ", "");
            String category = binding.spinnerCategory.getSelectedItem().toString();
            String comment = binding.etMultyLineComment.getText().toString();

            if (editGoalViewModel.checkData(title, sum, category, comment)) {
                goalViewModel.setSelectedGoal(editGoalViewModel.selectGoal);
                ((GoalsActivity) getActivity()).closeFragments();
            } else {
                Toast.makeText(getActivity(), editGoalViewModel.exception, Toast.LENGTH_LONG).show();
            }
            hideKeyboardFrom(getActivity(), requireView());
        });

        binding.ibClose.setOnClickListener(v -> {
            hideKeyboardFrom(getActivity(), requireView());
            ((GoalsActivity) getActivity()).closeFragments();
        });
    }

    private void observeSelectGoal() {
        goalViewModel.selectedGoal().observe(getViewLifecycleOwner(), goal -> {
            binding.etTitle.setText(goal.getTitleOfGoal());
            binding.etSum.setText(String.valueOf(goal.getMoneyGoal()));
            binding.etMultyLineComment.setText(goal.getComment());
            selectGoal = goal;
            editGoalViewModel.selectGoal = selectGoal;
            String[] categoriesArray = getResources().getStringArray(R.array.categoriesGoals);
            String categoryToSet = goal.getCategory();

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

    private void editGoal() {

        String title = binding.etTitle.getText().toString();
        long sum = Long.parseLong(binding.etSum.getText().toString());
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String comment = binding.etMultyLineComment.getText().toString();
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (!title.isEmpty() && sum != 0L && !category.isEmpty()) {
            Map<String, Object> newGoalData = new HashMap<>();
            newGoalData.put("goalId", selectGoal.getGoalId());
            newGoalData.put("titleOfGoal", title);
            newGoalData.put("moneyGoal", sum);
            newGoalData.put("progressOfMoneyGoal", selectGoal.getProgressOfMoneyGoal());
            newGoalData.put("date", selectGoal.getDate());
            newGoalData.put("category", category);
            newGoalData.put("comment", comment);
            newGoalData.put("status", String.valueOf(selectGoal.getStatus()));

            selectGoal.setTitleOfGoal(title);
            selectGoal.setMoneyGoal(sum);
            selectGoal.setCategory(category);
            selectGoal.setComment(comment);
            goalViewModel.setSelectedGoal(selectGoal);

            editGoalViewModel.editGoalToBase(newGoalData, selectGoal);

            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
