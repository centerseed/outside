package com.barry.outside.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WeatherSyncService extends Service {

    private static WeatherSyncAdapter weatherSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (weatherSyncAdapter == null) {
                weatherSyncAdapter = new WeatherSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return weatherSyncAdapter.getSyncAdapterBinder();
    }
}