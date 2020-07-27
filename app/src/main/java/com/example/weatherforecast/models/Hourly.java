package com.example.weatherforecast.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Hourly implements Serializable {
    private int dt;
    private float temp;
    private int humidity;
    @SerializedName("wind_speed")
    private float windSpeed;
    private List<Weather> weather;

    public int getDate() {
        return dt;
    }

    public void setDate(int dt) {
        this.dt = dt;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public int getClouds() {
        return humidity;
    }

    public void setClouds(int humidity) {
        this.humidity = humidity;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
}
