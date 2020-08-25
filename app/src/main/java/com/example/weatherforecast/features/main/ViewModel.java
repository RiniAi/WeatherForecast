package com.example.weatherforecast.features.main;

import androidx.lifecycle.LiveData;

import com.example.weatherforecast.models.Forecast;

public interface ViewModel {
    LiveData<String> getToastObserver();

    LiveData<Forecast> getForecast();

    void permissionDenied(boolean b);

    void start();

    void swipeRefresh();

    void unsubscribe();

    void startSearchByGps();

    void startSearchByQuery(String s);

    void requestPermissionsResult(int requestCode, int[] grantResults);
}
