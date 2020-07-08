package com.example.weatherforecast.features.today;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weatherforecast.databinding.FragmentTodayBinding;
import com.example.weatherforecast.models.Current;
import com.example.weatherforecast.models.Forecast;

public class TodayFragment extends Fragment {
    private FragmentTodayBinding binding;

    public static TodayFragment newInstance(Forecast forecast) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("forecast", forecast);
        TodayFragment fragment = new TodayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTodayBinding.inflate(inflater, container, false);
        loadList();
        return binding.getRoot();
    }

    private void loadList() {
        Forecast forecast = (Forecast) getArguments().getSerializable("forecast");
        Current current = forecast.getCurrent();
        binding.tvTemperature.setText(String.valueOf(current.getTemp()));
        binding.tvDescriptionWeather.setText(current.getWeather().get(0).getDescription());
        binding.tvTemperatureFeelsLike.setText(String.valueOf(current.getFeelsLike()));
        binding.tvWindSpeed.setText(String.valueOf(current.getWindSpeed()));
        binding.tvClouds.setText(String.valueOf(current.getClouds()));
        binding.tvHumidity.setText(String.valueOf(current.getHumidity()));
        binding.tvSunrise.setText(String.valueOf(current.getSunrise()));
        binding.tvUvi.setText(String.valueOf(current.getUvi()));

        TodayHourlyAdapter hourlyAdapter = new TodayHourlyAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvHourlyForecast.setLayoutManager(layoutManager);
        binding.rvHourlyForecast.setAdapter(hourlyAdapter);
        hourlyAdapter.setList(forecast.getHourly());

        TodayDailyAdapter dailyAdapter = new TodayDailyAdapter(getActivity());
        LinearLayoutManager nextLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvDailyForecast.setLayoutManager(nextLayoutManager);
        binding.rvDailyForecast.setAdapter(dailyAdapter);
        dailyAdapter.setList(forecast.getDaily());
    }
}
