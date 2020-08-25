package com.example.weatherforecast.features.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.weatherforecast.App;
import com.example.weatherforecast.R;
import com.example.weatherforecast.databinding.ActivityMainBinding;
import com.example.weatherforecast.features.FragmentPageAdapter;
import com.example.weatherforecast.features.daily.DailyFragment;
import com.example.weatherforecast.features.hourly.HourlyFragment;
import com.example.weatherforecast.features.today.TodayFragment;
import com.google.android.material.tabs.TabLayout;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;
    private int locationRequestCode = 1000;
    private ActivityMainBinding binding;
    private ViewPager pager;
    private int pageItem = 0;

    @Inject
    ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().activityComponent().inject(this);

        initViewBinding();
        initNavigation();
        initSwipeRefresh();
        initToast();
        showForecast();
        checkGpsEnabled();
        viewModel.start();
    }

    private void initViewBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setViewModel((MainViewModel) viewModel);
        setSupportActionBar(binding.toolbar);
    }

    private void initNavigation() {
        TabLayout navigation = binding.navigation;
        pager = binding.fragmentContainer;
        navigation.setupWithViewPager(pager);
    }

    private void initToast() {
        viewModel.getToastObserver().observe(this, message -> {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toastSetGravity(toast);
            toast.show();
        });
    }

    private void showForecast() {
        viewModel.getForecast().observe(this, forecast -> {
            FragmentPageAdapter adapter = new FragmentPageAdapter(getSupportFragmentManager());
            adapter.addFragment(TodayFragment.newInstance(forecast), getString(R.string.main_activity_today_fragment));
            adapter.addFragment(HourlyFragment.newInstance(forecast.getHourly()), getString(R.string.main_activity_hourly_fragment));
            adapter.addFragment(DailyFragment.newInstance(forecast.getDaily()), getString(R.string.main_activity_daily_fragment));
            pager.setAdapter(adapter);
            pager.setCurrentItem(pageItem);
        });
    }

    private void checkGpsEnabled() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, locationRequestCode);
            viewModel.permissionDenied(true);
        } else {
            viewModel.permissionDenied(false);
        }
    }

    private void initSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            pageItem = pager.getCurrentItem();
            binding.swipeRefresh.setRefreshing(false);
            viewModel.swipeRefresh();
        }, 2000));
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() != 0) {
            pager.setCurrentItem(0);
        } else if (doubleBackToExitPressedOnce) {
            finish();
        } else {
            clickAgain();
            doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        menu.findItem(R.id.gps).setOnMenuItemClickListener(menuItem -> {
            checkGpsEnabled();
            pageItem = pager.getCurrentItem();
            viewModel.startSearchByGps();
            menu.findItem(R.id.search).collapseActionView(); // Collapse the action view associated with this menu item
            return true;
        });

        menu.findItem(R.id.search).setOnMenuItemClickListener(menuItem -> {
            searchView.setIconified(false); // Search field to always be visible
            return true;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                pageItem = pager.getCurrentItem();
                viewModel.startSearchByQuery(s);
                searchView.setQuery("", false);
                menu.findItem(R.id.search).collapseActionView(); // Collapse the action view associated with this menu item
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            menu.findItem(R.id.search).collapseActionView();
            return false;
        });
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        viewModel.requestPermissionsResult(requestCode, grantResults);
    }

    private void toastSetGravity(Toast toast) {
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 40);
    }

    private void clickAgain() {
        Toast toast = Toast.makeText(this, R.string.main_activity_click_again, Toast.LENGTH_LONG);
        toastSetGravity(toast);
        toast.show();
    }
}
