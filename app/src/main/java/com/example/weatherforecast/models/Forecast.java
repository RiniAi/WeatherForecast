package com.example.weatherforecast.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Forecast implements Serializable {
    private float lat;
    private float lon;
    private Current current;
    private List<Hourly> hourly;
    private List<Daily> daily;

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public List<Hourly> getHourly() {
        return hourly;
    }

    public List<Hourly> getTodayHourly() {
        List<Hourly> todayHourly = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            todayHourly.add(hourly.get(i));
        }
        return todayHourly;
    }

    public void setHourly(List<Hourly> hourly) {
        this.hourly = hourly;
    }

    public List<Daily> getDaily() {
        return daily;
    }

    public List<Daily> getTodayDaily() {
        List<Daily> dailyHourly = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            dailyHourly.add(daily.get(i));
        }
        return dailyHourly;
    }

    public void setDaily(List<Daily> daily) {
        this.daily = daily;
    }
}
