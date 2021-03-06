package com.barry.outside.network;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.barry.outside.parser.pm25Parser;
import com.barry.outside.provider.WeatherProvider;

/**
 * Created by Owner on 2015/11/13.
 */
public class WeatherSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String BROADCAST_SYNC_ERROR = "com.barry.outside.SYNC_ERROR";
    public static final String BROADCAST_SYNC_OK = "com.barry.outside.SYNC_OK";
    public static final String ARG_BROADCAST_ERROR = "com.barry.outside";

    protected ContentResolver contentResolver;

    public WeatherSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init(context);
    }

    protected void init(Context context) {
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("WeatherSyncAdapter", "on sync...");

        ConnectBuilder connectBuilder = new ConnectBuilder(getContext());
        connectBuilder.setMethod("GET")
               // .setUrl("http://opendata.epa.gov.tw/webapi/api/rest/datastore/355000000I000001?sort=SiteName&format=json")
                .setUrl("http://opendata.epa.gov.tw/ws/Data/REWIQA/?$orderby=SiteName&$skip=0&$top=1000&format=json")
                .setOnResponseListener(new pm25Parser(getContext(), provider))
        .open();

        Uri uri = WeatherProvider.getProviderUri(authority, WeatherProvider.TABLE_PM25);
        Log.e(WeatherSyncAdapter.class.getName(), " Notify data change " + uri);
        contentResolver.notifyChange(uri, null);
    }
}
