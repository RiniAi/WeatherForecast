package com.example.weatherforecast.base;

public interface BasePresenter<T> {
    void setView(T view);
    void start();
}
