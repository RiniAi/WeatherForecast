package com.example.weatherforecast.features.main;

import com.example.weatherforecast.base.BasePresenter;
import com.example.weatherforecast.base.BaseView;
import com.example.weatherforecast.models.Forecast;

public interface MainContract {
    interface View extends BaseView {
        void showForecast(Forecast forecast);

        void showProgressBar();

        void showFragmentContainer();

        void showEmptyView();

        void permissionDeniedLastQuery();

        void showError();

        void hideProgressBar();

        void setCityNameForToolbarTitle(String city, String area);

        void checkGpsEnabled();

        void checkGpsEnabledRoQuery();

        void checkInternetConnection();

        void nothingNotFound();

        void permissionDenied();
    }

    interface Presenter extends BasePresenter<View> {
        void unsubscribe();

        boolean isGpsAvailable();

        boolean isInternetAvailable();

        void getForecastViaGps();

        void requestPermissionsResult(int requestCode, int[] grantResults);

        void checkLastQuery();

        void getForecastViaQuery(String locationName);
    }
}
