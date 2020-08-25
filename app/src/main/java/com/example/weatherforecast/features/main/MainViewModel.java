package com.example.weatherforecast.features.main;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weatherforecast.R;
import com.example.weatherforecast.models.Forecast;
import com.example.weatherforecast.usecases.RequestForecastUseCase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel implements ViewModel {
    public final MutableLiveData<Forecast> forecastLive = new MutableLiveData<>();
    public final MutableLiveData<String> toastMessageObserver = new MutableLiveData<>();
    public final ObservableField<String> locality = new ObservableField<>();
    public final ObservableBoolean isDataLoading = new ObservableBoolean();
    public final ObservableBoolean isEmpty = new ObservableBoolean();

    public static final String STORAGE_LOCATION = "00/00";
    private boolean isPermissionGps = false;
    private String[] locationMemory;
    private int counter = 0;

    private FusedLocationProviderClient fusedLocationClient;
    private CompositeDisposable disposables;
    private Geocoder geocoder;

    @Inject
    Context context;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    RequestForecastUseCase requestForecastUseCase;

    @Inject
    public MainViewModel(Context context, SharedPreferences sharedPreferences, RequestForecastUseCase requestForecastUseCase) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.disposables = new CompositeDisposable();
        this.requestForecastUseCase = requestForecastUseCase;
        this.geocoder = new Geocoder(context, Locale.getDefault());
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public LiveData<String> getToastObserver() {
        return toastMessageObserver;
    }

    @Override
    public LiveData<Forecast> getForecast() {
        return forecastLive;
    }

    @Override
    public void permissionDenied(boolean b) {
        isPermissionGps = b;
    }

    @Override
    public void start() {
        isDataLoading.set(true);
        isEmpty.set(true);
        Log.i("WeatherForecast", "Data Loading: " + isDataLoading.get());
        locality.set(context.getString(R.string.app_name));
        locationMemory = sharedPreferences.getString(STORAGE_LOCATION, "").split("/");
        Log.i("WeatherForecast", "Location in memory: " + Arrays.toString(locationMemory));
        if (isInternetAvailable() & isGpsAvailable()) {
            getForecastViaGps();
        } else if (!isInternetAvailable()) {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_check_internet_connection));
            isDataLoading.set(false);
        } else if (!isGpsAvailable() & !Arrays.toString(locationMemory).equals("[]")) {
            loadForecast(Double.parseDouble(locationMemory[0]), Double.parseDouble(locationMemory[1]));
        } else if (!isGpsAvailable() & Arrays.toString(locationMemory).equals("[]")) {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_check_gps_enabled_or_query));
            isDataLoading.set(false);
        }
    }

    @Override
    public void startSearchByGps() {
        isDataLoading.set(true);
        locationMemory = sharedPreferences.getString(STORAGE_LOCATION, "").split("/");
        Log.i("WeatherForecast", "Location in memory: " + Arrays.toString(locationMemory));
        if (isInternetAvailable() & isGpsAvailable()) {
            getForecastViaGps();
        } else if (!isInternetAvailable()) {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_check_internet_connection));
            isDataLoading.set(false);
        } else if (!isGpsAvailable()) {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_check_gps_enabled));
            isDataLoading.set(false);
        } else {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_try_later));
            isDataLoading.set(false);
        }
    }

    @Override
    public void startSearchByQuery(String s) {
        isDataLoading.set(true);
        if (isInternetAvailable()) {
            getForecastViaQuery(s);
        } else {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_check_internet_connection));
            isDataLoading.set(false);
        }
    }

    @Override
    public void swipeRefresh() {
        locationMemory = sharedPreferences.getString(STORAGE_LOCATION, "").split("/");
        if (isInternetAvailable() && !Arrays.toString(locationMemory).equals("[]")) {
            loadForecast(Double.parseDouble(locationMemory[0]), Double.parseDouble(locationMemory[1]));
        } else if (!isInternetAvailable()) {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_check_internet_connection));
            isDataLoading.set(false);
        } else {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_refresh_start));
            isDataLoading.set(false);
        }
    }

    @Override
    public void unsubscribe() {
        disposables.clear();
    }

    private boolean isGpsAvailable() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void getForecastViaGps() {
        isDataLoading.set(true);
        if (!isPermissionGps) {
            checkPermission();
            fusedLocationClient.getLastLocation().addOnSuccessListener(this::getLtdLng);
            Log.i("WeatherForecast", "GetForecastViaGps: Permission to determine the location of the received");
        }
    }

    // Requesting permission if it was not received before
    @Override
    public void requestPermissionsResult(int requestCode, int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this::getLtdLng);
                } else {
                    checkLastQuery();
                }
                break;
            }
        }
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    private void checkLastQuery() {
        if (Arrays.toString(locationMemory).equals("[]")) {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_permission_denied));
            isDataLoading.set(false);
            Log.e("WeatherForecast", "GetForecastViaGps: Permission to determine the location was not received");
        } else {
            loadForecast(Double.parseDouble(locationMemory[0]), Double.parseDouble(locationMemory[1]));
            toastMessageObserver.setValue(context.getString(R.string.main_activity_permission_denied_last_query));
            Log.e("WeatherForecast", "GetForecastViaGps: Permission to determine the location was not received. Search is performed by location saved in memory " + Arrays.toString(locationMemory));
        }
    }

    private void getLtdLng(Location location) {
        if (location != null) {
            loadForecast(location.getLatitude(), location.getLongitude());
            Log.i("WeatherForecast", "GetForecastViaGps: Location determined by GPS: " + location.getLatitude() + " " + location.getLongitude());
        }
        //  The location object may be null in the following situations:
        //  - location is turned off in the device settings;
        //  - device never recorded its location;
        //  - google Play services on the device have restarted, and there is no active Fused Location Provider client that has requested location after the services restarted.
        //  To avoid this situation we create a new client and request location updates yourself.
        else {
            if (isGpsAvailable()) {
                updateFusedLocationClient();
            }
            // Case when there was no last location and it also failed to update
            else {
                toastMessageObserver.setValue(context.getString(R.string.main_activity_check_gps_enabled));
                isDataLoading.set(false);
                Log.e("WeatherForecast", "GetForecastViaGps: FusedLocationProvider return null");
            }
        }
    }

    private void updateFusedLocationClient() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        loadForecast(location.getLatitude(), location.getLongitude());
                        Log.i("WeatherForecast", "GetForecastViaGps: FusedLocationProvider has been updated, location: " + location.getLatitude() + " " + location.getLongitude());
                    }
                }
            }
        };
        checkPermission();
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void loadForecast(double latitude, double longitude) {
        disposables.clear();
        RequestForecastUseCase.Params requestForecastParams = new RequestForecastUseCase.Params(latitude, longitude);
        Disposable subscription = requestForecastUseCase.run(requestForecastParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        forecast -> {
                            updateMessage();
                            saveLocation(latitude, longitude);
                            determineCityByCoordinates(latitude, longitude);
                            forecastLive.setValue(forecast);
                            isEmpty.set(false);
                            isDataLoading.set(false);
                            Log.i("WeatherForecast", "Request was completed successfully");
                        },
                        // onError
                        throwable -> {
                            toastMessageObserver.setValue(context.getString(R.string.main_activity_try_later));
                            isDataLoading.set(false);
                            Log.e("WeatherForecast", "An error occurred during the request: " + throwable.getMessage());
                        }
                );
        disposables.add(subscription);
    }

    private void updateMessage() {
        counter++;
        if (counter > 1) {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_date_update));
        }
    }

    private void saveLocation(double latitude, double longitude) {
        String location = latitude + "/" + longitude;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STORAGE_LOCATION, location);
        editor.apply();
    }

    private void getForecastViaQuery(String location) {
        List<Address> locations = new ArrayList<>();
        try {
            locations = geocoder.getFromLocationName(location, 1);
            Log.i("WeatherForecast", "GetForecastViaQuery: Location: " + locations + " " + locations.size());
        } catch (IOException e) {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_try_later));
            isDataLoading.set(false);
            Log.e("WeatherForecast", "GetForecastViaQuery: Impossible to connect to GeoCoder:", e);
        } finally {
            if (locations.size() == 0) {
                toastMessageObserver.setValue(context.getString(R.string.main_activity_nothing_not_found));
                isDataLoading.set(false);
            } else {
                double lat = locations.get(0).getLatitude();
                double lon = locations.get(0).getLongitude();
                loadForecast(lat, lon);
                Log.i("WeatherForecast", "GetForecastViaQuery: Location: " + lat + " " + lon);
            }
        }
    }

    private void determineCityByCoordinates(double latitude, double longitude) {
        List<Address> location = new ArrayList<>();
        try {
            location = geocoder.getFromLocation(latitude, longitude, 1);
            Log.i("WeatherForecast", "DetermineCityByCoordinates: Location size: " + location.size());
        } catch (IOException e) {
            toastMessageObserver.setValue(context.getString(R.string.main_activity_try_later));
            Log.e("WeatherForecast", "DetermineCityByCoordinates: Impossible to connect to GeoCoder");
        } finally {
            if (location.size() != 0) {
                String area = location.get(0).getAdminArea();
                if (area == null) {
                    area = location.get(0).getCountryName();
                }
                String city = location.get(0).getLocality();
                if (city == null) {
                    city = area;
                    area = "";
                }
                locality.set(city + " " + area);
                Log.i("WeatherForecast", "DetermineCityByCoordinates: Location: " + location.get(0).getLocality());
            }
        }
    }
}
