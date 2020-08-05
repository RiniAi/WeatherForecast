package com.example.weatherforecast.features.main;

import android.Manifest;
import android.app.SearchManager;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;

    private int locationRequestCode = 1000;

    private ActivityMainBinding binding;
    private SearchView searchView;
    private TabLayout navigation;
    private ViewPager pager;

    private double latitude;
    private double longitude;

    private FragmentPageAdapter adapter;

    // TODO: Add update FusedLocationClient, anna 05.08.2020
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarMainActivity.toolbar);

        initNavigation();
        navigation.setupWithViewPager(pager);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getGpsData();
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() != 0) {
            pager.setCurrentItem(0);
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Click again to exit the app", Toast.LENGTH_SHORT).show();
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
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this, this::getLtdLng);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
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
            Log.e("FusedLocationClient", "Location is null");
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
                initGeoCoder();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    private void initGeoCoder() {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> location = new ArrayList<>();
        try {
            location = geocoder.getFromLocationName(searchView.getQuery().toString(), 1);
            Log.i("Geolocation", "Location" + " " + location + " " + location.size());
        } catch (IOException e) {
            Toast.makeText(this, "Impossible to connect to GeoCoder", Toast.LENGTH_SHORT).show();
            Log.e("Geolocation", "Impossible to connect to GeoCoder", e);
        } finally {
            double lat = location.get(0).getLatitude();
            double lon = location.get(0).getLongitude();
            Log.i("Geolocation", "Location" + " " + lat + " " + lon);
            binding.toolbarMainActivity.toolbar.setTitle(location.get(0).getFeatureName() + " " + location.get(0).getAdminArea());
            loadForecast(lat, lon);
        }
    }

    private void loadForecast(double lat, double lon) {
        OpenWeatherMapApiService apiService = RetrofitClientInstance.getRetrofitInstance().create(OpenWeatherMapApiService.class);
        Call<Forecast> call = apiService.getForecast(lat, lon);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(@NonNull Call<Forecast> call, @NonNull Response<Forecast> response) {
                Log.i("Response", response.toString());
                Forecast forecast = response.body();
                showForecast(forecast);
            }

            @Override
            public void onFailure(@NonNull Call<Forecast> call, @NonNull Throwable t) {
                Log.e("Response", t.getMessage());
                Toast.makeText(MainActivity.this, "Failed to get weather data" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showForecast(Forecast forecast) {
        adapter = new FragmentPageAdapter(getSupportFragmentManager());
        adapter.addFragment(TodayFragment.newInstance(forecast), "Today");
        adapter.addFragment(HourlyFragment.newInstance(forecast.getHourly()), "Hourly");
        adapter.addFragment(DailyFragment.newInstance(forecast.getDaily()), "Daily");

        pager.setAdapter(adapter);
    }
}
