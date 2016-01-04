package com.barry.outside;

import android.Manifest;
import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.barry.outside.account.AccountUtil;
import com.barry.outside.air.AirFragment;
import com.barry.outside.uv.UVFragment;


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
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle args = new Bundle();
        args.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        args.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        mResolver.requestSync(account, authority, args);

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
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

