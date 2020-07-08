package com.example.weatherforecast.features.hourly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weatherforecast.databinding.FragmentHourlyBinding;
import com.example.weatherforecast.models.Hourly;

import java.util.ArrayList;
import java.util.List;

public class HourlyFragment extends Fragment {
    private FragmentHourlyBinding binding;

    public static HourlyFragment newInstance(List<Hourly> hourlyList) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("hourly", (ArrayList<Hourly>) hourlyList);
        HourlyFragment fragment = new HourlyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHourlyBinding.inflate(inflater, container, false);
        loadList();
        return binding.getRoot();
    }

    private void loadList() {
        List<Hourly> hourlyList = (ArrayList<Hourly>) getArguments().getSerializable("hourly");
        HourlyAdapter hourlyAdapter = new HourlyAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.rvHourlyForecast.setLayoutManager(layoutManager);
        binding.rvHourlyForecast.setAdapter(hourlyAdapter);
        hourlyAdapter.setList(hourlyList);
    }
}
