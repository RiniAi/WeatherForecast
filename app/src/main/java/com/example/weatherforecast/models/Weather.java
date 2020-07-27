package com.example.weatherforecast.models;

import java.io.Serializable;

public class Weather implements Serializable {
    private String description;
    private int id;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
