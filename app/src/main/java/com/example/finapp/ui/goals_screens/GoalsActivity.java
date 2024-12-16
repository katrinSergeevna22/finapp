package com.example.finapp.ui.goals_screens;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.finapp.R;
import com.example.finapp.databinding.ActivityGoalsBinding;
import com.example.finapp.model.Goal;
import com.example.finapp.model.Tip;
import com.example.finapp.ui.AuthActivity;
import com.example.finapp.ui.BackgroundFragment;
import com.example.finapp.ui.MainActivity;
import com.example.finapp.ui.costs_screens.ExpensesActivity;
import com.example.finapp.ui.costs_screens.IncomeActivity;
import com.example.finapp.ui.goals_screens.adapter_goals.GoalAdapter;
import com.example.finapp.ui.tips_screens.TipsActivity;
import com.example.finapp.viewModel.AuthViewModel;
import com.example.finapp.viewModel.GoalViewModel;

import java.util.Objects;


public class GoalsActivity extends AppCompatActivity {
    private ActivityGoalsBinding binding;
    private GoalViewModel viewModel;
    private GoalAdapter adapter;
    private final BackgroundFragment backgroundFragment = BackgroundFragment.newInstance();
    private final InfoGoalFragment infoGoalFragment = InfoGoalFragment.newInstance();
    private final AddGoalFragment newAddGoalFragment = AddGoalFragment.newInstance();
    private Tip mainTip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(GoalViewModel.class);

        viewModel.loadGoal();
        viewModel.loadGoalByCategory();
        viewModel.loadOneRandomTipLiveData();

        setupUI();
        observeViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupUI();
        observeViewModel();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUI() {
        binding.toolbarGoal.setNavigationOnClickListener(v -> binding.drawerGoal.openDrawer(GravityCompat.START));

        adapter = new GoalAdapter(goal -> {
            viewModel.setSelectedGoal(goal);
            viewModel.fragmentIsOpen = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.backgroundFragment, backgroundFragment)
                    .addToBackStack(null)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.place_holder_infoFragment, infoGoalFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.rcView.setLayoutManager(new LinearLayoutManager(this));
        binding.rcView.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.shape_indent_rcview)));
        binding.rcView.addItemDecoration(itemDecoration);

        binding.ibAddGoal.setOnClickListener(v -> {
            viewModel.fragmentIsOpen = true;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.backgroundFragment, backgroundFragment)
                    .addToBackStack(null)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.place_holder_addFragment, newAddGoalFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.svTextAdvice.setOnClickListener(v -> navigateTo("TipsActivity"));
        binding.ibMainTip.setOnClickListener(v -> navigateTo("TipsActivityWithSelect"));

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                navigateTo("MainActivity");
            } else if (item.getItemId() == R.id.menu_tips) {
                navigateTo("TipsActivity");
            } else if (item.getItemId() == R.id.menu_income) {
                navigateTo("IncomeActivity");
            } else if (item.getItemId() == R.id.menu_expense) {
                navigateTo("ExpenseActivity");
            } else if (item.getItemId() == R.id.menu_exit) {
                navigateTo("AuthActivity");
            }
            return true;
        });

        binding.toolbarGoal.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.item_active) {
                viewModel.setSelectedCategory("Active");
            } else if (item.getItemId() == R.id.item_achieved) {
                viewModel.setSelectedCategory("Achieved");
            } else if (item.getItemId() == R.id.item_deleted) {
                viewModel.setSelectedCategory("Deleted");
            }
            return true;
        });
    }

    public void closeFragments() {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().popBackStack();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void observeViewModel() {
        viewModel.getGoalsCategoryLiveData().observe(this, goalList -> {
            binding.tvCount.setText(String.valueOf(goalList.size()));
            binding.tvTitleGoals.setText(viewModel.getTitleOfCategory());
            adapter.submitList(goalList);

            String selectedItem = getIntent().getStringExtra("selectedItem");
            int selectPosition = 0;
            for (int index = 0; index < goalList.size(); index++) {
                Goal item = goalList.get(index);
                if (item.getTitleOfGoal().equals(selectedItem)) {
                    selectPosition = index;
                    break;
                }
            }
            Objects.requireNonNull(binding.rcView.getLayoutManager()).scrollToPosition(selectPosition);

            adapter.notifyDataSetChanged();
        });

        viewModel.getOneRandomTipLiveData().observe(this, tip -> {
            mainTip = tip;
            binding.tvTitleTip.setText(tip.getTitle());
            binding.tvAdvice.setText(tip.getText());
        });
    }

    public void navigateTo(String nameActivity) {
        Intent intent;
        switch (nameActivity) {
            case "MainActivity":
                intent = new Intent(GoalsActivity.this, MainActivity.class);
                startActivity(intent);
                break;

            case "TipsActivity":
                intent = new Intent(GoalsActivity.this, TipsActivity.class);
                startActivity(intent);
                break;

            case "TipsActivityWithSelect":
                intent = new Intent(GoalsActivity.this, TipsActivity.class);
                intent.putExtra("selectedItem", mainTip.getTitle());
                startActivity(intent);
                break;

            case "IncomeActivity":
                intent = new Intent(GoalsActivity.this, IncomeActivity.class);
                startActivity(intent);
                break;

            case "ExpenseActivity":
                intent = new Intent(GoalsActivity.this, ExpensesActivity.class);
                startActivity(intent);
                break;

            case "AuthActivity":
                AuthViewModel authViewModel = new AuthViewModel();
                authViewModel.exit();
                intent = new Intent(GoalsActivity.this, AuthActivity.class);
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
