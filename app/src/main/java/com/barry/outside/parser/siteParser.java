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
public class SiteParser extends BaseJsonParser {

    Uri uri;

    public SiteParser(Context context, ContentProviderClient provider) {
        super(context, provider);
        uri = WeatherProvider.getProviderUri(context.getString(R.string.auth_provider_weather), WeatherProvider.TABLE_WEATHER);
    }

    @Override
    void parse(JSONObject object) {
        if (null == object) {
            return;
        }

        Log.e("SiteParser", object.toString());

        // JSONArray array = object.optJSONObject("result").optJSONArray("records");
        JSONArray array = object.optJSONArray("data");
        try {
            for (int i = 0; i < array.length(); i++) {
                ContentValues cv = new ContentValues();
                JSONObject jsonObject = array.getJSONObject(i);

                String siteName = jsonObject.optString("SiteName");
                String time = jsonObject.optString("PublishTime");

                cv.put(WeatherProvider.FIELD_ID, (siteName).hashCode());
                cv.put(WeatherProvider.FIELD_SITE_NAME, jsonObject.optString("SiteName"));
                cv.put(WeatherProvider.FIELD_COUNTRY, jsonObject.optString("County"));
                cv.put(WeatherProvider.FIELD_LAT, jsonObject.optDouble("TWD97Lat"));
                cv.put(WeatherProvider.FIELD_LNG, jsonObject.optDouble("TWD97Lon"));
                providerClient.insert(uri, cv);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
