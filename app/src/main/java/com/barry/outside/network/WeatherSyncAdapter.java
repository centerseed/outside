package com.barry.outside.network;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.barry.outside.BroadcastConst;
import com.barry.outside.ConnectBuilder;
import com.barry.outside.R;
import com.barry.outside.parser.SiteParser;
import com.barry.outside.parser.pm25Parser;
import com.barry.outside.parser.uvParser;
import com.barry.outside.provider.WeatherProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        Uri uri = WeatherProvider.getProviderUri(authority, WeatherProvider.TABLE_WEATHER);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        ConnectBuilder connectBuilder = new ConnectBuilder(getContext());

        if (cursor.getCount() == 0) {
            try {
                JSONArray array = new JSONArray(getContext().getString(R.string.site_infos));
                for (int i = 0; i < array.length(); i++) {
                    ContentValues cv = new ContentValues();
                    JSONObject jsonObject = array.getJSONObject(i);

                    String siteName = jsonObject.optString("SiteName");
                    cv.put(WeatherProvider.FIELD_ID, (siteName).hashCode());
                    cv.put(WeatherProvider.FIELD_SITE_NAME, jsonObject.optString("SiteName"));
                    cv.put(WeatherProvider.FIELD_COUNTRY, jsonObject.optString("County"));
                    cv.put(WeatherProvider.FIELD_LAT, jsonObject.optDouble("TWD97Lat"));
                    cv.put(WeatherProvider.FIELD_LNG, jsonObject.optDouble("TWD97Lon"));
                    provider.insert(uri, cv);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();

        connectBuilder.setMethod("GET")
               // .setUrl("http://opendata.epa.gov.tw/webapi/api/rest/datastore/355000000I000001?sort=SiteName&format=json")
                .setUrl("http://opendata.epa.gov.tw/ws/Data/AQX/?format=json")
                .setOnResponseListener(new pm25Parser(getContext(), provider))
        .open();

        connectBuilder.setMethod("GET")
                .setUrl("http://opendata.epa.gov.tw/ws/Data/UV/?format=json")
                .setOnResponseListener(new uvParser(getContext(), provider))
                .open();

        Log.e(WeatherSyncAdapter.class.getName(), " Notify data change " + uri);
        contentResolver.notifyChange(uri, null);
    }
}
