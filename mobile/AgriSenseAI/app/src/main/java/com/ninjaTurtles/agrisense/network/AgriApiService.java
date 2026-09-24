package com.ninjaTurtles.agrisense.network;

import com.ninjaTurtles.agrisense.models.AlertNotification;
import com.ninjaTurtles.agrisense.models.DiseaseResult;
import com.ninjaTurtles.agrisense.models.Farm;
import com.ninjaTurtles.agrisense.models.IrrigationStatus;
import com.ninjaTurtles.agrisense.models.Recommendation;
import com.ninjaTurtles.agrisense.models.SensorData;
import com.ninjaTurtles.agrisense.models.WeatherInfo;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AgriApiService {

    @GET("sensors/current")
    Call<SensorData> getCurrentSensorData();

    @GET("weather/current")
    Call<WeatherInfo> getWeatherInfo();

    @GET("farms")
    Call<List<Farm>> getFarms();

    @GET("recommendations")
    Call<List<Recommendation>> getRecommendations();

    @GET("alerts")
    Call<List<AlertNotification>> getAlerts();

    @POST("irrigation/toggle")
    Call<IrrigationStatus> toggleIrrigation(@Body IrrigationStatus status);

    @POST("disease/analyze")
    Call<DiseaseResult> analyzePlantDisease(@Body String imageBase64);
}
