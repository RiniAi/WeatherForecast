package com.example.weatherforecast.features.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.weatherforecast.databinding.ActivityMainBinding;
import com.example.weatherforecast.features.dailyForecast.DailyForecastFragment;
import com.example.weatherforecast.features.hourlyForecast.HourlyForecastFragment;
import com.example.weatherforecast.features.precipitationForecast.PrecipitationForecastFragment;
import com.example.weatherforecast.features.todayForecast.TodayForecastFragment;
import com.google.android.material.tabs.TabLayout;

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
        MainFragmentPageAdapter adapter = new MainFragmentPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new TodayForecastFragment(), "Today");
        adapter.addFragment(new DailyForecastFragment(), "Daily");
        adapter.addFragment(new HourlyForecastFragment(), "Hourly");
        adapter.addFragment(new PrecipitationForecastFragment(), "Precipitation");
        pager.setAdapter(adapter);
        navigation.setupWithViewPager(pager);
    }
}
