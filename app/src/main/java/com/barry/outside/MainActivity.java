package com.barry.outside;

import android.Manifest;
import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.barry.outside.account.AccountUtil;
import com.barry.outside.air.AirFragment;
import com.barry.outside.provider.WeatherProvider;
import com.barry.outside.uv.UVFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Owner on 2015/11/13.
 */
public class MainActivity extends ToolbarActivity implements LocationListener, chooseCountyFragmentDialog.OnSelectedListener{

    final long SYNC_PERIOD = 50L * 60L;

    LocationManager locationManager;
    Account account;
    String authority;
    Location location;
    TextView tvLocation;
    ProgressBar pbLoading;
    ContentResolver mResolver;

    final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    final int LOCATION_ACCOUNT_REQUEST_CODE = 101;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar(R.mipmap.ic_launcher);
        AccountUtil.createDummyAccountIfNotExist(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initUI();
        initFragment();

        mResolver = getContentResolver();
        account = AccountUtil.getAccount(this);
        authority = getResources().getString(R.string.auth_provider_weather);
        mResolver.setSyncAutomatically(account, authority, true);
        ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, SYNC_PERIOD);

        Uri uri = WeatherProvider.getProviderUri(authority, WeatherProvider.TABLE_WEATHER);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() == 0) {
            Intent intent = new Intent(this, InitActivity.class);
            startActivity(intent);
            finish();

        }
        cursor.close();
    }

    @Override
    public void onResume() {
        super.onResume();

        location = PreferenceUtils.getLastLocation(this);
        if (PreferenceUtils.getAutoLocalize(this)) {
            updateLocation();
        } else {
            tvLocation.setText(LocationUtils.getNearbyCountry(this, location.getLatitude(), location.getLongitude()));
        }
        sendBroadcast(BroadcastConst.BROADCAST_GET_LOCATION);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.action_localization:
                pbLoading.setVisibility(View.VISIBLE);
                updateLocation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        pbLoading.setVisibility(View.INVISIBLE);
        this.location = location;
        tvLocation.setText(LocationUtils.getNearbyCountry(this, location.getLatitude(), location.getLongitude()));

        checkPermission();
        locationManager.removeUpdates(this);

        Log.e(MainActivity.class.getName(), "location: " + location.toString());
        sendBroadcast(BroadcastConst.BROADCAST_GET_LOCATION);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);

            return false;
        }
        return true;
    }

    private void initFragment() {
        Fragment f = new AirFragment();
        Bundle args = new Bundle();
        args.putParcelable("location", location);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_air, f).commit();

        Fragment f_uv = new UVFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container_uv, f_uv).commit();
    }

    private void initUI() {
        tvLocation = (TextView) findViewById(R.id.tv_location);
        pbLoading = (ProgressBar) findViewById(R.id.progressBar);

        if (PreferenceUtils.getAutoLocalize(this)) {
            pbLoading.setVisibility(View.VISIBLE);
        }

        pbLoading.setVisibility(View.INVISIBLE);

        ImageView ivLocation = (ImageView) findViewById(R.id.iv_location);
        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCountyFragmentDialog fragmentDialog = new chooseCountyFragmentDialog();
                Bundle bundle = new Bundle();
                bundle.putString(chooseCountyFragmentDialog.ARG_CURR_LOCATION, tvLocation.getText().toString());
                fragmentDialog.setArguments(bundle);
                fragmentDialog.setSelectedListener(MainActivity.this);
                fragmentDialog.show(getSupportFragmentManager(), null);
            }
        });
    }

    private void updateLocation() {
        if (checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, MainActivity.this);
            sendBroadcast(BroadcastConst.BROADCAST_UPDATING_LOCATION);
            pbLoading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            } else {
                // Permission Denied
            }
        }
    }

    private void sendBroadcast(int castId) {
        final Intent intent = new Intent();
        intent.setAction(castId + "");
        switch (castId) {
            case BroadcastConst.BROADCAST_GET_LOCATION:
                intent.putExtra("location", location);
                break;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendBroadcast(intent);
            }
        }).start();

    }

    @Override
    public void onSelected(String country) {
        location = LocationUtils.getLocationByName(this, country);
        tvLocation.setText(LocationUtils.getNearbyCountry(this, location.getLatitude(), location.getLongitude()));
        sendBroadcast(BroadcastConst.BROADCAST_GET_LOCATION);
    }
}

