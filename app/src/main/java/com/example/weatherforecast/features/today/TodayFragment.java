package com.example.weatherforecast.features.today;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.example.weatherforecast.App;
import com.example.weatherforecast.R;
import com.example.weatherforecast.databinding.FragmentTodayBinding;
import com.example.weatherforecast.models.Current;
import com.example.weatherforecast.models.Forecast;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

public class TodayFragment extends Fragment {
    private FragmentTodayBinding binding;
    private ViewPager viewPager;

    @Inject
    TodayHourlyAdapter hourlyAdapter;
    @Inject
    TodayDailyAdapter dailyAdapter;

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
        App.getAppComponent().activityComponent().inject(this);

        viewPager = getActivity().findViewById(R.id.fragment_container);
        binding.cvHourly.setOnClickListener(view -> viewPager.setCurrentItem(1));
        binding.cvDaily.setOnClickListener(view -> viewPager.setCurrentItem(2));

        loadList();
        return binding.getRoot();
    }

    private void loadList() {
        Forecast forecast = (Forecast) getArguments().getSerializable("forecast");
        Current current = forecast.getCurrent();
        binding.tvTemperature.setText(getString(R.string.temperature, (int) current.getTemp()));
        binding.tvDescriptionWeather.setText(current.getWeather().get(0).getDescription());
        binding.tvTemperatureFeelsLike.setText(getString(R.string.temperature_feels_like, (int) current.getFeelsLike()));
        binding.tvWindSpeed.setText(getString(R.string.wind_speed, (int) current.getWindSpeed()));
        binding.tvClouds.setText(getString(R.string.clouds, current.getClouds()));
        binding.tvHumidity.setText(getString(R.string.humidity, current.getHumidity()));
        binding.tvPressure.setText(getString(R.string.pressure, current.getPressure()));
        binding.tvSunrise.setText(new SimpleDateFormat("HH:mm").format(new Date(current.getSunrise() * 1000L)));
        binding.tvSunset.setText(new SimpleDateFormat("HH:mm").format(new Date(current.getSunset() * 1000L)));
        binding.tvUvi.setText(String.valueOf(current.getUvi()));
        selectImage(current);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvHourlyForecast.setLayoutManager(layoutManager);
        binding.rvHourlyForecast.setAdapter(hourlyAdapter);
        hourlyAdapter.setContext(getContext());
        hourlyAdapter.setList(forecast.getTodayHourly());
        hourlyAdapter.setOnItemClickListener((hourly) -> viewPager.setCurrentItem(1));

        LinearLayoutManager nextLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvDailyForecast.setLayoutManager(nextLayoutManager);
        binding.rvDailyForecast.setAdapter(dailyAdapter);
        hourlyAdapter.setContext(getContext());
        dailyAdapter.setList(forecast.getTodayDaily());
        dailyAdapter.setOnItemClickListener((daily) -> viewPager.setCurrentItem(2));
    }

    private void selectImage(Current current) {
        int time = Integer.parseInt(new SimpleDateFormat("HH").format(new Date(current.getDate() * 1000L)));
        switch (current.getWeather().get(0).getId()) {
            case 200:
            case 201:
            case 202:
            case 210:
            case 211:
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
                binding.ivImageWeather.setImageResource(R.drawable.thunderstorm);
                break;
            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:
            case 520:
            case 521:
            case 522:
            case 531:
                binding.ivImageWeather.setImageResource(R.drawable.shower_rain);
                break;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
                if (time >= 6 && time <= 18) {
                    binding.ivImageWeather.setImageResource(R.drawable.d_rain);
                } else {
                    binding.ivImageWeather.setImageResource(R.drawable.n_rain);
                }
                break;
            case 511:
            case 600:
            case 601:
            case 602:
            case 611:
            case 612:
            case 613:
            case 615:
            case 616:
            case 620:
            case 621:
            case 622:
                binding.ivImageWeather.setImageResource(R.drawable.show);
                break;
            case 701:
            case 711:
            case 721:
            case 731:
            case 741:
            case 751:
            case 761:
            case 762:
            case 771:
            case 781:
                binding.ivImageWeather.setImageResource(R.drawable.mist);
                break;
            case 800:
                if (time >= 6 && time <= 18) {
                    binding.ivImageWeather.setImageResource(R.drawable.d_clear_sky);
                } else {
                    binding.ivImageWeather.setImageResource(R.drawable.n_clear_sky);
                }
                break;
            case 801:
                if (time >= 6 && time <= 18) {
                    binding.ivImageWeather.setImageResource(R.drawable.d_few_clouds);
                } else {
                    binding.ivImageWeather.setImageResource(R.drawable.n_few_clouds);
                }
                break;
            case 803:
            case 804:
                binding.ivImageWeather.setImageResource(R.drawable.broken_clouds);
                break;
            case 802:
            default:
                binding.ivImageWeather.setImageResource(R.drawable.scattered_clouds);
        }
    }
}
