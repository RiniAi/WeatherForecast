package com.example.weatherforecast.di;

import com.example.weatherforecast.network.OpenWeatherMapApiService;
import com.example.weatherforecast.network.RetrofitClientInstance;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {
    @Provides
    public OpenWeatherMapApiService providesOpenWeatherApiService() {
        return RetrofitClientInstance.getRetrofitInstance().create(OpenWeatherMapApiService.class);
    }
}
