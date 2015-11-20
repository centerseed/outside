package com.barry.outside.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.barry.outside.BroadcastConst;
import com.barry.outside.LocationUtils;
import com.barry.outside.PreferenceUtils;
import com.barry.outside.R;
import com.barry.outside.SiteAdapter;
import com.barry.outside.SiteInfo;
import com.barry.outside.provider.WeatherProvider;

import java.util.ArrayList;

/**
 * Created by Mac on 15/11/13.
 */
public class AirFragment extends BroadcastFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        chooseCountyFragmentDialog.OnSelectedListener {

    static final int LOADER_COUNTRY = 0;
    static final int LOADER_SITE = 1;

    Uri contentUri;
    TextView tvPM25;
    TextView tvLocation;
    TextView tvSite;
    TextView tvTime;
    ProgressBar pbLoading;
    Location location;

    String country = "";
    String siteName = "";

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_air, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pbLoading = (ProgressBar) view.findViewById(R.id.progressBar);
        pbLoading.setVisibility(View.INVISIBLE);

        tvSite = (TextView) view.findViewById(R.id.tv_site);
        tvPM25 = (TextView) view.findViewById(R.id.tv_pm25);
        tvTime = (TextView) view.findViewById(R.id.tv_update_time);

        tvLocation = (TextView) view.findViewById(R.id.tv_location);
        location = PreferenceUtils.getLastLocation(getContext());
        siteName = LocationUtils.getNearbySite(getContext(), location.getLatitude(), location.getLongitude());
        tvSite.setText(siteName);

        ImageView ivLocation = (ImageView) view.findViewById(R.id.iv_location);
        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCountyFragmentDialog fragmentDialog = new chooseCountyFragmentDialog();
                Bundle bundle = new Bundle();
                bundle.putString(chooseCountyFragmentDialog.ARG_CURR_LOCATION, tvLocation.getText().toString());
                fragmentDialog.setArguments(bundle);
                fragmentDialog.setSelectedListener(AirFragment.this);
                fragmentDialog.show(getFragmentManager(), null);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
    }

    @Override
    public void onResume() {
        super.onResume();

        contentUri = WeatherProvider.getProviderUri(getContext().getResources().getString(R.string.auth_provider_weather));
        // getLoaderManager().initLoader(LOADER_COUNTRY, null, this);
        getLoaderManager().initLoader(LOADER_SITE, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    IntentFilter getIntentFilter() {
        IntentFilter f = new IntentFilter();
        f.addAction(BroadcastConst.BROADCAST_GET_LOCATION + "");
        f.addAction(BroadcastConst.BROADCAST_UPDATING_LOCATION + "");
        return f;
    }

    @Override
    void onReceiveBroadcast(int action, Intent intent) {
        switch (action) {
            case BroadcastConst.BROADCAST_GET_LOCATION:
                location = intent.getParcelableExtra("location");

                if (null != location) {
                    updateLocation(location);
                }

                pbLoading.setVisibility(View.INVISIBLE);
                break;
            case BroadcastConst.BROADCAST_UPDATING_LOCATION:
                pbLoading.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cl = new CursorLoader(getActivity());
        switch (i) {
            case LOADER_COUNTRY:
                cl.setUri(contentUri);
                cl.setSelection(WeatherProvider.FIELD_COUNTRY + "=?");
                cl.setSelectionArgs(new String[]{country});
                break;
            case LOADER_SITE:
                cl.setUri(contentUri);
             //   cl.setSelection(WeatherProvider.FIELD_SITE_NAME + "=?");
             //   cl.setSelectionArgs(new String[]{siteName});
                break;
        }


        return cl;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        if (null == cursor) {
            return;
        }

        if (cursor.moveToFirst()) {
            switch (loader.getId()) {
                case LOADER_COUNTRY:
                    double lat = cursor.getDouble(cursor.getColumnIndex(WeatherProvider.FIELD_LAT));
                    double lng = cursor.getDouble(cursor.getColumnIndex(WeatherProvider.FIELD_LNG));

                   // siteName = LocationUtils.getNearbySite(getContext(), lat, lng);

                    // getLoaderManager().restartLoader(LOADER_SITE, null, this);
                    break;
                case LOADER_SITE:
                    ArrayList<SiteInfo> infos = LocationUtils.getNearestSiteArray(cursor, location);

                    SiteInfo info = infos.get(0);
                    updateSitePM25Info(info.getName(), info.getCountry(), info.getPm25() +"", info.getUpdateTime());

                    SiteAdapter adapter = new SiteAdapter(getContext(), new ArrayList<>(infos.subList(1, 5)));
                    recyclerView.setAdapter(adapter);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onSelected(String country) {
        location = LocationUtils.getLocationByName(getContext(), country);
        updateLocation(location);
    }

    public void updateLocation(Location location) {
        this.location = location;

        PreferenceUtils.setLastCLocation(getContext(), location);
        country = LocationUtils.getNearbyCountry(getContext(), location.getLatitude(), location.getLongitude());
        siteName = LocationUtils.getNearbySite(getContext(), location.getLatitude(), location.getLongitude());

        // TODO: 更新觀測站資訊
       // getLoaderManager().restartLoader(LOADER_COUNTRY, null, this);
        getLoaderManager().restartLoader(LOADER_SITE, null, this);
    }

    private void updateSitePM25Info(String name, String country, String pm25, String time) {
        tvSite.setText(name);
        tvLocation.setText(country);
        tvPM25.setText(pm25);
        tvTime.setText(time);
    }
}
