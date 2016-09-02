package com.barry.outside.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.barry.outside.BroadcastConst;
import com.google.android.gms.maps.GoogleMap;

abstract public class BroadcastFragment extends ContentFragment {

    protected BroadcastReceiver receiver;
    protected IntentFilter filter;

    @Override
    public void onStart() {
        super.onStart();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (null != intent) {
                    try {
                        int action = Integer.valueOf(intent.getAction());
                        onReceiveBroadcast(action, intent);
                    } catch (Exception e) {

                    }
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        addIntentFilter(intentFilter);
        getContext().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(receiver);
    }

    public abstract void addIntentFilter(IntentFilter filter);
    public abstract void onReceiveBroadcast(int action, Intent intent);
}
