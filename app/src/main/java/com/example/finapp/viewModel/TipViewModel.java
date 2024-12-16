package com.example.finapp.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finapp.model.Tip;

import java.util.ArrayList;
import java.util.List;

public class TipViewModel extends ViewModel {
    private final DataRepository dataRepository = new DataRepository();
    private final List<Tip> tipsList = new ArrayList<>();
    private final MutableLiveData<List<Tip>> tipsLiveData = new MutableLiveData<>();

    public LiveData<List<Tip>> getTipsLiveData() {
        return tipsLiveData;
    }

    public void loadFinancialAdvices() {
        dataRepository.getFinancialAdvices().observeForever(tips -> {
            tipsList.clear();
            tipsList.addAll(tips);
            tipsLiveData.setValue(tipsList);
        });
    }
}
