package com.example.weatherforecast.features.dailyForecast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherforecast.databinding.FragmentDailyForecastBinding;

public class DailyForecastFragment extends Fragment {
    private FragmentDailyForecastBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDailyForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
