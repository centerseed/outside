package com.barry.outside;

import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.barry.outside.account.AccountUtil;
import com.barry.outside.fragments.AirFragment;
import com.google.android.gms.common.api.GoogleApiClient;


/**
 * Created by Owner on 2015/11/13.
 */
public class MainActivity extends ToolbarActivity implements LocationListener {

    final long SYNC_PERIOD = 50 * 1000;

    LocationManager locationManager;
    Account account;
    String authority;
    Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar(R.mipmap.ic_launcher);
        AccountUtil.createDummyAccountIfNotExist(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initFragment();

        account = AccountUtil.getAccount(this);
        authority = getResources().getString(R.string.auth_provider_weather);
        ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, SYNC_PERIOD);
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle args = new Bundle();
        args.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        args.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(account, authority, args);
        
        if (PreferenceUtils.getAutoLocalize(this)) {
            updateLocation();
        }
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
                updateLocation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
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
        getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
    }

    private void updateLocation() {
        if (checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1000, MainActivity.this);
            sendBroadcast(BroadcastConst.BROADCAST_UPDATING_LOCATION);
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

        sendBroadcast(intent);
    }
}

