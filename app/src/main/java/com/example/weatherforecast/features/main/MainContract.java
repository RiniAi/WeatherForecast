package com.example.weatherforecast.features.main;

import com.example.weatherforecast.base.BasePresenter;
import com.example.weatherforecast.base.BaseView;
import com.example.weatherforecast.models.Forecast;

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void showError();

        void showForecast(Forecast forecast);

        void hideProgressBarAndViewForecast();

        void setCityNameForToolbarTitle(String city, String area);

        void setDefaultToolbarTitle();

        void checkGps();

        void permissionDenied();
    }

    interface Presenter extends BasePresenter {
        void getForecastViaGps();

        void getForecastViaQuery(String locationName);

        void unsubscribe();

        void requestPermissionsResult(int requestCode, int[] grantResults);
    }
}
