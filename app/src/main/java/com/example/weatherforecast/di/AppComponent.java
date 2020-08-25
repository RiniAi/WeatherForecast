package com.example.weatherforecast.di;

import dagger.Component;

@Component(modules = {
        AppModule.class,
        ServiceModule.class,
        ViewModelModule.class,
        ForecastRepositoryModule.class
})

public interface AppComponent {
    ActivityComponent activityComponent();
}
