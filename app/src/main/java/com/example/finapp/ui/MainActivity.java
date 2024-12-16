package com.example.finapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.finapp.R;
import com.example.finapp.databinding.ActivityMainBinding;
import com.example.finapp.model.Goal;
import com.example.finapp.model.Tip;
import com.example.finapp.ui.costs_screens.ExpensesActivity;
import com.example.finapp.ui.costs_screens.IncomeActivity;
import com.example.finapp.ui.goals_screens.GoalsActivity;
import com.example.finapp.ui.tips_screens.TipsActivity;
import com.example.finapp.viewModel.AuthViewModel;
import com.example.finapp.viewModel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private Tip mainTip = new Tip();
    private Goal mainGoal = new Goal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(binding.getRoot());
        observeViewModel();
        setupUI();
    }

    private void setupUI() {
        binding.tvGreeting.setText(ContextCompat.getString(this, R.string.greeting));
        binding.tvTitleMoney.setText("Последняя неделя");

        binding.toolbarMain.setNavigationOnClickListener(v -> {
            binding.drawerMain.openDrawer(GravityCompat.START);
        });

        binding.ibGoals.setOnClickListener(v -> navigateTo("GoalsActivity"));
        binding.ibTips.setOnClickListener(v -> navigateTo("TipsActivity"));
        binding.ibIncome.setOnClickListener(v -> navigateTo("IncomeActivity"));
        binding.ibExpense.setOnClickListener(v -> navigateTo("ExpenseActivity"));
        binding.ibMainTip.setOnClickListener(v -> navigateTo("TipsActivityWithSelect"));

        binding.ibMainGoal.setOnClickListener(v -> {
            if (mainGoal.equals(new Goal())) {
                navigateTo("GoalsActivity");
            } else {
                navigateTo("GoalsActivityWithSelect");
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                navigateTo("MainActivity");
            } else if (item.getItemId() == R.id.menu_tips) {
                navigateTo("TipsActivity");
            } else if (item.getItemId() == R.id.menu_income) {
                navigateTo("IncomeActivity");
            } else if (item.getItemId() == R.id.menu_expense) {
                navigateTo("ExpenseActivity");
            } else if (item.getItemId() == R.id.menu_goals) {
                navigateTo("GoalsActivity");
            } else if (item.getItemId() == R.id.menu_exit) {
                navigateTo("AuthActivity");
            }
            return true;
        });
    }

    @SuppressLint("SetTextI18n")
    private void observeViewModel() {
        viewModel.getBalance().observe(this, balance -> {
            binding.tvBalance.setText(String.valueOf(balance));
        });

        viewModel.getOneRandomGoal().observe(this, goal -> {
            binding.tvTitleGoal.setText(goal.getTitleOfGoal());
            if (goal.getDate().isEmpty()) {
                binding.tvMoneyGoal.setText("");
                binding.tvProgressMoney.setText("");
                binding.tvDate.setText("");
            } else {
                mainGoal = goal;
                binding.tvMoneyGoal.setText(String.valueOf(goal.getMoneyGoal()));
                binding.tvProgressMoney.setText(goal.getProgressOfMoneyGoal() + " из " + goal.getMoneyGoal());
                binding.tvDate.setText(goal.getDate());
                float totalAmountToSave = goal.getMoneyGoal();
                float currentAmountSaved = goal.getProgressOfMoneyGoal();
                float progress = (currentAmountSaved / totalAmountToSave * 100);
                binding.progressBar.setProgress((int) progress);

                viewModel.getOneRandomTip().observe(this, tip -> {
                    binding.tvTitleTip.setText(tip.getTitle());
                    binding.tvAdvice.setText(tip.getText());
                    mainTip = tip;
                });

                viewModel.getSumIncome().observe(this, sumIncome -> {
                    binding.tvBalanceIncome.setText("+ " + String.format("%.2f", sumIncome));
                });

                viewModel.getSumExpense().observe(this, sumExpense -> {
                    binding.tvBalanceExpense.setText("- " + String.format("%.2f", sumExpense));
                });

            }
        });
    }

    private void navigateTo(String activityName) {
        Intent intent;
        switch (activityName) {
            case "GoalsActivity":
                intent = new Intent(this, GoalsActivity.class);
                startActivity(intent);
                break;
            case "GoalsActivityWithSelect":
                intent = new Intent(this, GoalsActivity.class);
                intent.putExtra("selectedItem", mainGoal.getTitleOfGoal());
                startActivity(intent);
                break;
            case "TipsActivity":
                intent = new Intent(this, TipsActivity.class);
                startActivity(intent);
                break;
            case "TipsActivityWithSelect":
                intent = new Intent(this, TipsActivity.class);
                intent.putExtra("selectedItem", mainTip.getTitle());
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
            default:
                break;
        }
    }

}
