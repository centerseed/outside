package com.barry.outside;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.barry.outside.account.AccountUtil;
import com.barry.outside.air.AirRankingFragment;
import com.barry.outside.provider.WeatherProvider;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final long SYNC_PERIOD = 50L * 60L;
    String mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        HomeFragment f = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, f, null).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle args = new Bundle();
        args.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        args.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        getContentResolver().requestSync(AccountUtil.getAccount(this), mAuth, args);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment f = null;
        if (id == R.id.nav_home) {
            f = new HomeFragment();
        }

        if (id == R.id.nav_ranking) {
            f = new AirRankingFragment();
        }

        if (id == R.id.nav_setting) {
            f = new SettingFragment();
        }

        if (f != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
