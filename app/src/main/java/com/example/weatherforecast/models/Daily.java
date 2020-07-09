package com.example.weatherforecast.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Daily implements Serializable {
    private int dt;
    private Temp temp;
    private int pressure;
    private int humidity;
    @SerializedName("wind_speed")
    private float windSpeed;
    private List<Weather> weather;
    private int clouds;
    private float uvi;

    public int getDate() {
        return dt;
    }

    public void setDate(int dt) {
        this.dt = dt;
    }

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
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

    public int getClouds() {
        return clouds;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public float getUvi() {
        return uvi;
    }

    public void setUvi(float uvi) {
        this.uvi = uvi;
    }
}
