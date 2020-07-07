package com.example.weatherforecast.features.hourlyForecast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherforecast.databinding.FragmentHourlyForecastBinding;

public class HourlyForecastFragment extends Fragment {
    private FragmentHourlyForecastBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHourlyForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}