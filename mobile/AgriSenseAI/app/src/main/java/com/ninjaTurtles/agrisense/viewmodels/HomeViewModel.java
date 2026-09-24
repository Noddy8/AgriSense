package com.ninjaTurtles.agrisense.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ninjaTurtles.agrisense.data.AgriRepository;
import com.ninjaTurtles.agrisense.models.IrrigationStatus;
import com.ninjaTurtles.agrisense.models.Recommendation;
import com.ninjaTurtles.agrisense.models.SensorData;
import com.ninjaTurtles.agrisense.models.WeatherInfo;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private AgriRepository repository;

    public HomeViewModel() {
        repository = AgriRepository.getInstance();
    }

    public LiveData<SensorData> getSensorData() {
        return repository.getSensorData();
    }

    public LiveData<WeatherInfo> getWeatherInfo() {
        return repository.getWeather();
    }

    public LiveData<List<Recommendation>> getRecommendations() {
        return repository.getRecommendations();
    }

    public LiveData<IrrigationStatus> getIrrigationStatus() {
        return repository.getIrrigationStatus();
    }
}
