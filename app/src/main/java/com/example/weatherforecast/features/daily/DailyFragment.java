package com.example.weatherforecast.features.daily;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weatherforecast.databinding.FragmentDailyBinding;
import com.example.weatherforecast.models.Daily;

import java.util.ArrayList;
import java.util.List;

public class DailyFragment extends Fragment {
    private FragmentDailyBinding binding;

    public static DailyFragment newInstance(List<Daily> dailyList) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("daily", (ArrayList<Daily>) dailyList);
        DailyFragment fragment = new DailyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDailyBinding.inflate(inflater, container, false);
        loadList();
        return binding.getRoot();
    }

    private void loadList() {
        List<Daily> dailyList = (ArrayList<Daily>) getArguments().getSerializable("daily");
        DailyAdapter dailyAdapter = new DailyAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.rvDailyForecast.setLayoutManager(layoutManager);
        binding.rvDailyForecast.setAdapter(dailyAdapter);
        dailyAdapter.setList(dailyList);
    }
}
