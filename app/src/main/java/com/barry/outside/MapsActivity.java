package com.barry.outside;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.barry.outside.air.SiteInfo;
import com.barry.outside.provider.WeatherProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends ToolbarActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap mMap;
    Location mLocation;
    String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initToolbar(R.mipmap.ic_back);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocation = getIntent().getParcelableExtra("location");
        mName = getIntent().getStringExtra("name");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng pos = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(pos).title(mName));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(pos)); */

        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(pos)
                        .zoom(11)
                        .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = new CursorLoader(this);
        cl.setUri(WeatherProvider.getProviderUri(getString(R.string.auth_provider_weather), WeatherProvider.TABLE_WEATHER));
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        BitmapDescriptor icon =
                BitmapDescriptorFactory.fromResource(R.mipmap.ic_localization);

        if (cursor != null && cursor.moveToFirst()) {
            ArrayList<SiteInfo> infos = LocationUtils.getNearestSiteArray(cursor, mLocation);

            for (int i = 0; i < infos.size(); i++) {
                SiteInfo info = infos.get(i);
                if (i == 0)
                    mMap.addMarker(new MarkerOptions().position(info.getLatlng()).title(info.getName()).icon(icon).snippet(info.getPm25() + "")).showInfoWindow();
                else
                    mMap.addMarker(new MarkerOptions().position(info.getLatlng()).title(info.getName()).icon(icon).snippet(info.getPm25() + ""));
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
