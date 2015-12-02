package com.barry.outside.parser;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.barry.outside.R;
import com.barry.outside.provider.WeatherProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Owner on 2015/11/13.
 */
public class pm25HttpParser extends BaseHttpParser {

    Uri uri;

    public pm25HttpParser(Context context, ContentProviderClient provider) {
        super(context, provider);
        uri = WeatherProvider.getProviderUri(context.getString(R.string.auth_provider_weather), WeatherProvider.TABLE_WEATHER);
    }

    @Override
    void parse(String response) {
        if (null == response) {
            return;
        }

    /*
        JSONArray array = object.optJSONArray("data");
        try {
            for (int i = 0; i < array.length(); i++) {
                ContentValues cv = new ContentValues();
                JSONObject jsonObject = array.getJSONObject(i);
               // cv.put(WeatherProvider.FIELD_ID, jsonObject.optString("SiteName").hashCode());
                cv.put(WeatherProvider.FIELD_LOCATION, jsonObject.optString("SiteName"));
                cv.put(WeatherProvider.FIELD_COUNTRY, jsonObject.optString("County"));
                cv.put(WeatherProvider.FIELD_PM25, jsonObject.optInt("PM2.5"));
                cv.put(WeatherProvider.FIELD_TIME, jsonObject.optInt("PublishTime"));
                providerClient.insert(uri, cv);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }
}
