package com.example.weatherforecast.features.main;

import android.app.SearchManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private TabLayout navigation;
    private ViewPager pager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarMainActivity.toolbar);
        initNavigation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        menu.findItem(R.id.search).setOnMenuItemClickListener(menuItem -> {
            searchView.setFocusable(true);
            searchView.setIconified(false);
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                menu.findItem(R.id.search).collapseActionView();
                binding.toolbarMainActivity.toolbar.setTitle(searchView.getQuery());
                loadForecast();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    private void initNavigation() {
        navigation = binding.navigation;
        pager = binding.fragmentContainer;
    }

    private void loadForecast() {
        OpenWeatherMapApiService apiService = RetrofitClientInstance.getRetrofitInstance().create(OpenWeatherMapApiService.class);
        Call<Forecast> call = apiService.getForecast();
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                Forecast forecast = response.body();
                showForecast(forecast);
                Log.i("INFOR", response.toString());
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.i("INFOR", t.getMessage());
            }
        });
    }

    private void showForecast(Forecast forecast) {
        FragmentPageAdapter adapter = new FragmentPageAdapter(getSupportFragmentManager());
        adapter.addFragment(TodayFragment.newInstance(forecast), "Today");
        adapter.addFragment(HourlyFragment.newInstance(forecast.getHourly()), "Hourly");
        adapter.addFragment(DailyFragment.newInstance(forecast.getDaily()), "Daily");
        pager.setAdapter(adapter);
        navigation.setupWithViewPager(pager);
    }
}
