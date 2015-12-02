package com.barry.outside.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.barry.outside.BroadcastConst;
import com.barry.outside.ColorUtils;
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
public class AirFragment extends BroadcastFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final int LOADER_SITE = 1;

    Uri contentUri;
    TextView tvPM25;
    TextView tvSite;
    TextView tvTime;
    Location location;

    String country = "";
    String siteName = "";

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        contentUri = WeatherProvider.getProviderUri(getContext().getResources().getString(R.string.auth_provider_weather), WeatherProvider.TABLE_WEATHER);
        return inflater.inflate(R.layout.fragment_air, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSite = (TextView) view.findViewById(R.id.tv_site);
        tvPM25 = (TextView) view.findViewById(R.id.tv_pm25);
        tvTime = (TextView) view.findViewById(R.id.tv_update_time);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        f.addAction(BroadcastConst.BROADCAST_GET_SITEINFO + "");
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
                Log.e(AirFragment.class.getName(), "get location " + location.toString() );
                break;
            case BroadcastConst.BROADCAST_UPDATING_LOCATION:
                break;
            case BroadcastConst.BROADCAST_GET_SITEINFO:
                getLoaderManager().restartLoader(LOADER_SITE, null, this);
                break;
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cl = new CursorLoader(getActivity());
        cl.setUri(contentUri);
        Log.e(AirFragment.class.getName(), "onCreateLoader " + cl);

        return cl;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        if (null == cursor || null == location) {
            return;
        }

        if (cursor.moveToFirst()) {
            switch (loader.getId()) {
                case LOADER_SITE:
                    Log.e(AirFragment.class.getName(), "LOADER_SITE " + cursor.getCount());
                    ArrayList<SiteInfo> infos = LocationUtils.getNearestSiteArray(cursor, location);

                    SiteInfo info = infos.get(0);
                    updateSitePM25Info(info.getName(), info.getCountry(), info.getPm25(), info.getUpdateTime());

                    SiteAdapter adapter = new SiteAdapter(getContext(), new ArrayList<>(infos.subList(1, 10)));
                    recyclerView.setAdapter(adapter);
                    break;
            }
        } else {
            Log.e(AirFragment.class.getName(), "Cursor error ");
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        Log.e(AirFragment.class.getName(), "onLoaderReset ");
        getLoaderManager().restartLoader(LOADER_SITE, null, this);
    }

    public void updateLocation(Location location) {
        this.location = location;

        PreferenceUtils.setLastCLocation(getContext(), location);
        country = LocationUtils.getNearbyCountry(getContext(), location.getLatitude(), location.getLongitude());
        siteName = LocationUtils.getNearbySite(getContext(), location.getLatitude(), location.getLongitude());

        getLoaderManager().restartLoader(LOADER_SITE, null, this);
    }

    private void updateSitePM25Info(String name, String country, int pm25, String time) {
        tvSite.setText(name);
        tvPM25.setText(pm25 + "");
        tvPM25.setTextColor(ColorUtils.getColor(getContext(), pm25));
        tvTime.setText(time);
    }
}
