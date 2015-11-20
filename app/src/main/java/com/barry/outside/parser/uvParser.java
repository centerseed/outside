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
public class uvParser extends BaseJsonParser {

    Uri uri;

    public uvParser(Context context, ContentProviderClient provider) {
        super(context, provider);
        uri = WeatherProvider.getProviderUri(context.getString(R.string.auth_provider_weather));
    }

    @Override
    void parse(JSONObject object) {
        if (null == object) {
            return;
        }

        Log.d("pm25Parser", object.toString());

        JSONArray array = object.optJSONArray("data");
        try {
            for (int i = 0; i < array.length(); i++) {
                ContentValues cv = new ContentValues();
                JSONObject jsonObject = array.getJSONObject(i);
              //  cv.put(WeatherProvider.FIELD_ID, jsonObject.optString("SiteName").hashCode());
                cv.put(WeatherProvider.FIELD_SITE_NAME, jsonObject.optString("SiteName"));
                cv.put(WeatherProvider.FIELD_COUNTRY, jsonObject.optString("County"));
                cv.put(WeatherProvider.FIELD_UV, jsonObject.optInt("PM2.5"));
                cv.put(WeatherProvider.FIELD_TIME, jsonObject.optInt("PublishTime"));
                providerClient.insert(uri, cv);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
