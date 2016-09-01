package com.barry.outside.air;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barry.outside.BroadcastConst;
import com.barry.outside.R;
import com.barry.outside.base.BroadcastFragment;
import com.barry.outside.provider.WeatherProvider;
import com.barry.outside.utils.ColorUtils;
import com.barry.outside.utils.LocationUtils;
import com.barry.outside.utils.PreferenceUtils;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import at.grabner.circleprogress.CircleProgressView;

public class AirInfoFragment extends BroadcastFragment {

    CircleProgressView mCircleProgress;
    TextView mSite;
    Location mLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_air_info, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSite = (TextView) view.findViewById(R.id.site);

        mCircleProgress = (CircleProgressView) view.findViewById(R.id.circleProgress);
        mCircleProgress.setContourSize(0);
        mCircleProgress.setBlockCount(20);
        mCircleProgress.setMaxValue(72f);
        mCircleProgress.setRimColor(Color.LTGRAY);

        mLocation = PreferenceUtils.getLastLocation(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void addIntentFilter(IntentFilter filter) {
        filter.addAction(BroadcastConst.BROADCAST_GET_LOCATION + "");
    }

    @Override
    public void onReceiveBroadcast(int action, Intent intent) {
        if (action == BroadcastConst.BROADCAST_GET_LOCATION) {
            getLoaderManager().restartLoader(0, null, this);

            LatLng latlng = intent.getParcelableExtra("location");
            mLocation.setLongitude(latlng.longitude);
            mLocation.setLatitude(latlng.latitude);
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
            mSite.setText(infos.get(0).getName());
            mCircleProgress.setValueAnimated(infos.get(0).getPm25() * 72/100f);

            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name) + " - " + infos.get(0).getName());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
