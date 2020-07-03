package com.example.weatherforecast.features.precipitation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherforecast.databinding.FragmentPrecipitationBinding;

public class PrecipitationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentPrecipitationBinding binding = FragmentPrecipitationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
