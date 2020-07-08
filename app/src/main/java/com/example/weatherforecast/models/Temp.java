package com.example.weatherforecast.models;

import java.io.Serializable;

public class Temp implements Serializable {
    private float min;
    private float max;

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }
}
