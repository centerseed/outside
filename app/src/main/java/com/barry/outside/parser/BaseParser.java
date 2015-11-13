package com.barry.outside.parser;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.barry.outside.ConnectBuilder;
import com.barry.outside.URLUtils;
import com.barry.outside.syncadapter.WeatherSyncAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by Owner on 2015/11/13.
 */
public abstract class BaseParser implements ConnectBuilder.OnResponseListener {

    private static final String TAG = "BaseParser";
    JSONObject jsonObject;
    protected Context context;
    protected ContentProviderClient providerClient;

    public BaseParser(Context context, ContentProviderClient provider) {
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
        setInputStream(inputStream).parse(jsonObject);
    }

    abstract void parse(JSONObject object);

    public BaseParser setInputStream(InputStream inputStream) {
        String js = URLUtils.convertStreamToString(inputStream);
        try {
            jsonObject = new JSONObject(js);
        } catch (JSONException e) {
            try {
                jsonObject = new JSONObject("{data:" + js + "}");
            } catch (JSONException e1) {
                e.printStackTrace();
            }
        }
        return this;
    }
}
