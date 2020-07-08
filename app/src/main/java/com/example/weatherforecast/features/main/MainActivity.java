package com.example.weatherforecast.features.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.weatherforecast.databinding.ActivityMainBinding;
import com.example.weatherforecast.features.FragmentPageAdapter;
import com.example.weatherforecast.features.daily.DailyFragment;
import com.example.weatherforecast.features.hourly.HourlyFragment;
import com.example.weatherforecast.features.today.TodayFragment;
import com.example.weatherforecast.models.Current;
import com.example.weatherforecast.models.Daily;
import com.example.weatherforecast.models.Forecast;
import com.example.weatherforecast.models.Hourly;
import com.example.weatherforecast.models.Temp;
import com.example.weatherforecast.models.Weather;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private TabLayout navigation;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarMainActivity.toolbar);
        initNavigation();
        loadFragment();
    }

    private void initNavigation() {
        navigation = binding.navigation;
        pager = binding.fragmentContainer;
    }

    private void loadFragment() {
        Temp temp = new Temp();
        temp.setMax(4);
        temp.setMin(4);

        List<Temp> tempList = new ArrayList<>();
        tempList.add(temp);

        Weather weather = new Weather();
        weather.setDescription("sunny");

        List<Weather> weatherToday = new ArrayList<>();
        weatherToday.add(weather);

        Daily daily = new Daily();
        daily.setDate(10000);
        daily.setTemp(tempList);
        daily.setPressure(44);
        daily.setHumidity(43);
        daily.setWindSpeed(43);
        daily.setWeather(weatherToday);
        daily.setClouds(43);

        List<Daily> dailyList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dailyList.add(daily);
        }

        Hourly hourly = new Hourly();
        hourly.setDate(10000);
        hourly.setTemp(45);
        hourly.setClouds(43);
        hourly.setWindSpeed(43);
        hourly.setWeather(weatherToday);

        List<Hourly> hourlyList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            hourlyList.add(hourly);
        }

        Current current = new Current();
        current.setDate(1000093);
        current.setSunrise(11);
        current.setSunset(111);
        current.setTemp(23);
        current.setFeelsLike(24);
        current.setPressure(34);
        current.setHumidity(45);
        current.setUvi(34);
        current.setClouds(43);
        current.setWindSpeed(12);
        current.setWeather(weatherToday);

        Forecast forecast = new Forecast();
        forecast.setLat(4);
        forecast.setLon(4);
        forecast.setCurrent(current);
        forecast.setHourly(hourlyList);
        forecast.setDaily(dailyList);

        FragmentPageAdapter adapter = new FragmentPageAdapter(getSupportFragmentManager());
        adapter.addFragment(TodayFragment.newInstance(forecast), "Today");
        adapter.addFragment(HourlyFragment.newInstance(hourlyList), "Hourly");
        adapter.addFragment(DailyFragment.newInstance(dailyList), "Daily");

        pager.setAdapter(adapter);
        navigation.setupWithViewPager(pager);
    }
}
