package com.example.finapp.ui.costs_screens;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.finapp.R;
import com.example.finapp.databinding.ActivityIncomeBinding;
import com.example.finapp.model.Cost;
import com.example.finapp.model.Goal;
import com.example.finapp.model.Tip;
import com.example.finapp.ui.AuthActivity;
import com.example.finapp.ui.BackgroundFragment;
import com.example.finapp.ui.MainActivity;
import com.example.finapp.ui.tips_screens.TipsActivity;
import com.example.finapp.ui.costs_screens.adapter_cost.CostAdapter;
import com.example.finapp.ui.goals_screens.GoalsActivity;
import com.example.finapp.viewModel.AuthViewModel;
import com.example.finapp.viewModel.CostViewModel;

import java.util.List;
import java.util.Objects;

public class IncomeActivity extends AppCompatActivity {

    private ActivityIncomeBinding binding;
    private CostViewModel viewModel;
    private CostAdapter adapter;

    private final BackgroundFragment newBackgroundFragment = BackgroundFragment.newInstance();
    private final InfoIncomeFragment infoIncomeFragment = InfoIncomeFragment.newInstance();
    private final AddIncomeFragment newAddIncomeFragment = AddIncomeFragment.newInstance();
    private Tip mainTip = new Tip();
    private Goal mainGoal = new Goal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CostViewModel.class);
        loadData();

        setupUI();
        observeViewModel();
    }

    private void loadData() {
        viewModel.loadIncomes();
        viewModel.loadOneRandomTipLiveData("Income");
        viewModel.loadOneRandomGoalLiveData();
    }

    private void setupUI() {
        binding.toolbarGoal.setNavigationOnClickListener(v ->
                binding.drawerGoal.openDrawer(GravityCompat.START)
        );

        adapter = new CostAdapter(cost -> {
            viewModel.setSelectedCost(cost);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.backgroundFragment, newBackgroundFragment)
                    .addToBackStack(null)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.place_holder_infoIncomeFragment, infoIncomeFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.rcView.setLayoutManager(new LinearLayoutManager(this));
        binding.rcView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.shape_indent_rcview)));
        binding.rcView.addItemDecoration(itemDecoration);

        binding.ibAddIncome.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.backgroundFragment, newBackgroundFragment)
                    .addToBackStack(null)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.place_holder_addIncomeFragment, newAddIncomeFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.ibMainGoal.setOnClickListener(v -> navigateTo("GoalsActivityWithSelect"));
        binding.ibMainTip.setOnClickListener(v -> navigateTo("TipsActivityWithSelect"));

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_goals) {
                navigateTo("GoalsActivity");
            } else if (item.getItemId() == R.id.menu_tips) {
                navigateTo("TipsActivity");
            } else if (item.getItemId() == R.id.menu_home) {
                navigateTo("MainActivity");
            } else if (item.getItemId() == R.id.menu_expense) {
                navigateTo("ExpenseActivity");
            } else if (item.getItemId() == R.id.menu_income) {
                navigateTo("IncomeActivity");
            } else if (item.getItemId() == R.id.menu_exit) {
                navigateTo("AuthActivity");
            }
            return true;
        });
    }

    private void observeViewModel() {
        viewModel.balanceLiveData.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double balance) {
                binding.tvBalanceIncome.setText(balance.toString());
            }
        });

        viewModel.getIncomeLiveData().observe(this, new Observer<List<Cost>>() {
            @Override
            public void onChanged(List<Cost> incomeList) {
                adapter.submitList(incomeList);
                adapter.notifyDataSetChanged();
            }
        });

        viewModel.getOneRandomGoalLiveData().observe(this, new Observer<Goal>() {
            @Override
            public void onChanged(Goal goal) {
                mainGoal = goal;
                binding.tvTitleGoal.setText(goal.getTitleOfGoal());
                if (goal.getDate().isEmpty()) {
                    binding.tvMoneyGoal.setText("");
                } else {
                    binding.tvMoneyGoal.setText(String.valueOf(goal.getMoneyGoal()));
                    double totalAmountToSave = goal.getMoneyGoal();
                    double currentAmountSaved = goal.getProgressOfMoneyGoal();

                    int progress = (int) ((currentAmountSaved / totalAmountToSave) * 100);
                    binding.progressBar.setProgress(progress);
                }
            }
        });

        viewModel.getOneRandomTipLiveData().observe(this, new Observer<Tip>() {
            @Override
            public void onChanged(Tip tip) {
                mainTip = tip;
                binding.tvTitleTip.setText(tip.getTitle());
            }
        });
    }

    public void closeFragments() {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().popBackStack();
    }

    private void navigateTo(String nameActivity) {
        Intent intent;
        switch (nameActivity) {
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
