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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar(R.mipmap.ic_launcher);
        AccountUtil.createDummyAccountIfNotExist(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String location;

        checkPermission();
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (null != lastLocation) {
            location = LocationUtils.getNearbyCountry(this, lastLocation.getLatitude(), lastLocation.getLongitude());
        } else {
            location = "正在更新位置";
        }
        Bundle args = new Bundle();
        args.putString("location", location);

        Fragment f = new AirFragment();
        f.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();

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

        checkPermission();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 30, 1000, this);
    }

    @Override
    public void onPause() {
        super.onPause();

        checkPermission();
        locationManager.removeUpdates(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(MainActivity.class.getName(), "location: " + location.toString());
        Intent intent = new Intent();
        intent.setAction(BroadcastConst.BROADCASE_UPDATE_LOCATION + "");
        intent.putExtra("location", location);
        sendBroadcast(intent);
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

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
    }
}

