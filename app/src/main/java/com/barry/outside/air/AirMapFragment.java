package com.barry.outside.air;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barry.outside.BroadcastConst;
import com.barry.outside.R;
import com.barry.outside.base.BroadcastFragment;
import com.barry.outside.provider.WeatherProvider;
import com.barry.outside.utils.BroadcastUtils;
import com.barry.outside.utils.ColorUtils;
import com.barry.outside.utils.LocationUtils;
import com.barry.outside.utils.PreferenceUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class AirMapFragment extends BroadcastFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    Location mLocation;
    boolean isCollapse = false;
    int mCurrentType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mCurrentType = PreferenceUtils.getDefaultType(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, null, this);
        mLocation = PreferenceUtils.getLastLocation(getContext());
    }

    @Override
    public void addIntentFilter(IntentFilter filter) {
        filter.addAction(BroadcastConst.BROADCAST_GET_LOCATION + "");
        filter.addAction(BroadcastConst.BROADCAST_COLLAPSE + "");
        filter.addAction(BroadcastConst.BROADCAST_EXPAND + "");
        filter.addAction(BroadcastConst.BROADCAST_SELECT_LOCATION + "");
        filter.addAction(BroadcastConst.BROADCAST_CLICK_MAP_POS + "");
    }

    @Override
    public void onReceiveBroadcast(int action, Intent intent) {
        if (action == BroadcastConst.BROADCAST_GET_LOCATION && !isCollapse) {
            mLocation = intent.getParcelableExtra("location");
            getLoaderManager().restartLoader(0, null, this);
            moveCamera(10, mLocation);
        }

        if (action == BroadcastConst.BROADCAST_CLICK_MAP_POS && !isCollapse) {
            mLocation = intent.getParcelableExtra("location");
            moveCamera(mMap.getCameraPosition().zoom, mLocation);
        }

        if (action == BroadcastConst.BROADCAST_SELECT_LOCATION && !isCollapse) {
            mLocation = intent.getParcelableExtra("location");
            getLoaderManager().restartLoader(0, null, this);
            moveCamera(10, mLocation);
        }

        if (action == BroadcastConst.BROADCAST_COLLAPSE && mMap != null) {
            isCollapse = true;
            Location location = new Location("");
            location.setLatitude(23.973875);
            location.setLongitude(120.982024);
            moveCamera(7.4f, location);
        }

        if (action == BroadcastConst.BROADCAST_EXPAND && mMap != null) {
            isCollapse = false;
            Location location = PreferenceUtils.getLastLocation(getContext());
            moveCamera(10, location);
        }
    }

    @Override
    protected Uri getProviderUri() {
        return WeatherProvider.getProviderUri(getString(R.string.auth_provider_weather), WeatherProvider.TABLE_PM25);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = (CursorLoader) super.onCreateLoader(id, args);
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null && cursor.moveToFirst()) {
            ArrayList<SiteInfo> infos = LocationUtils.getNearestSiteArray(cursor, mLocation);
            mCurrentType = PreferenceUtils.getDefaultType(getContext());

            if (mMap != null) {
                mMap.clear();
            }
            for (int i = 0; i < infos.size(); i++) {
                SiteInfo info = infos.get(i);
                MarkerOptions options;
                if (mCurrentType == 0) {
                    BitmapDescriptor icon = getMarkerIcon(ColorUtils.getPM25Color(getContext(), info.getPm25()));
                    options = new MarkerOptions().position(info.getLatlng()).title(info.getName()).icon(icon).snippet(info.getPm25() + "");
                } else if (mCurrentType == 1) {
                    BitmapDescriptor icon = getMarkerIcon(ColorUtils.getPM10(getContext(), info.getPm10()));
                    options = new MarkerOptions().position(info.getLatlng()).title(info.getName()).icon(icon).snippet(info.getPm10() + "");
                } else if (mCurrentType == 2) {
                    BitmapDescriptor icon = getMarkerIcon(ColorUtils.getPSIColor(getContext(), info.getPSI()));
                    options = new MarkerOptions().position(info.getLatlng()).title(info.getName()).icon(icon).snippet(info.getPSI() + "");
                } else if (mCurrentType == 3) {
                    BitmapDescriptor icon = getMarkerIcon(ColorUtils.getO3(getContext(), info.getO3()));
                    options = new MarkerOptions().position(info.getLatlng()).title(info.getName()).icon(icon).snippet(info.getO3() + "");
                } else {
                    BitmapDescriptor icon = getMarkerIcon(ColorUtils.getCO(getContext(), info.getCO()));
                    options = new MarkerOptions().position(info.getLatlng()).title(info.getName()).icon(icon).snippet(info.getCO() + "");
                }

                if (i == 0)
                    mMap.addMarker(options).showInfoWindow();
                else
                    mMap.addMarker(options);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (PreferenceUtils.getAutoLocalize(getContext())) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        Location location = PreferenceUtils.getLastLocation(getContext());
        moveCamera(10, location);
    }

    public BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng latLng = marker.getPosition();

        Location location = new Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        BroadcastUtils.sendParcelableBroadcast(getActivity(), BroadcastConst.BROADCAST_CLICK_MAP_POS, "location", location);

        PreferenceUtils.setLastLocation(getContext(), location);
        return false;
    }

    private void moveCamera(float zoom, Location location) {
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(zoom)
                        .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
