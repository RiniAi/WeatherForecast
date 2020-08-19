package com.example.weatherforecast.di;

import com.example.weatherforecast.features.daily.DailyFragment;
import com.example.weatherforecast.features.hourly.HourlyFragment;
import com.example.weatherforecast.features.main.MainActivity;
import com.example.weatherforecast.features.today.TodayFragment;

import dagger.Subcomponent;

@Subcomponent
public interface ActivityComponent {
    void inject(MainActivity activity);

    void inject(TodayFragment fragment);

    void inject(HourlyFragment fragment);

    void inject(DailyFragment fragment);
}
