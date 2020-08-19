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
        String location = sharedPreferences.getString(STORAGE_LOCATION, "");
        if (!location.equals("")) {
            view.showProgressBar();
            getForecastViaQuery(location);
        }
    }

    @Override
    public void unsubscribe() {
        disposables.clear();
    }

    @Override
    public void getForecastViaGps() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, locationRequestCode);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this::getLtdLng);
            Log.i("FusedLocationClient", "Permission is obtained");
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
                    Log.i("FusedLocationClient", "Permission is obtained");
                } else {
                    view.permissionDenied();
                    Log.e("FusedLocationClient", "Permission denied");
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

    private void getLtdLng(Location location) {
        if (location != null) {
            loadForecast(location.getLatitude(), location.getLongitude());
            Log.i("FusedLocationClient", "Location" + " " + location.getLatitude() + " " + location.getLongitude());
        }
        //  The location object may be null in the following situations:
        //  - location is turned off in the device settings;
        //  - device never recorded its location;
        //  - google Play services on the device have restarted, and there is no active Fused Location Provider client that has requested location after the services restarted.
        //  To avoid this situation we create a new client and request location updates yourself.
        else {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                updateFusedLocationClient();
            }
            // Case when there was no last location and it also failed to update
            else {
                view.hideProgressBarAndViewForecast();
                view.setDefaultToolbarTitle();
                view.checkGps();
                Log.e("FusedLocationClient", "Location is null");
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
                        Log.i("FusedLocationClient", "Location" + " " + location.getLatitude() + " " + location.getLongitude());
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
                            determineCityByCoordinates(latitude, longitude);
                            view.showForecast(forecast);
                        },
                        // onError
                        throwable -> {
                            view.showError();
                            Log.e("Response", throwable.getMessage());
                        }
                );
        disposables.add(subscription);
    }

    private void determineCityByCoordinates(double latitude, double longitude) {
        List<Address> location = new ArrayList<>();
        try {
            location = geocoder.getFromLocation(latitude, longitude, 1);
            Log.i("Geolocation", "Location" + " " + location + " " + location.size());
        } catch (IOException e) {
            view.showError();
            Log.e("Geolocation", "Impossible to connect to GeoCoder", e);
        } finally {
            if (location.size() != 0) {
                view.setCityNameForToolbarTitle(location.get(0).getLocality(), location.get(0).getAdminArea());
                saveLocation(location);
                Log.i("Geolocation", "Location" + location.toString());
            }
        }
    }

    private void saveLocation(List<Address> location) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STORAGE_LOCATION, location.get(0).getLocality());
        editor.apply();
    }

    @Override
    public void getForecastViaQuery(String locationName) {
        List<Address> location = new ArrayList<>();
        try {
            location = geocoder.getFromLocationName(locationName, 1);
            Log.i("Geolocation", "Location" + " " + location + " " + location.size());
        } catch (IOException e) {
            view.showError();
            Log.e("Geolocation", "Impossible to connect to GeoCoder", e);
        } finally {
            if (location.size() != 0) {
                double lat = location.get(0).getLatitude();
                double lon = location.get(0).getLongitude();
                loadForecast(lat, lon);
                Log.i("Geolocation", "Location" + " " + lat + " " + lon);
            }
        }
    }
}
