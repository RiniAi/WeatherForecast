package com.example.weatherforecast;

import android.app.Application;

import com.example.weatherforecast.di.AppComponent;
import com.example.weatherforecast.di.AppModule;
import com.example.weatherforecast.di.DaggerAppComponent;

// TODO: Don't forget to include it in your manifest! (android:name=".App")
public class App extends Application {
    private static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(App.this))
                .build();
    }
}
