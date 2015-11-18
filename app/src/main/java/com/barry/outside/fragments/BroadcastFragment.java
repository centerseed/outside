package com.barry.outside.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Mac on 15/11/16.
 */
abstract public class BroadcastFragment extends Fragment {

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
        IntentFilter intentFilter = getIntentFilter();
        getContext().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(receiver);
    }

    abstract IntentFilter getIntentFilter();
    abstract void onReceiveBroadcast(int action, Intent intent);
}
