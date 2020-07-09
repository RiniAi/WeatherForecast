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
import com.example.weatherforecast.models.Daily;
import com.squareup.picasso.Picasso;

public class TodayDailyAdapter extends BaseAdapter<Daily, TodayDailyAdapter.HourlyForecastViewHolder> {

    public TodayDailyAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public HourlyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.lists_today_daily_item, parent, false);
        return new HourlyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyForecastViewHolder holder, int position) {
        Daily daily = getItem(position);
        if (daily == null) {
            return;
        }
        holder.date.setText(String.valueOf(daily.getDate()));
        Picasso.get().load(R.drawable.n_few_clouds).into(holder.image);
        holder.temperature.setText(String.valueOf(daily.getTemp().getMax()));
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
