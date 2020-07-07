package com.example.weatherforecast.features.dayForecast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherforecast.databinding.FragmentDayForecastBinding;

public class DayForecastFragment extends Fragment {
    private FragmentDayForecastBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDayForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
