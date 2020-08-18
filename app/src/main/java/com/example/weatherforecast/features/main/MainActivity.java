package com.example.weatherforecast.features.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.weatherforecast.R;
import com.example.weatherforecast.databinding.ActivityMainBinding;
import com.example.weatherforecast.features.FragmentPageAdapter;
import com.example.weatherforecast.features.daily.DailyFragment;
import com.example.weatherforecast.features.hourly.HourlyFragment;
import com.example.weatherforecast.features.today.TodayFragment;
import com.example.weatherforecast.models.Forecast;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private MainContract.Presenter presenter;

    private boolean doubleBackToExitPressedOnce = false;
    private ActivityMainBinding binding;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MainPresenter(this, this, getPreferences(Context.MODE_PRIVATE));

        initViewBinding();
        initNavigation();
        hideProgressBarAndViewForecast();
        presenter.start();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.presenter = presenter;
    }

    private void initViewBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarMainActivity.toolbar);
    }

    private void initNavigation() {
        TabLayout navigation = binding.navigation;
        pager = binding.fragmentContainer;
        navigation.setupWithViewPager(pager);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() != 0) {
            pager.setCurrentItem(0);
        } else if (doubleBackToExitPressedOnce) {
            finish();
        } else {
            Toast.makeText(MainActivity.this, R.string.main_activity_click_again, Toast.LENGTH_LONG).show();
            doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        menu.findItem(R.id.gps).setOnMenuItemClickListener(menuItem -> {
            showProgressBar();
            setDefaultToolbarTitle();
            menu.findItem(R.id.search).collapseActionView(); // Collapse the action view associated with this menu item
            presenter.getForecastViaGps();
            return true;
        });

        menu.findItem(R.id.search).setOnMenuItemClickListener(menuItem -> {
            searchView.setIconified(false); // Search field to always be visible
            return true;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                showProgressBar();
                setDefaultToolbarTitle();
                menu.findItem(R.id.search).collapseActionView(); // Collapse the action view associated with this menu item
                presenter.getForecastViaQuery(searchView.getQuery().toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        presenter.requestPermissionsResult(requestCode, grantResults);
    }

    @Override
    public void showForecast(Forecast forecast) {
        binding.fragmentContainer.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
        FragmentPageAdapter adapter = new FragmentPageAdapter(getSupportFragmentManager());
        adapter.addFragment(TodayFragment.newInstance(forecast), getString(R.string.main_activity_today_fragment));
        adapter.addFragment(HourlyFragment.newInstance(forecast.getHourly()), getString(R.string.main_activity_hourly_fragment));
        adapter.addFragment(DailyFragment.newInstance(forecast.getDaily()), getString(R.string.main_activity_daily_fragment));
        pager.setAdapter(adapter);
    }

    @Override
    public void showProgressBar() {
        binding.fragmentContainer.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setCityNameForToolbarTitle(String city, String area) {
        binding.toolbarMainActivity.toolbar.setTitle(city + " " + area);
    }

    @Override
    public void setDefaultToolbarTitle() {
        binding.toolbarMainActivity.toolbar.setTitle(R.string.app_name);
    }

    @Override
    public void hideProgressBarAndViewForecast() {
        binding.fragmentContainer.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void checkGps() {
        Toast.makeText(MainActivity.this, R.string.main_activity_check_gps_enabled, Toast.LENGTH_LONG).show();
    }

    @Override
    public void permissionDenied() {
        Toast.makeText(this, R.string.main_activity_permission_denied, Toast.LENGTH_LONG).show();
        hideProgressBarAndViewForecast();
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.main_activity_try_later, Toast.LENGTH_LONG).show();
        hideProgressBarAndViewForecast();
    }
}
