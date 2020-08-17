package com.example.weatherforecast.network;

import com.example.weatherforecast.models.Forecast;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapApiService {
    /**
     * Request a weather forecast from the OpenWeatherMap API using the received coordinates
     * (User enters name of city, and we use GeoCoder to enter the coordinates of this city. This is done because the API only works with coordinates)
     *
     * @param lat using "GeoCoder", we get latitude coordinates of city entered by user
     * @param lon using "GeoCoder", we get longitude coordinates of city entered by user
     * @return weather data at the specified coordinates, including hourly and weekly forecasts
     */

    @GET("onecall?units=metric&appid=719394f46a7c5afb89a3aa717085e890&")
    Single<Forecast> getForecast(@Query("lat") double lat, @Query("lon") double lon);
}
