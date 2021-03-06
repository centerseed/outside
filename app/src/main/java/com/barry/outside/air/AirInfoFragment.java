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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.barry.outside.BroadcastConst;
import com.barry.outside.R;
import com.barry.outside.base.BroadcastFragment;
import com.barry.outside.AirInfoTypeFragmentDialog;
import com.barry.outside.provider.WeatherProvider;
import com.barry.outside.utils.BroadcastUtils;
import com.barry.outside.utils.ColorUtils;
import com.barry.outside.utils.LocationUtils;
import com.barry.outside.utils.PreferenceUtils;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import at.grabner.circleprogress.UnitPosition;

public class AirInfoFragment extends BroadcastFragment implements AirInfoTypeFragmentDialog.OnSelectedListener {

    CircleProgressView mCircleProgress;
    Location mLocation;
    TextView mPSI;
    TextView mPM25;
    TextView mPM10;
    TextView mO3;
    TextView mCO;
    TextView mTime;

    ImageView mIconPSI;
    ImageView mIconPM25;
    ImageView mIconPM10;
    ImageView mIconO3;
    ImageView mIconCO;

    int mCurrType = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_air_info, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCircleProgress = (CircleProgressView) view.findViewById(R.id.circleProgress);
        //mCircleProgress.setContourSize(0);
        //mCircleProgress.setBlockCount(20);
        mCircleProgress.setRimColor(Color.argb(130, 200, 200, 200));
        mCircleProgress.setUnitVisible(true);
        mCircleProgress.setTextColor(getResources().getColor(android.R.color.white));
        mCircleProgress.setUnitColor(getResources().getColor(android.R.color.white));
        mCircleProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AirInfoTypeFragmentDialog f = AirInfoTypeFragmentDialog.getInstance(mCurrType);
                f.setSelectedListener(AirInfoFragment.this);
                f.show(getChildFragmentManager(), "");
            }
        });

        mPSI = (TextView) view.findViewById(R.id.psi);
        mPM25 = (TextView) view.findViewById(R.id.pm25);
        mPM10 = (TextView) view.findViewById(R.id.pm10);
        mO3 = (TextView) view.findViewById(R.id.o3);
        mCO = (TextView) view.findViewById(R.id.co);
        mTime = (TextView) view.findViewById(R.id.time);

        mIconPSI = (ImageView) view.findViewById(R.id.ic_psi);
        mIconPM25 = (ImageView) view.findViewById(R.id.ic_pm25);
        mIconPM10 = (ImageView) view.findViewById(R.id.ic_pm10);
        mIconO3 = (ImageView) view.findViewById(R.id.ic_o3);
        mIconCO = (ImageView) view.findViewById(R.id.ic_co);

        mLocation = PreferenceUtils.getLastLocation(getContext());
        mCurrType = PreferenceUtils.getDefaultType(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void addIntentFilter(IntentFilter filter) {
        filter.addAction(BroadcastConst.BROADCAST_GET_LOCATION + "");
        filter.addAction(BroadcastConst.BROADCAST_SELECT_LOCATION + "");
        filter.addAction(BroadcastConst.BROADCAST_CLICK_MAP_POS + "");
    }

    @Override
    public void onReceiveBroadcast(int action, Intent intent) {
        if (action == BroadcastConst.BROADCAST_GET_LOCATION ||
                action == BroadcastConst.BROADCAST_SELECT_LOCATION ||
                action == BroadcastConst.BROADCAST_CLICK_MAP_POS) {

            Location location = intent.getParcelableExtra("location");
            mLocation.setLongitude(location.getLongitude());
            mLocation.setLatitude(location.getLatitude());
            getLoaderManager().restartLoader(0, null, this);
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
            SiteInfo info = LocationUtils.getNearestSiteArray(cursor, mLocation).get(0);
            mCircleProgress.setTextMode(TextMode.TEXT);
            if (mCurrType == 0) {
                mCircleProgress.setMaxValue(150f);
                mCircleProgress.setValueAnimated(info.getPm25());
                mCircleProgress.setText(info.getPm25() + "");
                mCircleProgress.setBarColor(ColorUtils.getPM25Color(getContext(), info.getPm25()));
                mCircleProgress.setUnit(getString(R.string.pm25));
            } else if (mCurrType == 1) {
                mCircleProgress.setMaxValue(354f);
                mCircleProgress.setValueAnimated(info.getPm10());
                mCircleProgress.setText(info.getPm10() + "");
                mCircleProgress.setBarColor(ColorUtils.getPM10(getContext(), info.getPm10()));
                mCircleProgress.setUnit(getString(R.string.pm10));
            } else if (mCurrType == 2) {
                mCircleProgress.setMaxValue(200f);
                mCircleProgress.setValueAnimated(info.getPSI());
                mCircleProgress.setText(info.getPSI() + "");
                mCircleProgress.setBarColor(ColorUtils.getPSIColor(getContext(), info.getPSI()));
                mCircleProgress.setUnit(getString(R.string.PSI));
            } else if (mCurrType == 3) {
                mCircleProgress.setMaxValue(105f);
                mCircleProgress.setValueAnimated(info.getO3());
                mCircleProgress.setText(info.getO3() + "");
                mCircleProgress.setBarColor(ColorUtils.getO3(getContext(), info.getO3()));
                mCircleProgress.setUnit(getString(R.string.O3));
            } else {
                mCircleProgress.setMaxValue(15.4f);
                mCircleProgress.setValueAnimated(info.getCO());
                mCircleProgress.setText(info.getCO() + "");
                mCircleProgress.setBarColor(ColorUtils.getCO(getContext(), info.getCO()));
                mCircleProgress.setUnit(getString(R.string.CO));
            }

            mCircleProgress.setUnitPosition(UnitPosition.BOTTOM);

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_site) + " - " + info.getName());

            mPSI.setText(info.getPSI() + "");
            mPM25.setText(info.getPm25() + "");
            mPM10.setText(info.getPm10() + "");
            mCO.setText(info.getCO() + "");
            mO3.setText(info.getO3() + "");
            mTime.setText(info.getUpdateTime());

            mIconPSI.setColorFilter(ColorUtils.getPSIColor(getContext(), (int) info.getPSI()));
            mIconPM25.setColorFilter(ColorUtils.getPM25Color(getContext(), info.getPm25()));
            mIconPM10.setColorFilter(ColorUtils.getPM10(getContext(), info.getPm10()));
            mIconCO.setColorFilter(ColorUtils.getCO(getContext(), info.getCO()));
            mIconO3.setColorFilter(ColorUtils.getO3(getContext(), info.getO3()));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onSelected(int position) {
        mCurrType = position;
        getLoaderManager().restartLoader(0, null, this);
        PreferenceUtils.setDefaultType(getContext(), mCurrType);

        BroadcastUtils.sendParcelableBroadcast(getActivity(), BroadcastConst.BROADCAST_GET_LOCATION, "location", mLocation);
    }
}
