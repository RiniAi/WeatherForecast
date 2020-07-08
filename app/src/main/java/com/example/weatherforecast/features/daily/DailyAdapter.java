package com.example.weatherforecast.features.daily;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.weatherforecast.R;
import com.example.weatherforecast.base.BaseAdapter;
import com.example.weatherforecast.base.BaseViewHolder;
import com.example.weatherforecast.models.Daily;
import com.squareup.picasso.Picasso;

public class DailyAdapter extends BaseAdapter<Daily, DailyAdapter.DailyViewHolder> {

    public DailyAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.lists_daily_item, parent, false);
        return new DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder holder, int position) {
        Daily daily = getItem(position);
        if (daily == null) {
            return;
        }
        holder.date.setText(String.valueOf(daily.getDate()));
        holder.temperature.setText(String.valueOf(daily.getTemp().get(0).getMax()));
        holder.description.setText(daily.getWeather().get(0).getDescription());
        holder.wind.setText(String.valueOf(daily.getWindSpeed()));
        Picasso.get().load(R.drawable.n_few_clouds).into(holder.image);
        holder.clouds.setText(String.valueOf(daily.getClouds()));
        holder.humidity.setText(String.valueOf(daily.getHumidity()));
        holder.pressure.setText(String.valueOf(daily.getPressure()));
    }

    class DailyViewHolder extends BaseViewHolder {
        TextView date;
        TextView temperature;
        TextView description;
        TextView wind;
        ImageView image;
        TextView clouds;
        TextView humidity;
        TextView pressure;

        DailyViewHolder(@NonNull View view) {
            super(view);
            date = view.findViewById(R.id.tv_date);
            temperature = view.findViewById(R.id.tv_temperature);
            description = view.findViewById(R.id.tv_description_weather);
            wind = view.findViewById(R.id.tv_wind_speed);
            image = view.findViewById(R.id.iv_image_weather);
            clouds = view.findViewById(R.id.tv_clouds);
            humidity = view.findViewById(R.id.tv_humidity);
            pressure = view.findViewById(R.id.tv_pressure);
        }
    }
}
