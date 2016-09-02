package com.barry.outside;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
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

public class HomeFragment extends Fragment {
    FrameLayout mAirInfo;
    FrameLayout mMapInfo;
    ImageView mDrag;

    boolean isCollapse = false;
    TranslateAnimation mExpand;
    TranslateAnimation mCollapse;
    RotateAnimation mRotateToInverse;
    RotateAnimation mRotateToOrigin;

    float mMapPosition;
    int mOriginHeight;
    int mScreenHeight;
    int mToolbarHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAirInfo = (FrameLayout) view.findViewById(R.id.containerAirInfo);
        mMapInfo = (FrameLayout) view.findViewById(R.id.mapInfo);

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
                } else {
                    params.height = mScreenHeight;
                    animation = mCollapse;
                    rotateAnimation = mRotateToInverse;
                    sendBroadCast(BroadcastConst.BROADCAST_COLLAPSE);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mAirInfo.postDelayed(new Runnable() {
            @Override
            public void run() {
                prepareAnimation();
                mDrag.setEnabled(true);
            }
        }, 2000);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_localization) {
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareAnimation() {
        mMapPosition = mMapInfo.getTop();
        mOriginHeight = mMapInfo.getMeasuredHeight();
        mToolbarHeight = ((AppCompatActivity) getActivity()).getSupportActionBar().getHeight();
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
}
