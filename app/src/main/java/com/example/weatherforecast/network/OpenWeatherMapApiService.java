package com.example.weatherforecast.network;

import com.example.weatherforecast.models.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;

public interface OpenWeatherMapApiService {
    @GET("onecall?lat=47.2362&lon=38.8969&units=metric&appid=719394f46a7c5afb89a3aa717085e890")
    Call<Forecast> getForecast();
}
