package com.barry.outside.network;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;

import com.barry.outside.R;
import com.barry.outside.provider.WeatherProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AQIParser {
    Uri mUri;
    ContentResolver mResolver;

    public AQIParser(Context context) {
        mUri = WeatherProvider.getProviderUri(context.getString(R.string.auth_provider_weather), WeatherProvider.TABLE_PM25);
        mResolver = context.getContentResolver();
    }

    public void parse(String data) {
        try {
            JSONObject object = new JSONObject(data);
            JSONArray array = object.optJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                ContentValues cv = new ContentValues();
                JSONObject jsonObject = array.getJSONObject(i);

                cv.put(WeatherProvider.FIELD_PM25, jsonObject.optInt("PM2.5"));
                cv.put(WeatherProvider.FIELD_PM10, jsonObject.optString("PM10").length() == 0 ? "0" : jsonObject.optString("PM10"));
                cv.put(WeatherProvider.FIELD_NO2, jsonObject.optString("NO2"));
                cv.put(WeatherProvider.FIELD_NOX, jsonObject.optString("NOx"));
                cv.put(WeatherProvider.FIELD_SO2, jsonObject.optString("SO2"));
                cv.put(WeatherProvider.FIELD_NO, jsonObject.optString("NO").length() == 0 ? "0" : jsonObject.optString("NO"));
                cv.put(WeatherProvider.FIELD_CO, jsonObject.optString("CO").length() == 0 ? "0" : jsonObject.optString("CO"));
                cv.put(WeatherProvider.FIELD_NO, jsonObject.optString("NO"));
                cv.put(WeatherProvider.FIELD_O3, jsonObject.optString("O3").length() == 0? "0" : jsonObject.optString("O3"));
                cv.put(WeatherProvider.FIELD_PSI, jsonObject.optString("AQI"));
                cv.put(WeatherProvider.FIELD_TIME, jsonObject.optString("PublishTime"));
                mResolver.update(mUri, cv, WeatherProvider.FIELD_SITE_NAME + "=?", new String[]{jsonObject.optString("SiteName")});
            }

            mResolver.notifyChange(mUri, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
