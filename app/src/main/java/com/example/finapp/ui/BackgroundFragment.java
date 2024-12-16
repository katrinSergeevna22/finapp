package com.example.finapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finapp.databinding.FragmentBackgroundBinding;
import com.example.finapp.ui.costs_screens.ExpensesActivity;
import com.example.finapp.ui.costs_screens.IncomeActivity;
import com.example.finapp.ui.goals_screens.GoalsActivity;

public class BackgroundFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentBackgroundBinding binding = FragmentBackgroundBinding.inflate(inflater, container, false);
        binding.frameLayout.setOnClickListener(v -> {
            if (getActivity() instanceof GoalsActivity) {
                ((GoalsActivity) getActivity()).closeFragments();
            }
            if (getActivity() instanceof IncomeActivity) {
                ((IncomeActivity) getActivity()).closeFragments();
            }
            if (getActivity() instanceof ExpensesActivity) {
                ((ExpensesActivity) getActivity()).closeFragments();
            }
        });
        return binding.getRoot();
    }

    public void closeBackgroundFragment() {
        getParentFragmentManager().popBackStack();
    }

    public static BackgroundFragment newInstance() {
        return new BackgroundFragment();
    }
}
