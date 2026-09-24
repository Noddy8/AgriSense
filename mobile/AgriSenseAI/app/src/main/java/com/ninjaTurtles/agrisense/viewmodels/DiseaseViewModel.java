package com.ninjaTurtles.agrisense.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ninjaTurtles.agrisense.data.AgriRepository;
import com.ninjaTurtles.agrisense.models.DiseaseResult;

public class DiseaseViewModel extends ViewModel {

    private AgriRepository repository;
    private MutableLiveData<Boolean> isAnalyzingLiveData = new MutableLiveData<>(false);
    private MutableLiveData<DiseaseResult> resultLiveData = new MutableLiveData<>(null);

    public DiseaseViewModel() {
        repository = AgriRepository.getInstance();
    }

    public LiveData<Boolean> getIsAnalyzing() {
        return isAnalyzingLiveData;
    }

    public LiveData<DiseaseResult> getDiseaseResult() {
        return resultLiveData;
    }

    public void analyzeImage() {
        isAnalyzingLiveData.setValue(true);
        resultLiveData.setValue(null);

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isAnalyzingLiveData.setValue(false);
                resultLiveData.setValue(repository.analyzeLeafImage());
            }
        }, 2500); // 2.5s scanning simulation
    }
}
