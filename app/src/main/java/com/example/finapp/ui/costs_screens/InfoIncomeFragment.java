package com.example.finapp.ui.costs_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finapp.R;
import com.example.finapp.databinding.FragmentInfoIncomeBinding;
import com.example.finapp.ui.BackgroundFragment;
import com.example.finapp.viewModel.CostViewModel;

public class InfoIncomeFragment extends Fragment {
    private FragmentInfoIncomeBinding binding;
    private CostViewModel viewModel;
    private final BackgroundFragment newBackgroundFragment = BackgroundFragment.newInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInfoIncomeBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(CostViewModel.class);
        viewModel.selectedCost.observe(getViewLifecycleOwner(), cost -> {
            binding.tvTitleInfo.setText(cost.getTitleOfCost());
            binding.tvMoneyIncomeInfo.setText(String.valueOf(cost.getMoneyCost()));
            binding.tvDateIncomeInfo.setText(cost.getDate());
            binding.tvIncomeCategory.setText(cost.getCategory());
            binding.tvIncomeComment.setText(cost.getComment());
        });

        binding.ibEdit.setOnClickListener(v -> {
            requireFragmentManager().beginTransaction()
                    .replace(R.id.backgroundFragment, newBackgroundFragment)
                    .addToBackStack(null)
                    .commit();

            requireFragmentManager().beginTransaction()
                    .replace(R.id.place_holder_addIncomeFragment, EditIncomeFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        });

        binding.ibDelete.setOnClickListener(v -> {
            double balance = viewModel.getBalanceNow();
            if (viewModel.selectedCost.getValue() != null &&
                    viewModel.selectedCost.getValue().getMoneyCost() > balance) {
                Toast.makeText((IncomeActivity) requireActivity(), "Баланс станет меньше нуля", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.deleteIncome();
                ((IncomeActivity) requireActivity()).closeFragments();
            }
        });

        binding.ibClose.setOnClickListener(v -> {
            ((IncomeActivity) requireActivity()).closeFragments();
        });

        return binding.getRoot();
    }

    public static InfoIncomeFragment newInstance() {
        return new InfoIncomeFragment();
    }
}
