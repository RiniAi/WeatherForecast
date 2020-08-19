package com.example.weatherforecast.usecases;

import com.example.weatherforecast.features.main.Repository;
import com.example.weatherforecast.models.Forecast;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class RequestForecastUseCase {
    @Inject
    Repository repository;

    @Inject
    public RequestForecastUseCase() {
    }

    public Single<Forecast> run(Params params) {
        return repository.getForecast(params.getLat(), params.getLon());
    }

    public static class Params {
        double lat;
        double lon;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public Params(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Params params = (Params) o;

            if (Double.compare(params.lat, lat) != 0) return false;
            return Double.compare(params.lon, lon) == 0;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(lat);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(lon);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }
}
