package com.example.weatherforecast.features.weatherForToday;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherforecast.databinding.FragmentTodayBinding;

public class TodayFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentTodayBinding binding = FragmentTodayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
