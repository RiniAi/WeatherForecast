package com.example.weatherforecast.features.main;

import com.example.weatherforecast.models.Forecast;
import com.example.weatherforecast.network.OpenWeatherMapApiService;
import com.example.weatherforecast.network.RetrofitClientInstance;

import io.reactivex.rxjava3.core.Single;

class ForecastRepository implements Repository {
    @Override
    public Single<Forecast> getForecast(double lat, double lon) {
        OpenWeatherMapApiService apiService = RetrofitClientInstance.getRetrofitInstance().create(OpenWeatherMapApiService.class);
        return apiService.getForecast(lat, lon);
    }
}
