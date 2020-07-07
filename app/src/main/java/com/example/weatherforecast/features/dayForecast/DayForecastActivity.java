package com.example.weatherforecast.features.dayForecast;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.weatherforecast.databinding.ActivityDayBinding;
import com.example.weatherforecast.features.FragmentPageAdapter;
import com.google.android.material.tabs.TabLayout;

public class DayForecastActivity extends AppCompatActivity {
    private ActivityDayBinding binding;
    private TabLayout navigation;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarDayActivity.toolbar);
        initNavigation();
        loadFragment();
    }

    private void initNavigation() {
        navigation = binding.navigation;
        pager = binding.fragmentContainer;
    }

    private void loadFragment() {
        FragmentPageAdapter adapter = new FragmentPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new DayForecastFragment(), "MONDAY");
        adapter.addFragment(new DayForecastFragment(), "TUESDAY");
        adapter.addFragment(new DayForecastFragment(), "WEDNESDAY");
        adapter.addFragment(new DayForecastFragment(), "THURSDAY");
        adapter.addFragment(new DayForecastFragment(), "FRIDAY");
        adapter.addFragment(new DayForecastFragment(), "SATURDAY");
        adapter.addFragment(new DayForecastFragment(), "SUNDAY");
        pager.setAdapter(adapter);
        navigation.setupWithViewPager(pager);
    }
}
