package com.barry.outside.uv;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.barry.outside.BroadcastConst;
import com.barry.outside.BroadcastFragment;
import com.barry.outside.LocationUtils;
import com.barry.outside.R;
import com.barry.outside.provider.WeatherProvider;
import com.barry.outside.network.UVInfo;

import java.util.ArrayList;

/**
 * Created by Mac on 15/11/21.
 */
public class UVFragment extends BroadcastFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    Uri contentUri;
    TextView tvUV;
    TextView tvTime;
    TextView tvSiteName;
    ImageView ivLevel;

    Location location;
    String country = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        contentUri = WeatherProvider.getProviderUri(getContext().getResources().getString(R.string.auth_provider_weather), WeatherProvider.TABLE_UV);
        return inflater.inflate(R.layout.fragment_uv, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivLevel = (ImageView) view.findViewById(R.id.iv_level);
        tvUV = (TextView) view.findViewById(R.id.tv_uv);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvSiteName = (TextView) view.findViewById(R.id.tv_site_name);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter f = new IntentFilter();
        f.addAction(BroadcastConst.BROADCAST_GET_LOCATION + "");
        f.addAction(BroadcastConst.BROADCAST_GET_SITEINFO + "");
        return f;
    }

    @Override
    public void onReceiveBroadcast(int action, Intent intent) {
        switch (action) {
            case BroadcastConst.BROADCAST_GET_LOCATION:
                location = intent.getParcelableExtra("location");

                if (null != location) {
                    updateLocation(location);
                }
            case BroadcastConst.BROADCAST_GET_SITEINFO:
                if (null != location) {
                    updateLocation(location);
                }
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cl = new CursorLoader(getActivity());
        cl.setUri(contentUri);
        cl.setSelection(WeatherProvider.FIELD_COUNTRY + "=?");
        cl.setSelectionArgs(new String[]{country});
        Log.e(UVFragment.class.getName(), "onCreateLoader " + cl);

        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (null == cursor || location == null) {
            return;
        }

        ArrayList<UVInfo> uvInfos = LocationUtils.getNearestUvSiteArray(cursor, location);
        if (uvInfos.size() == 0) {
            return;
        }

        UVInfo info = uvInfos.get(0);
        tvTime.setText(info.getUpdateTime());
        tvSiteName.setText(info.getSiteName());

        if (info.getUV() < 3) {
            ivLevel.setImageResource(R.drawable.ic_uv_1);
            tvUV.setText("低");
        } else if (info.getUV() < 6) {
            ivLevel.setImageResource(R.drawable.ic_uv_2);
            tvUV.setText("中");
        } else if (info.getUV() < 8) {
            ivLevel.setImageResource(R.drawable.ic_uv_3);
            tvUV.setText("高");
        } else if (info.getUV() < 12) {
            ivLevel.setImageResource(R.drawable.ic_uv_4);
            tvUV.setText("過量");
        } else {
            ivLevel.setImageResource(R.drawable.ic_uv_5);
            tvUV.setText("危險");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void updateLocation(Location location) {
        this.location = location;
        country = LocationUtils.getNearbyCountry(getContext(), location.getLatitude(), location.getLongitude());
        if (country.equals("新竹市")) {
            country = "新竹縣";
        }
        getLoaderManager().restartLoader(0, null, this);
    }
}
