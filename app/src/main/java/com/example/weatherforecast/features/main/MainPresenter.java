package com.example.weatherforecast.features.main;

import android.Manifest;
import android.app.Activity;
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

public class MainPresenter implements MainContract.Presenter {
    public static final String STORAGE_LOCATION = "location";
    private int locationRequestCode = 1000;
    private String[] locationMemory;
    private int counter = 0;

    private FusedLocationProviderClient fusedLocationClient;
    private CompositeDisposable disposables;
    private MainContract.View view;
    private Geocoder geocoder;

    @Inject
    Context context;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    RequestForecastUseCase requestForecastUseCase;

    @Inject
    public MainPresenter(Context context, SharedPreferences sharedPreferences, RequestForecastUseCase requestForecastUseCase) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.disposables = new CompositeDisposable();
        this.geocoder = new Geocoder(context, Locale.getDefault());
        this.requestForecastUseCase = requestForecastUseCase;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void start() {
        locationMemory = sharedPreferences.getString(STORAGE_LOCATION, "").split("/");
        Log.i("WeatherForecast", "Location in memory: " + Arrays.toString(locationMemory));
        if (isInternetAvailable() & isGpsAvailable()) {
            getForecastViaGps();
        } else if (!isInternetAvailable()) {
            view.checkInternetConnection();
            view.hideProgressBar();
        } else if (!isGpsAvailable() & !Arrays.toString(locationMemory).equals("[]")) {
            loadForecast(Double.parseDouble(locationMemory[0]), Double.parseDouble(locationMemory[1]));
        } else if (!isGpsAvailable() & Arrays.toString(locationMemory).equals("[]")) {
            view.checkGpsEnabledRoQuery();
            view.showEmptyView();
        }
    }

    @Override
    public void swipeRefresh() {
        locationMemory = sharedPreferences.getString(STORAGE_LOCATION, "").split("/");
        if (isInternetAvailable() && !Arrays.toString(locationMemory).equals("[]")) {
            loadForecast(Double.parseDouble(locationMemory[0]), Double.parseDouble(locationMemory[1]));
        } else if (!isInternetAvailable()) {
            view.checkInternetConnection();
        } else {
            view.refreshError();
        }
    }

    @Override
    public void unsubscribe() {
        disposables.clear();
    }

    @Override
    public boolean isGpsAvailable() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void getForecastViaGps() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) view, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, locationRequestCode);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this::getLtdLng);
            Log.i("WeatherForecast", "GetForecastViaGps: Permission to determine the location of the received");
        }
    }

    // Requesting permission if it was not received before (requestCode == 1000)
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

    @Override
    public void checkLastQuery() {
        if (Arrays.toString(locationMemory).equals("[]")) {
            view.showEmptyView();
            view.permissionDenied();
            Log.e("WeatherForecast", "GetForecastViaGps: Permission to determine the location was not received");
        } else {
            loadForecast(Double.parseDouble(locationMemory[0]), Double.parseDouble(locationMemory[1]));
            view.permissionDeniedLastQuery();
            Log.e("WeatherForecast", "GetForecastViaGps: Permission to determine the location was not received. Search is performed by location saved in memory " + Arrays.toString(locationMemory));
        }
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
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
                view.checkGpsEnabled();
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
                            view.showForecast(forecast);
                            Log.i("WeatherForecast", "Request was completed successfully");
                        },
                        // onError
                        throwable -> {
                            view.showError();
                            Log.e("WeatherForecast", "An error occurred during the request: " + throwable.getMessage());
                        }
                );
        disposables.add(subscription);
    }

    private void updateMessage() {
        counter++;
        if (counter > 1) {
            view.updateMessage();
        }
    }

    private void saveLocation(double latitude, double longitude) {
        String location = latitude + "/" + longitude;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STORAGE_LOCATION, location);
        editor.apply();
    }

    @Override
    public void getForecastViaQuery(String location) {
        List<Address> locations = new ArrayList<>();
        try {
            locations = geocoder.getFromLocationName(location, 1);
            Log.i("WeatherForecast", "GetForecastViaQuery: Location: " + locations + " " + locations.size());
        } catch (IOException e) {
            view.showError();
            Log.e("WeatherForecast", "GetForecastViaQuery: Impossible to connect to GeoCoder:", e);
        } finally {
            if (locations.size() == 0) {
                view.nothingNotFound();
                view.hideProgressBar();
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
            view.showError();
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
                view.setCityNameForToolbarTitle(city, area);
                Log.i("WeatherForecast", "DetermineCityByCoordinates: Location: " + location.get(0).getLocality());
            }
        }
    }
}
