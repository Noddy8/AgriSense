package com.ninjaTurtles.agrisense.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ninjaTurtles.agrisense.data.AgriRepository;
import com.ninjaTurtles.agrisense.models.IrrigationStatus;
import com.ninjaTurtles.agrisense.models.SensorData;

public class IrrigationViewModel extends ViewModel {

    private AgriRepository repository;

    public IrrigationViewModel() {
        repository = AgriRepository.getInstance();
    }

    public LiveData<IrrigationStatus> getIrrigationStatus() {
        return repository.getIrrigationStatus();
    }

    public LiveData<SensorData> getSensorData() {
        return repository.getSensorData();
    }

    public void startIrrigation() {
        repository.updatePumpState(IrrigationStatus.PumpState.ON);
    }

    public void stopIrrigation() {
        repository.updatePumpState(IrrigationStatus.PumpState.OFF);
    }
}
