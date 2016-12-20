package com.barry.outside;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.barry.outside.air.AirInfoFragment;
import com.barry.outside.air.AirMapFragment;
import com.barry.outside.utils.BroadcastUtils;
import com.barry.outside.utils.LocationUtils;
import com.barry.outside.utils.PreferenceUtils;

public class HomeFragment extends Fragment implements chooseCountyFragmentDialog.OnSelectedListener {
    public static int REQUEST_GET_LOCATION_PREMISSION = 10000;
    FrameLayout mAirInfo;
    FrameLayout mMapInfo;
    ImageView mDrag;

    boolean isFirstLoad = true;
    boolean isCollapse = false;
    TranslateAnimation mExpand;
    TranslateAnimation mCollapse;
    RotateAnimation mRotateToInverse;
    RotateAnimation mRotateToOrigin;
    LoadingView mLoading;
    MenuItem mLocalize;
    MenuItem mChoosePositon;

    float mMapPosition;
    int mOriginHeight;
    int mScreenHeight;
    int mToolbarHeight;

    LocationManager mLocationManager;
    chooseCountyFragmentDialog mFragmentDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFragmentDialog = new chooseCountyFragmentDialog();
        mFragmentDialog.setSelectedListener(this);

        mAirInfo = (FrameLayout) view.findViewById(R.id.containerAirInfo);
        mMapInfo = (FrameLayout) view.findViewById(R.id.mapInfo);

        mLoading = (LoadingView) view.findViewById(R.id.loading);
        mLoading.setVisibility(View.GONE);

        mDrag = (ImageView) view.findViewById(R.id.drag);
        mDrag.setEnabled(false);
        mDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mMapInfo.getLayoutParams();
                Animation animation;
                RotateAnimation rotateAnimation;
                if (isCollapse) {
                    params.height = mOriginHeight;
                    animation = mExpand;
                    rotateAnimation = mRotateToOrigin;
                    sendBroadCast(BroadcastConst.BROADCAST_EXPAND);
                    mLocalize.setVisible(true);
                    mChoosePositon.setVisible(true);
                } else {
                    params.height = mScreenHeight;
                    animation = mCollapse;
                    rotateAnimation = mRotateToInverse;
                    sendBroadCast(BroadcastConst.BROADCAST_COLLAPSE);
                    mLocalize.setVisible(false);
                    mChoosePositon.setVisible(false);
                }
                mMapInfo.setAnimation(animation);
                animation.setDuration(800);
                animation.startNow();
                mMapInfo.setLayoutParams(params);

                view.setAnimation(rotateAnimation);
                rotateAnimation.startNow();
                isCollapse = !isCollapse;
            }
        });

        AirInfoFragment airInfoFragment = new AirInfoFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.containerAirInfo, airInfoFragment, null).commit();

        AirMapFragment mapFragment = new AirMapFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.containerMap, mapFragment, null).commit();

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            isFirstLoad = false;
            mAirInfo.postDelayed(new Runnable() {
                @Override
                public void run() {
                    prepareAnimation();
                    mDrag.setEnabled(true);
                }
            }, 1000);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_GET_LOCATION_PREMISSION);
            return;
        }

        if (PreferenceUtils.getAutoLocalize(getContext())) {
            mLoading.setVisibility(View.VISIBLE);

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            final Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastLocation != null) {
                mAirInfo.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoading.setVisibility(View.GONE);
                        BroadcastUtils.sendParcelableBroadcast(getActivity(), BroadcastConst.BROADCAST_GET_LOCATION, "location", lastLocation);
                    }
                }, 1000);
            }

            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.removeUpdates(locationListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        mLocalize = menu.findItem(R.id.action_update_position);
        mChoosePositon =  menu.findItem(R.id.action_localization);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_localization) {
            mFragmentDialog.show(getChildFragmentManager(), "");
        }

        if (id == R.id.action_update_position) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return super.onOptionsItemSelected(item);
            }
            Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            BroadcastUtils.sendParcelableBroadcast(getActivity(), BroadcastConst.BROADCAST_GET_LOCATION, "location", lastLocation);
            mLocationManager.requestLocationUpdates(getProviderName(), 0, 0, locationListener);
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareAnimation() {
        mMapPosition = mMapInfo.getTop();
        mOriginHeight = mMapInfo.getMeasuredHeight();
        if (getActivity() == null || ((AppCompatActivity) getActivity()).getSupportActionBar() == null) {
            return;
        }

        mToolbarHeight = ((AppCompatActivity) getActivity()).getSupportActionBar().getHeight();

        int softHeight = getSoftButtonsBarHeight();
        if (softHeight == 0)
            mScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels - 2 * mToolbarHeight - getSoftButtonsBarHeight() + getStatusBarHeight(getContext());
        else
            mScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels - mToolbarHeight - getSoftButtonsBarHeight() + getStatusBarHeight(getContext());

        mCollapse = new TranslateAnimation(0, 0, mMapPosition, 0);
        mExpand = new TranslateAnimation(0, 0, -mMapPosition, 0);

        mRotateToInverse = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateToInverse.setDuration(800);
        mRotateToInverse.setFillAfter(true);

        mRotateToOrigin = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateToOrigin.setDuration(800);
        mRotateToOrigin.setFillAfter(true);
    }

    private int getSoftButtonsBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    protected void sendBroadCast(int action) {
        Intent intent = new Intent();
        intent.setAction(action + "");
        getActivity().sendBroadcast(intent);
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d("location", location.toString());
            mLocationManager.removeUpdates(locationListener);
            PreferenceUtils.setLastLocation(getContext(), location);

            BroadcastUtils.sendParcelableBroadcast(getActivity(), BroadcastConst.BROADCAST_GET_LOCATION, "location", location);
            mLoading.setVisibility(View.GONE);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    public void onSelected(String location) {
        Location lo = LocationUtils.getLocationByName(getContext(), location);
        PreferenceUtils.setLastLocation(getContext(), lo);
        BroadcastUtils.sendParcelableBroadcast(getActivity(), BroadcastConst.BROADCAST_SELECT_LOCATION, "location", lo);
    }

    String getProviderName() {
        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(true); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }
}
