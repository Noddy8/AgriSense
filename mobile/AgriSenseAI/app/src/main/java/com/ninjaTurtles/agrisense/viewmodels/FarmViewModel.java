package com.ninjaTurtles.agrisense.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ninjaTurtles.agrisense.data.AgriRepository;
import com.ninjaTurtles.agrisense.models.Farm;

import java.util.List;

public class FarmViewModel extends ViewModel {

    private AgriRepository repository;

    public FarmViewModel() {
        repository = AgriRepository.getInstance();
    }

    public LiveData<List<Farm>> getFarms() {
        return repository.getFarms();
    }
}
