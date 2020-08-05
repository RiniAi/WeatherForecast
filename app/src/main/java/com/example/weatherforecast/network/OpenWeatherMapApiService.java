package com.example.weatherforecast.network;

import com.example.weatherforecast.models.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapApiService {
    @GET("onecall?units=metric&appid=719394f46a7c5afb89a3aa717085e890&")
    Call<Forecast> getForecast(@Query("lat") double lat, @Query("lon") double lon);
}
