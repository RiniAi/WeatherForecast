package com.example.weatherforecast.di;

import com.example.weatherforecast.features.main.ForecastRepository;
import com.example.weatherforecast.features.main.Repository;
import com.example.weatherforecast.network.OpenWeatherMapApiService;

import dagger.Module;
import dagger.Provides;

@Module
public class ForecastRepositoryModule {
    @Provides
    public Repository providesForecastRepository(OpenWeatherMapApiService service) {
        return new ForecastRepository(service);
    }
}
