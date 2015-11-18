package com.barry.outside.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.barry.outside.BroadcastConst;
import com.barry.outside.LocationUtils;
import com.barry.outside.R;
import com.barry.outside.provider.WeatherProvider;

/**
 * Created by Mac on 15/11/13.
 */
public class AirFragment extends BroadcastFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        chooseCountyFragmentDialog.OnSelectedListener {

    Uri contentUri;
    TextView tvPM25;
    TextView tvLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_air, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvLocation = (TextView) view.findViewById(R.id.tv_location);
        tvPM25 = (TextView) view.findViewById(R.id.tv_pm25);
        tvLocation.setText(getLocation());

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
    }

    @Override
    public void onResume() {
        super.onResume();

        contentUri = WeatherProvider.getProviderUri(getContext().getResources().getString(R.string.auth_provider_weather));
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    IntentFilter getIntentFilter() {
        IntentFilter f = new IntentFilter();
        f.addAction(BroadcastConst.BROADCASE_UPDATE_LOCATION + "");
        return f;
    }

    @Override
    void onReceiveBroadcast(int action, Intent intent) {
        switch (action) {
            case BroadcastConst.BROADCASE_UPDATE_LOCATION:
                Location location = intent.getParcelableExtra("location");
                String nearbyCountry = LocationUtils.getNearbyCountry(getContext(), location.getLatitude(), location.getLongitude());
                tvLocation.setText(nearbyCountry);
                break;
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cl = new CursorLoader(getActivity());
        cl.setUri(contentUri);
 //       cl.setSelection(WeatherProvider.FIELD_LOCATION + "=?");
  //      cl.setSelectionArgs(new String[]{getLocation()});
        return cl;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
    //    int pm25 = cursor.getInt(cursor.getColumnIndex(WeatherProvider.FIELD_PM25));
    //    tvPM25.setText(pm25 + "");
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }


    private String getLocation() {
        return getArguments().getString("location");
    }

    @Override
    public void onSelected(String location) {
        tvLocation.setText(location);

        // TODO: 更新觀測站資訊

    }
}
