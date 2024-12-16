package com.example.finapp.ui.tips_screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.finapp.R;
import com.example.finapp.databinding.ActivityTipsBinding;
import com.example.finapp.model.Tip;
import com.example.finapp.ui.AuthActivity;
import com.example.finapp.ui.MainActivity;
import com.example.finapp.ui.tips_screens.adapter_tips.TipAdapter;
import com.example.finapp.ui.costs_screens.ExpensesActivity;
import com.example.finapp.ui.costs_screens.IncomeActivity;
import com.example.finapp.ui.goals_screens.GoalsActivity;
import com.example.finapp.viewModel.AuthViewModel;
import com.example.finapp.viewModel.TipViewModel;

import java.util.Objects;

public class TipsActivity extends AppCompatActivity {

    private ActivityTipsBinding binding;
    private TipViewModel viewModel;
    private TipAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTipsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(TipViewModel.class);

        // Запускаем загрузку финансовых советов
        viewModel.loadFinancialAdvices();

        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        binding.toolbarGoal.setNavigationOnClickListener(v ->
                binding.drawerGoal.openDrawer(GravityCompat.START)
        );

        adapter = new TipAdapter();
        binding.rcView.setLayoutManager(new LinearLayoutManager(this));
        binding.rcView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.shape_indent_rcview)));
        binding.rcView.addItemDecoration(itemDecoration);

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_goals) {
                navigateTo("GoalsActivity");
            } else if (item.getItemId() == R.id.menu_home) {
                navigateTo("MainActivity");
            } else if (item.getItemId() == R.id.menu_income) {
                navigateTo("IncomeActivity");
            } else if (item.getItemId() == R.id.menu_expense) {
                navigateTo("ExpenseActivity");
            } else if (item.getItemId() == R.id.menu_exit) {
                navigateTo("AuthActivity");
            }
            return true;
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void observeViewModel() {
        viewModel.getTipsLiveData().observe(this, tipsList -> {
            adapter.submitList(tipsList);
            binding.tvNumberTips.setText(String.valueOf(tipsList.size()));

            String selectedItem = getIntent().getStringExtra("selectedItem");
            int selectPosition = 0;
            for (int index = 0; index < tipsList.size(); index++) {
                Tip tip = tipsList.get(index);
                if (tip.getTitle().equals(selectedItem)) {
                    selectPosition = index;
                    break;
                }
            }
            binding.rcView.getLayoutManager().scrollToPosition(selectPosition);

            adapter.notifyDataSetChanged();
        });
    }

    private void navigateTo(String nameActivity) {
        Intent intent;
        switch (nameActivity) {
            case "GoalsActivity":
                intent = new Intent(this, GoalsActivity.class);
                startActivity(intent);
                break;
            case "MainActivity":
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case "IncomeActivity":
                intent = new Intent(this, IncomeActivity.class);
                startActivity(intent);
                break;
            case "ExpenseActivity":
                intent = new Intent(this, ExpensesActivity.class);
                startActivity(intent);
                break;
            case "AuthActivity":
                new AuthViewModel().exit();
                intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
                break;
        }
    }
}
