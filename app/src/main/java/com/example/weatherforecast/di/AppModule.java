package com.example.weatherforecast.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.weatherforecast.App;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    public App providesApp() {
        return app;
    }

    @Provides
    public Context providesContext() {
        return app.getApplicationContext();
    }

    @Provides
    public SharedPreferences providesSharedPreference() {
        return app.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
    }
}
