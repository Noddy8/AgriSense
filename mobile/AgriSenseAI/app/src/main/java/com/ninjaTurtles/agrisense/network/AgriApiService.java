package com.ninjaTurtles.agrisense.network;

import com.ninjaTurtles.agrisense.models.CropPredictionResponse;
import com.ninjaTurtles.agrisense.models.AccountBootstrapRequest;
import com.ninjaTurtles.agrisense.models.DashboardResponse;
import com.ninjaTurtles.agrisense.models.FertilizerOptions;
import com.ninjaTurtles.agrisense.models.FertilizerPredictionRequest;
import com.ninjaTurtles.agrisense.models.FertilizerPredictionResponse;
import com.ninjaTurtles.agrisense.models.Farm;
import com.ninjaTurtles.agrisense.models.SensorReading;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AgriApiService {
    @POST("account/bootstrap") Call<com.google.gson.JsonObject> bootstrapAccount(@Body AccountBootstrapRequest request);
    @Headers("Accept: application/json")
    @GET("farms") Call<List<Farm>> getFarms();

    @Headers("Accept: application/json")
    @GET("farms/{farmId}/dashboard") Call<DashboardResponse> getDashboard(@Path("farmId") int farmId);

    @Headers("Accept: application/json")
    @GET("sensors/latest/{farmId}") Call<SensorReading> getLatestReading(@Path("farmId") int farmId);

    @Headers("Accept: application/json")
    @GET("sensors/history/{farmId}") Call<List<SensorReading>> getSensorHistory(@Path("farmId") int farmId, @Query("limit") int limit);

    @Headers("Accept: application/json")
    @POST("sensors/simulation/{farmId}") Call<SensorReading> applyScenario(@Path("farmId") int farmId, @Query("scenario") String scenario);

    @Headers("Accept: application/json")
    @GET("recommendations/crop/{farmId}") Call<CropPredictionResponse> getCropRecommendation(@Path("farmId") int farmId);

    @Headers("Accept: application/json")
    @GET("recommendations/fertilizer/options") Call<FertilizerOptions> getFertilizerOptions();

    @Headers("Accept: application/json")
    @POST("recommendations/fertilizer") Call<FertilizerPredictionResponse> getFertilizerRecommendation(@Body FertilizerPredictionRequest request);
}
