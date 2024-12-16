package com.example.finapp.ui.goals_screens;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finapp.databinding.FragmentAddGoalBinding;
import com.example.finapp.viewModel.AddGoalViewModel;

public class AddGoalFragment extends Fragment {

    private FragmentAddGoalBinding binding;
    private AddGoalViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddGoalBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AddGoalViewModel.class);

        setupUI();
        return binding.getRoot();
    }

    public static AddGoalFragment newInstance() {
        return new AddGoalFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUI() {
        binding.ibClose.setOnClickListener(v -> {
            ((GoalsActivity) requireActivity()).closeFragments();
        });

        binding.ibSave.setOnClickListener(v -> {
            String title = binding.etTitle.getText().toString().trim();
            String sum = binding.etSum.getText().toString().replace(" ", "");
            String category = binding.spinnerCategory.getSelectedItem().toString();
            String comment = binding.etMultyLineComment.getText().toString().trim();

            if (viewModel.checkData(title, sum, category, comment)) {
                ((GoalsActivity) requireActivity()).closeFragments();
            } else {
                Toast.makeText(
                        requireActivity(),
                        viewModel.textOfToast,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
