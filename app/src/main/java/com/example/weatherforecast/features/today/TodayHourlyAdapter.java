package com.example.weatherforecast.features.today;

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

public class TodayHourlyAdapter extends BaseAdapter<Hourly, TodayHourlyAdapter.HourlyForecastViewHolder> {

    public TodayHourlyAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public HourlyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.lists_today_hourly_item, parent, false);
        return new HourlyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyForecastViewHolder holder, int position) {
        Hourly hourly = getItem(position);
        if (hourly == null) {
            return;
        }
        holder.date.setText(String.valueOf(hourly.getDate()));
        Picasso.get().load(R.drawable.d_clear_sky).into(holder.image);
        holder.temperature.setText(String.valueOf(hourly.getTemp()));
    }

    class HourlyForecastViewHolder extends BaseViewHolder {
        TextView date;
        ImageView image;
        TextView temperature;

        HourlyForecastViewHolder(@NonNull View view) {
            super(view);
            date = view.findViewById(R.id.tv_date);
            image = view.findViewById(R.id.iv_image_weather);
            temperature = view.findViewById(R.id.tv_temperature);
        }
    }
}
