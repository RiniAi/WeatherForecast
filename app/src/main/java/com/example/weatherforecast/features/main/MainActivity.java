package com.example.weatherforecast.features.main;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.weatherforecast.R;
import com.example.weatherforecast.databinding.ActivityMainBinding;
import com.example.weatherforecast.features.FragmentPageAdapter;
import com.example.weatherforecast.features.daily.DailyFragment;
import com.example.weatherforecast.features.hourly.HourlyFragment;
import com.example.weatherforecast.features.today.TodayFragment;
import com.example.weatherforecast.models.Forecast;
import com.example.weatherforecast.network.OpenWeatherMapApiService;
import com.example.weatherforecast.network.RetrofitClientInstance;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;

    private int locationRequestCode = 1000;

    private ActivityMainBinding binding;
    private SearchView searchView;
    private TabLayout navigation;
    private ViewPager pager;

    private double latitude;
    private double longitude;

    private CompositeDisposable disposables;
    private OpenWeatherMapApiService apiService;

    private FragmentPageAdapter adapter;

    private Geocoder geocoder;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarMainActivity.toolbar);

        binding.progressBar.setVisibility(View.GONE);
        binding.fragmentContainer.setVisibility(View.GONE);

        disposables = new CompositeDisposable();

        apiService = RetrofitClientInstance.getRetrofitInstance().create(OpenWeatherMapApiService.class);

        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        initNavigation();
        navigation.setupWithViewPager(pager);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        unsubscribe();
    }

    public void unsubscribe() {
        disposables.clear();
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() != 0) {
            pager.setCurrentItem(0);
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
            } else {
                Toast.makeText(MainActivity.this, R.string.main_activity_click_again, Toast.LENGTH_LONG).show();
                doubleBackToExitPressedOnce = true;
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        }
    }

    private void initNavigation() {
        navigation = binding.navigation;
        pager = binding.fragmentContainer;
    }

    private void getGpsData() {
        binding.progressBar.setVisibility(View.VISIBLE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);
        } else {
            Log.i("FusedLocationClient", "Permission is obtained");
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, this::getLtdLng);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Log.i("FusedLocationClient", "Permission is obtained");
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this::getLtdLng);
                } else {
                    Toast.makeText(this, R.string.main_activity_permission_denied, Toast.LENGTH_LONG).show();
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
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            } else {
                hideProgressBarAndFragment();
                binding.toolbarMainActivity.toolbar.setTitle(R.string.app_name);
                Toast.makeText(MainActivity.this, "Check if GPS is enabled", Toast.LENGTH_LONG).show();
                Log.e("FusedLocationClient", "Location is null");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        menu.findItem(R.id.gps).setOnMenuItemClickListener(menuItem -> {
            getGpsData();
            searchView.clearFocus();
            menu.findItem(R.id.search).collapseActionView();

            return true;
        });

        menu.findItem(R.id.search).setOnMenuItemClickListener(menuItem -> {
            searchView.setFocusable(true);
            searchView.setIconified(false);
            return super.onOptionsItemSelected(menu.findItem(R.id.search));
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                menu.findItem(R.id.search).collapseActionView();

                binding.progressBar.setVisibility(View.VISIBLE);

                getCoordinatesCity();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    private void getCoordinatesCity() {
        List<Address> location = new ArrayList<>();
        try {
            location = geocoder.getFromLocationName(searchView.getQuery().toString(), 1);
            Log.i("Geolocation", "Location" + " " + location + " " + location.size());
        } catch (IOException e) {
            showError();
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

    private void determineCityByCoordinates(double latitude, double longitude) {
        List<Address> location = new ArrayList<>();
        try {
            location = geocoder.getFromLocation(latitude, longitude, 1);
            Log.i("Geolocation", "Location" + " " + location + " " + location.size());
        } catch (IOException e) {
            showError();
            Log.e("Geolocation", "Impossible to connect to GeoCoder", e);
        } finally {
            if (location.size() != 0) {
                binding.toolbarMainActivity.toolbar.setTitle(location.get(0).getLocality() + " " + location.get(0).getAdminArea());
                Log.i("Geolocation", "Location" + location.toString());
            }
        }
    }

    private void loadForecast(double lat, double lon) {
        disposables.clear();
        Disposable subscription = run(lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        forecast -> {
                            determineCityByCoordinates(latitude, longitude);
                            showForecast(forecast);

                        },
                        // onError
                        throwable -> {
                            Log.e("Response", throwable.getMessage());
                            showError();
                        }
                );
        disposables.add(subscription);
    }

    private Single<Forecast> run(double lat, double lon) {
        return apiService.getForecast(lat, lon);
    }

    private void showForecast(Forecast forecast) {
        binding.fragmentContainer.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);

        adapter = new FragmentPageAdapter(getSupportFragmentManager());

        adapter.addFragment(TodayFragment.newInstance(forecast), "Today");
        adapter.addFragment(HourlyFragment.newInstance(forecast.getHourly()), "Hourly");
        adapter.addFragment(DailyFragment.newInstance(forecast.getDaily()), "Daily");

        pager.setAdapter(adapter);
    }

    public void showError() {
        hideProgressBarAndFragment();
        Toast.makeText(this, R.string.main_activity_try_later, Toast.LENGTH_LONG).show();
    }

    private void hideProgressBarAndFragment() {
        binding.progressBar.setVisibility(View.GONE);
        binding.fragmentContainer.setVisibility(View.GONE);
    }
}
