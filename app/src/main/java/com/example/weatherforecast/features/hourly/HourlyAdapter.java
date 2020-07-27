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

import java.text.SimpleDateFormat;
import java.util.Date;

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
        holder.temperature.setText(getContext().getString(R.string.hourly_temperature, (int) hourly.getTemp()));
        holder.time.setText(new SimpleDateFormat("HH:mm").format(new Date(hourly.getDate() * 1000L)));
        holder.date.setText(new SimpleDateFormat("dd, E").format(new Date(hourly.getDate() * 1000L)));
        holder.description.setText(hourly.getWeather().get(0).getDescription());
        holder.wind.setText(getContext().getString(R.string.hourly_wind_speed, (int) hourly.getWindSpeed()));
        holder.clouds.setText(getContext().getString(R.string.clouds, (int) hourly.getClouds()));
        if (hourly.getClouds() < 50) {
            Picasso.get().load(R.drawable.not_drop).into(holder.cloudsImage);
        } else {
            Picasso.get().load(R.drawable.drop).into(holder.cloudsImage);
        }
        selectImage(hourly, holder);
    }

    private void selectImage(Hourly hourly, HourlyViewHolder holder) {
        int time = Integer.parseInt(new SimpleDateFormat("HH").format(new Date(hourly.getDate() * 1000L)));
        switch (hourly.getWeather().get(0).getId()) {
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

    class HourlyViewHolder extends BaseViewHolder {
        TextView time;
        TextView date;
        ImageView image;
        TextView description;
        TextView wind;
        TextView temperature;
        TextView clouds;
        ImageView cloudsImage;

        HourlyViewHolder(@NonNull View view) {
            super(view);
            time = view.findViewById(R.id.tv_time);
            date = view.findViewById(R.id.tv_date);
            image = view.findViewById(R.id.iv_image_weather);
            description = view.findViewById(R.id.tv_description_weather);
            wind = view.findViewById(R.id.tv_wind_speed);
            temperature = view.findViewById(R.id.tv_temperature);
            clouds = view.findViewById(R.id.tv_clouds);
            cloudsImage = view.findViewById(R.id.iv_clouds);
        }
    }
}
