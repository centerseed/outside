package com.barry.outside;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.barry.outside.account.AccountUtil;
import com.barry.outside.air.AirRankListFragment;
import com.barry.outside.network.AQIParser;
import com.barry.outside.network.AsyncCallback;
import com.barry.outside.provider.WeatherProvider;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.Stack;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final long SYNC_PERIOD = 50L * 60L;
    String mAuth;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mDrawerToggle;
    protected OkHttpClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mClient = new OkHttpClient();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mAuth = getString(R.string.auth_provider_weather);
        Uri uri = WeatherProvider.getProviderUri(mAuth, WeatherProvider.TABLE_PM25);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() == 0) {
            Intent intent = new Intent(this, InitActivity.class);
            startActivity(intent);
            finish();
        }

        AccountUtil.createDummyAccountIfNotExist(this);
        Account account = AccountUtil.getAccount(this);
        getContentResolver().setSyncAutomatically(account, mAuth, true);
        ContentResolver.addPeriodicSync(account, mAuth, Bundle.EMPTY, SYNC_PERIOD);

        onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
        mNavigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        Bundle args = new Bundle();
        args.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        args.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        getContentResolver().requestSync(AccountUtil.getAccount(this), mAuth, args); */

        Request request = new Request.Builder()
                .url("http://opendata.epa.gov.tw/ws/Data/REWIQA/?$orderby=SiteName&$skip=0&$top=1000&format=json")
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new AsyncCallback(this) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                new AQIParser(getApplicationContext()).parse(json);
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().findFragmentByTag("home") != null)
                super.onBackPressed();
            else {
                onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
                mNavigationView.getMenu().getItem(0).setChecked(true);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        replaceFragmentById(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragmentById(int id) {
        Fragment f = null;
        if (id == R.id.nav_home) {
            f = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, f, "home").commit();
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
            return;
        }

        if (id == R.id.nav_ranking) {
            f = new AirRankListFragment();
        }

        if (id == R.id.nav_setting) {
            f = new SettingFragment();
        }

        if (f != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
        }
    }
}
