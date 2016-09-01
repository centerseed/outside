package com.barry.outside.air;

import android.content.ContentResolver;
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
import android.widget.TextView;

import com.barry.outside.BroadcastConst;
import com.barry.outside.base.BroadcastFragment;
import com.barry.outside.utils.ColorUtils;
import com.barry.outside.CursorChangedObserver;
import com.barry.outside.utils.LocationUtils;
import com.barry.outside.MapsActivity;
import com.barry.outside.utils.PreferenceUtils;
import com.barry.outside.R;
import com.barry.outside.account.AccountUtil;
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
    String authority;

    RecyclerView recyclerView;
    private CursorChangedObserver cursorObserver;
    ImageView mImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        authority = getContext().getResources().getString(R.string.auth_provider_weather);
        contentUri = WeatherProvider.getProviderUri(authority, WeatherProvider.TABLE_PM25);
        cursorObserver = new CursorChangedObserver(new CursorChangedObserver.OnCursorChangedListener() {
            @Override
            public void onCursorChanged(Cursor c) {
                try {
                    reload();
                } catch (Exception e) {
                }
            }
        });

        getLoaderManager().initLoader(LOADER_SITE, null, this);
        return inflater.inflate(R.layout.fragment_air, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSite = (TextView) view.findViewById(R.id.tv_site);
        tvPM25 = (TextView) view.findViewById(R.id.tv_pm25);

        mImage = (ImageView) view.findViewById(R.id.map);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("location", location);
                intent.putExtra("name", tvSite.getText().toString());
                startActivity(intent);
            }
        });

        tvTime = (TextView) view.findViewById(R.id.tv_update_time);
        ImageView ivReport = (ImageView) view.findViewById(R.id.iv_report);
        ivReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PM25RankActivity.class);
                startActivity(intent);
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

        Bundle args = new Bundle();
        args.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        args.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        getActivity().getContentResolver().requestSync(AccountUtil.getAccount(getActivity()), authority, args);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void addIntentFilter(IntentFilter f) {
        f.addAction(BroadcastConst.BROADCAST_GET_LOCATION + "");
        f.addAction(BroadcastConst.BROADCAST_UPDATING_LOCATION + "");
    }

    @Override
    public void onReceiveBroadcast(int action, Intent intent) {
        switch (action) {
            case BroadcastConst.BROADCAST_GET_LOCATION:
                location = intent.getParcelableExtra("location");

                if (null != location) {
                    updateLocation(location);
                }
                Log.e(AirFragment.class.getName(), "get location " + location.toString());
                break;
            case BroadcastConst.BROADCAST_UPDATING_LOCATION:
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
    protected Uri getProviderUri() {
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        cursorObserver.swapCursor(cursor);
        if (null == cursor) {
            return;
        }

        if (null == location) {
            return;
        }

        if (cursor.moveToFirst()) {
            switch (loader.getId()) {
                case LOADER_SITE:
                    Log.e(AirFragment.class.getName(), "LOADER_SITE " + cursor.getCount());
                    ArrayList<SiteInfo> infos = LocationUtils.getNearestSiteArray(cursor, location);

                    SiteInfo info = infos.get(0);
                    updateSitePM25Info(info.getName(), info.getCountry(), info.getPm25(), info.getUpdateTime());

                    if (infos.size() < 10) {
                        return;
                    }
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
        reload();
    }

    public void updateLocation(Location location) {
        this.location = location;

        PreferenceUtils.setLastCLocation(getContext(), location);
        country = LocationUtils.getNearbyCountry(getContext(), location.getLatitude(), location.getLongitude());
        siteName = LocationUtils.getNearbySite(getContext(), location.getLatitude(), location.getLongitude());

        reload();
    }

    private void updateSitePM25Info(String name, String country, int pm25, String time) {
        tvSite.setText(name);
        tvPM25.setText(pm25 + "");
        tvPM25.setTextColor(ColorUtils.getColor(getContext(), pm25));
        tvTime.setText(time);
    }

    private void reload() {
        getLoaderManager().restartLoader(LOADER_SITE, null, this);
    }
}
