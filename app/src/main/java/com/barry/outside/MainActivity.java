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
import com.barry.outside.air.AirRankListFragment;
import com.barry.outside.provider.WeatherProvider;

import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final long SYNC_PERIOD = 50L * 60L;
    String mAuth;
    Stack<Integer> mFragmentStack;
    NavigationView mNavigationView;

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

        mFragmentStack = new Stack<>();
        onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
        mNavigationView.getMenu().getItem(0).setChecked(true);
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
        }

        if (mFragmentStack.size() == 1) {
            super.onBackPressed();
        } else {
            mFragmentStack.pop();
            int id = mFragmentStack.pop();
            if (id == R.id.nav_home) {
                mNavigationView.getMenu().getItem(0).setChecked(true);
            } else if (id == R.id.nav_ranking) {
                mNavigationView.getMenu().getItem(1).setChecked(true);
            } else if (id == R.id.nav_setting) {
                mNavigationView.getMenu().getItem(2).setChecked(true);
            }
            replaceFragmentById(id);
            mFragmentStack.push(id);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        replaceFragmentById(id);
        mFragmentStack.push(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragmentById(int id) {
        Fragment f = null;
        if (id == R.id.nav_home) {
            f = new HomeFragment();
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
