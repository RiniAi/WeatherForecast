package com.example.weatherforecast.features.main;

import com.example.weatherforecast.base.BasePresenter;
import com.example.weatherforecast.base.BaseView;
import com.example.weatherforecast.models.Forecast;

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void showForecast(Forecast forecast);

        void showProgressBar();

        void setCityNameForToolbarTitle(String city, String area);

        void setDefaultToolbarTitle();

        void hideProgressBarAndViewForecast();

        void checkGps();

        void permissionDenied();

        void showError();
    }

    interface Presenter extends BasePresenter {
        void unsubscribe();

        void getForecastViaGps();

        void requestPermissionsResult(int requestCode, int[] grantResults);

        void getForecastViaQuery(String locationName);
    }
}
