package com.barry.outside.air;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barry.outside.BroadcastConst;
import com.barry.outside.R;
import com.barry.outside.base.BroadcastFragment;
import com.barry.outside.base.ContentFragment;
import com.barry.outside.provider.WeatherProvider;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_maps, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    }

    @Override
    public void onReceiveBroadcast(int action, Intent intent) {
        if (action == BroadcastConst.BROADCAST_GET_LOCATION) {

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

            for (int i = 0; i < infos.size(); i++) {
                SiteInfo info = infos.get(i);
                BitmapDescriptor icon = getMarkerIcon(ColorUtils.getColor(getContext(), info.getPm25()));
                MarkerOptions options = new MarkerOptions().position(info.getLatlng()).title(info.getName()).icon(icon).snippet(info.getPm25() + "");

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

        Location location = PreferenceUtils.getLastLocation(getContext());
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(10)
                        .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng latLng = marker.getPosition();
        Intent intent = new Intent();
        intent.setAction(BroadcastConst.BROADCAST_GET_LOCATION + "");
        intent.putExtra("location", latLng);
        getActivity().sendBroadcast(intent);
        return false;
    }
}
