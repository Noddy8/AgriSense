package com.ninjaTurtles.agrisense.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ninjaTurtles.agrisense.data.AgriRepository;
import com.ninjaTurtles.agrisense.models.AlertNotification;

import java.util.List;

public class AlertsViewModel extends ViewModel {

    private AgriRepository repository;

    public AlertsViewModel() {
        repository = AgriRepository.getInstance();
    }

    public LiveData<List<AlertNotification>> getAlerts() {
        return repository.getAlerts();
    }

    public void markAsRead(AlertNotification alert) {
        if (alert != null) {
            alert.setUnread(false);
        }
    }
}
