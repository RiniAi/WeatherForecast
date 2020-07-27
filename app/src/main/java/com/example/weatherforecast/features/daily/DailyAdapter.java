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

import java.text.SimpleDateFormat;
import java.util.Date;

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
        holder.date.setText(new SimpleDateFormat("E dd/MM/yyyy").format(new Date(daily.getDate() * 1000L)));
        holder.temperature.setText(getContext().getString(R.string.temperature, (int) daily.getTemp().getMax()));
        holder.description.setText(daily.getWeather().get(0).getDescription());
        holder.wind.setText(getContext().getString(R.string.wind_speed, (int) daily.getWindSpeed()));
        holder.clouds.setText(getContext().getString(R.string.clouds, (int) daily.getClouds()));
        holder.humidity.setText(getContext().getString(R.string.humidity, (int) daily.getHumidity()));
        holder.pressure.setText(getContext().getString(R.string.pressure, (int) daily.getPressure()));
        holder.uvi.setText(String.valueOf(daily.getUvi()));
        selectImage(daily, holder);
    }

    private void selectImage(Daily daily, DailyViewHolder holder) {
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
                Picasso.get().load(R.drawable.thunderstorm).into(holder.image);
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
                Picasso.get().load(R.drawable.shower_rain).into(holder.image);
                break;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
                if (time >= 6 && time <= 18) {
                    Picasso.get().load(R.drawable.d_rain).into(holder.image);
                } else {
                    Picasso.get().load(R.drawable.n_rain).into(holder.image);
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
                Picasso.get().load(R.drawable.show).into(holder.image);
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
                Picasso.get().load(R.drawable.mist).into(holder.image);
                break;
            case 800:
                if (time >= 6 && time <= 18) {
                    Picasso.get().load(R.drawable.d_clear_sky).into(holder.image);
                } else {
                    Picasso.get().load(R.drawable.n_clear_sky).into(holder.image);
                }
                break;
            case 801:
                if (time >= 6 && time <= 18) {
                    Picasso.get().load(R.drawable.d_few_clouds).into(holder.image);
                } else {
                    Picasso.get().load(R.drawable.n_few_clouds).into(holder.image);
                }
                break;
            case 803:
                Picasso.get().load(R.drawable.broken_clouds).into(holder.image);
                break;
            case 804:
                Picasso.get().load(R.drawable.overcast_clouds).into(holder.image);
                break;
            case 802:
            default:
                Picasso.get().load(R.drawable.scattered_clouds).into(holder.image);
        }
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
        TextView uvi;

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
            uvi = view.findViewById(R.id.tv_uvi);
        }
    }
}
