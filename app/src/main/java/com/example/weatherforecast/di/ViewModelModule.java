package com.example.weatherforecast.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.weatherforecast.features.main.MainViewModel;
import com.example.weatherforecast.features.main.ViewModel;
import com.example.weatherforecast.usecases.RequestForecastUseCase;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModelModule {
    @Provides
    ViewModel providesViewModel(Context context, SharedPreferences sharedPreferences, RequestForecastUseCase requestForecastUseCase) {
        return new MainViewModel(context,sharedPreferences, requestForecastUseCase);
    }
}
