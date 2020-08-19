package com.example.weatherforecast.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.weatherforecast.features.main.MainContract;
import com.example.weatherforecast.features.main.MainPresenter;
import com.example.weatherforecast.usecases.RequestForecastUseCase;

import dagger.Module;
import dagger.Provides;

@Module
public class PresenterModule {
    @Provides
    MainContract.Presenter providesMainPresenter(Context context, SharedPreferences sharedPreferences, RequestForecastUseCase requestForecastUseCase) {
        return new MainPresenter(context,sharedPreferences, requestForecastUseCase);
    }
}
