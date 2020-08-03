package com.example.weatherforecast.features.today;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.base.BaseAdapter;
import com.example.weatherforecast.base.BaseViewHolder;
import com.example.weatherforecast.models.Daily;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TodayDailyAdapter extends BaseAdapter<Daily, TodayDailyAdapter.HourlyForecastViewHolder> {
    private OnItemTodayDailyClickListener onClickListener;

    public TodayDailyAdapter(Context context) {
        super(context);
    }

    public void setOnItemClickListener(OnItemTodayDailyClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public HourlyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.lists_today_daily_item, parent, false);
        return new HourlyForecastViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyForecastViewHolder holder, int position) {
        Daily daily = getItem(position);
        if (daily == null) {
            return;
        }
        holder.date.setText(new SimpleDateFormat("E").format(new Date(daily.getDate() * 1000L)));
        holder.temperature.setText(getContext().getString(R.string.temperature, (int) daily.getTemp().getMax()));
        selectImage(daily, holder);
    }

    private void selectImage(Daily daily, HourlyForecastViewHolder holder) {
        int time = Integer.parseInt(new SimpleDateFormat("HH").format(new Date(daily.getDate() * 1000L)));
        switch (daily.getWeather().get(0).getId()) {
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
                holder.image.setImageResource(R.drawable.thunderstorm);
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
                holder.image.setImageResource(R.drawable.shower_rain);
                break;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
                if (time >= 6 && time <= 18) {
                    holder.image.setImageResource(R.drawable.d_rain);
                } else {
                    holder.image.setImageResource(R.drawable.n_rain);
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
                holder.image.setImageResource(R.drawable.show);
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
                holder.image.setImageResource(R.drawable.mist);
                break;
            case 800:
                if (time >= 6 && time <= 18) {
                    holder.image.setImageResource(R.drawable.d_clear_sky);
                } else {
                    holder.image.setImageResource(R.drawable.n_clear_sky);
                }
                break;
            case 801:
                if (time >= 6 && time <= 18) {
                    holder.image.setImageResource(R.drawable.d_few_clouds);
                } else {
                    holder.image.setImageResource(R.drawable.n_few_clouds);
                }
                break;
            case 803:
            case 804:
                holder.image.setImageResource(R.drawable.broken_clouds);
                break;
            case 802:
            default:
                holder.image.setImageResource(R.drawable.scattered_clouds);
        }
    }

    class HourlyForecastViewHolder extends BaseViewHolder {
        TextView date;
        ImageView image;
        TextView temperature;

        HourlyForecastViewHolder(@NonNull View view, OnItemTodayDailyClickListener onClickListener) {
            super(view);
            date = view.findViewById(R.id.tv_date);
            image = view.findViewById(R.id.iv_image_weather);
            temperature = view.findViewById(R.id.tv_temperature);
            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                    onClickListener.onItemClick(getItem(position));
                }
            });
        }
    }
}
