package com.example.weatherforecast.features.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

class MainPresenter implements MainContract.Presenter {
    private int locationRequestCode = 1000;
    private double longitude;
    private double latitude;

    private FusedLocationProviderClient fusedLocationClient;
    private CompositeDisposable disposables;
    private Geocoder geocoder;

    private Context context;
    private MainContract.View view;
    private ForecastRepository forecastRepository;

    public MainPresenter(Context context, MainContract.View view) {
        this.context = context;
        this.disposables = new CompositeDisposable();
        this.forecastRepository = new ForecastRepository();
        this.geocoder = new Geocoder(context, Locale.getDefault());
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        this.view = view;
        this.view.setPresenter(this);
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
            Log.i("FusedLocationClient", "Permission is obtained");
            fusedLocationClient.getLastLocation().addOnSuccessListener(this::getLtdLng);
        }
    }

    @Override
    public void requestPermissionsResult(int requestCode, int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("FusedLocationClient", "Permission is obtained");
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this::getLtdLng);
                } else {
                    view.permissionDenied();
                    Log.e("FusedLocationClient", "Permission denied");
                }
                break;
            }
        }
    }

    private void getLtdLng(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            loadForecast(latitude, longitude);
            Log.i("FusedLocationClient", "Location" + " " + latitude + " " + longitude);
        } else {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gpsEnabled) {
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
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                loadForecast(latitude, longitude);
                                Log.i("FusedLocationClient", "Location" + " " + latitude + " " + longitude);
                            }
                        }
                    }
                };
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            } else {
                view.hideProgressBarAndViewForecast();
                view.setDefaultToolbarTitle();
                view.checkGps();
                Log.e("FusedLocationClient", "Location is null");
            }
        }
    }

    private void loadForecast(double lat, double lon) {
        disposables.clear();
        Disposable subscription = forecastRepository.getForecast(lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        forecast -> {
                            determineCityByCoordinates(lat, lon);
                            view.showForecast(forecast);

                        },
                        // onError
                        throwable -> {
                            Log.e("Response", throwable.getMessage());
                            view.showError();
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
                Log.i("Geolocation", "Location" + location.toString());
            }
        }
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
                Log.i("Geolocation", "Location" + " " + lat + " " + lon);
                loadForecast(lat, lon);
            }
        }
    }
}
