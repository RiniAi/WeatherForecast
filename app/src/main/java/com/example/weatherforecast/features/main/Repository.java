package com.example.weatherforecast.features.main;

import com.example.weatherforecast.models.Forecast;

import io.reactivex.rxjava3.core.Single;

public interface Repository {
    Single<Forecast> getForecast(double lat, double lon);
}
