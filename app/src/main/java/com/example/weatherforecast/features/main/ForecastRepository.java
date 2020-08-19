package com.example.weatherforecast.features.main;

import com.example.weatherforecast.models.Forecast;
import com.example.weatherforecast.network.OpenWeatherMapApiService;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class ForecastRepository implements Repository {
    @Inject
    OpenWeatherMapApiService service;

    @Inject
    public ForecastRepository(OpenWeatherMapApiService service) {
        this.service = service;
    }

    @Override
    public Single<Forecast> getForecast(double lat, double lon) {
        return service.getForecast(lat, lon);
    }
}
