package com.barry.outside.parser;

import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.barry.outside.ConnectBuilder;
import com.barry.outside.URLUtils;
import com.barry.outside.network.WeatherSyncAdapter;

import java.io.InputStream;

/**
 * Created by Owner on 2015/11/13.
 */
public abstract class BaseHttpParser implements ConnectBuilder.OnResponseListener {

    private static final String TAG = "BaseParser";
    String string;
    protected Context context;
    protected ContentProviderClient providerClient;

    public BaseHttpParser(Context context, ContentProviderClient provider) {
        this.context = context;
        providerClient = provider;
    }

    @Override
    public void onError(Exception e) {
        Log.d(TAG, "onError " + e.getMessage());
        Intent intent = new Intent(WeatherSyncAdapter.BROADCAST_SYNC_ERROR);
        intent.putExtra(WeatherSyncAdapter.ARG_BROADCAST_ERROR, e);
        context.sendBroadcast(intent);
    }

    @Override
    public void onUnauthorized(String url, String token) {

    }

    @Override
    public void onResponse(int resCode, InputStream inputStream) {
        setInputStream(inputStream).parse(string);
    }

    abstract void parse(String string);

    public BaseHttpParser setInputStream(InputStream inputStream) {
        string = URLUtils.convertStreamToString(inputStream);
        return this;
    }
}
