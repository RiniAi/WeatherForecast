package com.example.weatherforecast.features.hourly;

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
import com.example.weatherforecast.models.Hourly;
import com.squareup.picasso.Picasso;

public class HourlyAdapter extends BaseAdapter<Hourly, HourlyAdapter.HourlyViewHolder> {

    public HourlyAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.lists_hourly_item, parent, false);
        return new HourlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        Hourly hourly = getItem(position);
        if (hourly == null) {
            return;
        }
        holder.date.setText(String.valueOf(hourly.getDate()));
        Picasso.get().load(R.drawable.n_few_clouds).into(holder.image);
        holder.description.setText(hourly.getWeather().get(0).getDescription());
        holder.wind.setText(String.valueOf(hourly.getWindSpeed()));
        holder.temperature.setText(String.valueOf(hourly.getTemp()));
        holder.clouds.setText(String.valueOf(hourly.getClouds()));
    }

    class HourlyViewHolder extends BaseViewHolder {
        TextView date;
        ImageView image;
        TextView description;
        TextView wind;
        TextView temperature;
        TextView clouds;

        HourlyViewHolder(@NonNull View view) {
            super(view);
            date = view.findViewById(R.id.tv_date);
            image = view.findViewById(R.id.iv_image_weather);
            description = view.findViewById(R.id.tv_description_weather);
            wind = view.findViewById(R.id.tv_wind_speed);
            temperature = view.findViewById(R.id.tv_temperature);
            clouds = view.findViewById(R.id.tv_clouds);
        }
    }
}
