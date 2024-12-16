package com.example.finapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.finapp.model.Cost;
import com.example.finapp.model.Goal;
import com.example.finapp.model.Tip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainViewModel extends ViewModel {
    private final DataRepository dataRepository = new DataRepository();
    private final MutableLiveData<Double> balanceLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> sumIncomeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> sumExpenseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Goal> goalLiveData = new MutableLiveData<>();
    private final MutableLiveData<Tip> tipLiveData = new MutableLiveData<>();

    public LiveData<Double> getBalance() {
        dataRepository.getUserBalance().observeForever(balance -> {
            if (balance == null) {
                balanceLiveData.setValue(0.0);
            } else {
                balanceLiveData.setValue(balance);
            }
        });
        return balanceLiveData;
    }

    public LiveData<Goal> getOneRandomGoal() {
        LiveData<Goal> oneRandomGoalLiveData = dataRepository.getOneGoal();
        oneRandomGoalLiveData.observeForever(goalLiveData::setValue);
        return goalLiveData;
    }

    public LiveData<Tip> getOneRandomTip() {
        LiveData<Tip> oneRandomTipLiveData = dataRepository.getOneTip("all");
        oneRandomTipLiveData.observeForever(tipLiveData::setValue);
        return tipLiveData;
    }

    public MutableLiveData<Double> getSumIncome() {
        MutableLiveData<List<Cost>> incomeLiveData = dataRepository.getIncomes();

        incomeLiveData.observeForever(new Observer<List<Cost>>() {
            @Override
            public void onChanged(List<Cost> listCost) {
                double balanceIncome = 0.0;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date currentDate = Calendar.getInstance().getTime();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                Date lastWeekDate = calendar.getTime();

                if (listCost != null && !listCost.isEmpty()) {
                    for (Cost income : listCost) {
                        try {
                            Date incomeDate = sdf.parse(income.getDate());
                            if (incomeDate != null && incomeDate.compareTo(lastWeekDate) >= 0 && incomeDate.compareTo(currentDate) <= 0) {
                                balanceIncome += income.getMoneyCost();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                sumIncomeLiveData.setValue(balanceIncome);
            }
        });

        return sumIncomeLiveData;
    }


    public MutableLiveData<Double> getSumExpense() {
        MutableLiveData<List<Cost>> expenseLiveData = dataRepository.getExpenses();

        expenseLiveData.observeForever(new Observer<List<Cost>>() {
            @Override
            public void onChanged(List<Cost> listCost) {
                double balanceExpense = 0.0;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date currentDate = Calendar.getInstance().getTime();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                Date lastWeekDate = calendar.getTime();

                for (Cost expense : listCost) {
                    try {
                        Date expenseDate = sdf.parse(expense.getDate());
                        if (expenseDate != null && expenseDate.compareTo(lastWeekDate) >= 0 && expenseDate.compareTo(currentDate) <= 0) {
                            balanceExpense += expense.getMoneyCost();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                sumExpenseLiveData.setValue(balanceExpense);
            }
        });

        return sumExpenseLiveData;
    }
}


