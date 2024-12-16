package com.example.finapp.viewModel;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.finapp.model.Cost;
import com.example.finapp.model.Goal;
import com.example.finapp.model.Tip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataRepository {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    // Получение ID текущего пользователя
    private final String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MutableLiveData<Double> getUserBalance() {
        MutableLiveData<Double> balance = new MutableLiveData<>();
        DatabaseReference balanceRef = database.getReference("users").child(userId).child("balance");

        executorService.execute(() -> {
            balanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Double newBalance = snapshot.getValue(Double.class);
                    if (newBalance == null) {
                        newBalance = 0.0;
                        updateUserBalance(0.0); // Обновление баланса пользователя
                    }
                    balance.postValue(newBalance); // Обновляем LiveData из фонового потока
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    balance.postValue(0.0); // Устанавливаем значение по умолчанию при ошибке
                }
            });
        });

        return balance;
    }

    void updateUserBalance(double balance) {
        executorService.execute(() -> {
            DatabaseReference userBalanceRef = database.getReference("users").child(userId).child("balance");
            userBalanceRef.setValue(balance);
        });
    }

    public MutableLiveData<List<Cost>> getIncomes() {
        MutableLiveData<List<Cost>> incomeLiveData = new MutableLiveData<>();

        executorService.execute(() -> {
            DatabaseReference incomeRef = database.getReference("users").child(userId).child("income");
            incomeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Cost> items = new ArrayList<>();
                    for (DataSnapshot incomeSnapshot : snapshot.getChildren()) {
                        Cost income = incomeSnapshot.getValue(Cost.class);
                        if (income != null) {
                            items.add(income);
                        }
                    }
                    incomeLiveData.postValue(items); // Используем postValue для обновления LiveData из фонового потока
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("Ошибка", "Ошибка при чтении данных из базы данных");
                }
            });
        });

        return incomeLiveData;
    }

    public MutableLiveData<List<Cost>> getExpenses() {
        MutableLiveData<List<Cost>> expenseLiveData = new MutableLiveData<>();

        executorService.execute(() -> {
            DatabaseReference expenseRef = database.getReference("users").child(userId).child("expense");
            expenseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Cost> items = new ArrayList<>();
                    for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                        Cost expenses = expenseSnapshot.getValue(Cost.class);
                        if (expenses != null) {
                            items.add(expenses);
                        }
                    }
                    expenseLiveData.postValue(items); // Используем postValue для обновления LiveData из фонового потока
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("Ошибка", "Ошибка при чтении данных из базы данных");
                }
            });
        });

        return expenseLiveData;
    }

    public MutableLiveData<List<Goal>> getGoals() {
        MutableLiveData<List<Goal>> financeGoalsLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            DatabaseReference goalsRef = database.getReference("users").child(userId).child("goals");
            goalsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Goal> items = new ArrayList<>();
                    for (DataSnapshot goalSnapshot : snapshot.getChildren()) {
                        Goal goal = goalSnapshot.getValue(Goal.class);
                        if (goal != null) {
                            items.add(goal);
                        }
                    }
                    financeGoalsLiveData.postValue(items); // Обновляем LiveData из фонового потока
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("Ошибка", "Ошибка при чтении данных из базы данных");
                }
            });
        });
        return financeGoalsLiveData;
    }

    public MutableLiveData<Goal> getOneGoal() {
        MutableLiveData<Goal> oneGoalLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            DatabaseReference goalsRef = database.getReference("users").child(userId).child("goals");
            goalsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Goal> goalActiveList = new ArrayList<>();
                    for (DataSnapshot goalSnapshot : snapshot.getChildren()) {
                        Goal goal = goalSnapshot.getValue(Goal.class);
                        if (goal != null && goal.getStatus().equals("Active")) {
                            goalActiveList.add(goal);
                        }
                    }
                    if (goalActiveList.isEmpty()) {
                        oneGoalLiveData.postValue(new Goal("0", "Добавьте цель", 0L, 0L, "", "", "", ""));
                    } else {
                        Random random = new Random();
                        oneGoalLiveData.postValue(goalActiveList.get(random.nextInt(goalActiveList.size())));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("Ошибка", "Ошибка при чтении данных из базы данных");
                }
            });
        });
        return oneGoalLiveData;
    }

    // Обновление дохода в базе
    public void editIncomeToBase(Map<String, Object> newIncome, Cost selectIncome) {
        executorService.execute(() -> {
            DatabaseReference incomeRef = database.getReference("users").child(userId).child("income").child(selectIncome.getCostId());
            incomeRef.updateChildren(newIncome);
        });
    }

    // Удаление дохода
    public void deleteIncome(Cost selectIncome) {
        if (selectIncome != null) {
            executorService.execute(() -> {
                database.getReference("users").child(userId)
                        .child("income")
                        .child(selectIncome.getCostId())
                        .removeValue();
            });
        }
    }

    // Запись данных о расходах
    public void writeExpenseData(Cost newCost) {
        executorService.execute(() -> {
            DatabaseReference costRef = database.getReference("users").child(userId).child("expense").push();
            String costId = costRef.getKey();
            newCost.setCostId(costId);
            costRef.setValue(newCost);
        });
    }

    // Обновление расхода в базе
    public void editExpenseToBase(Map<String, Object> newExpense, Cost selectExpense) {
        executorService.execute(() -> {
            DatabaseReference expenseRef = database.getReference("users").child(userId).child("expense").child(selectExpense.getCostId());
            expenseRef.updateChildren(newExpense)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FinancialRepository", "Данные обновлены");
                    })
                    .addOnFailureListener(e -> {
                        Log.d("FinancialRepository", "Ошибка обновления данных", e);
                    });
        });
    }

    // Удаление расхода
    public void deleteExpense(Cost selectExpense) {
        if (selectExpense != null) {
            executorService.execute(() -> {
                database.getReference("users").child(userId)
                        .child("expense")
                        .child(selectExpense.getCostId())
                        .removeValue();
            });
        }
    }

    // Запись данных о цели
    public void writeGoalData(Goal newGoal) {
        executorService.execute(() -> {
            DatabaseReference goalsRef = database.getReference("users").child(userId).child("goals").push();
            String goalId = goalsRef.getKey();
            newGoal.setGoalId(goalId);
            goalsRef.setValue(newGoal);
        });
    }

    // Обновление цели в базе
    public void editGoalToBase(Map<String, Object> newGoal, Goal selectGoal) {
        executorService.execute(() -> {
            DatabaseReference goalRef = database.getReference("users").child(userId).child("goals").child(selectGoal.getGoalId());
            goalRef.updateChildren(newGoal);
        });
    }

    // Удаление цели
    public void deleteGoal(Goal selectGoal) {
        if (selectGoal != null) {
            executorService.execute(() -> {
                database.getReference("users").child(userId)
                        .child("goals")
                        .child(selectGoal.getGoalId())
                        .removeValue();
            });
        }
    }

    // Получение одного совета
    public MutableLiveData<Tip> getOneTip(String categoryTip) {
        MutableLiveData<Tip> oneTipLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            DatabaseReference tipsRef = database.getReference().child("tips");
            tipsRef.addValueEventListener(new ValueEventListener() {
                @Override

                public void onDataChange(DataSnapshot snapshot) {
                    List<Tip> items = new ArrayList<>();
                    for (DataSnapshot tipSnapshot : snapshot.getChildren()) {
                        Tip tip = tipSnapshot.getValue(Tip.class);
                        if (tip != null && (categoryTip.equals(tip.getTheme()) || categoryTip.equals("all"))) {
                            items.add(tip);
                        }
                    }

                    if (!items.isEmpty()) {
                        Random random = new Random();
                        oneTipLiveData.postValue(items.get(random.nextInt(items.size())));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("Ошибка", "Ошибка при чтении данных из базы данных");
                }
            });
        });

        return oneTipLiveData;
    }

    // Запись данных о доходах
    public void writeIncomeData(Cost newCost) {
        executorService.execute(() -> {
            DatabaseReference costRef = database.getReference("users").child(userId).child("income").push();
            String costId = costRef.getKey();
            newCost.setCostId(costId);
            costRef.setValue(newCost);
        });
    }

    public MutableLiveData<List<Tip>> getFinancialAdvices() {
        MutableLiveData<List<Tip>> tipLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            DatabaseReference tipsRef = database.getReference().child("tips");
            tipsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Tip> items = new ArrayList<>();
                    for (DataSnapshot tipSnapshot : snapshot.getChildren()) {
                        Tip tip = tipSnapshot.getValue(Tip.class);
                        items.add(tip);
                    }
                    tipLiveData.postValue(items);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("Ошибка", "Ошибка при чтении данных из базы данных");
                }
            });
        });

        return tipLiveData;
    }
}