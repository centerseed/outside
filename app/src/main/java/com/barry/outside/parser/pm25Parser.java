package com.barry.outside.parser;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.barry.outside.R;
import com.barry.outside.base.BaseJsonParser;
import com.barry.outside.provider.WeatherProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Owner on 2015/11/13.
 */
public class pm25Parser extends BaseJsonParser {

    Uri uri;

    public pm25Parser(Context context, ContentProviderClient provider) {
        super(context, provider);
        uri = WeatherProvider.getProviderUri(context.getString(R.string.auth_provider_weather), WeatherProvider.TABLE_PM25);
    }

    @Override
    public void parse(JSONObject object) {
        if (null == object) {
            return;
        }

        Log.e("pm25Parser", object.toString());

        JSONArray array = object.optJSONArray("data");
        try {
            for (int i = 0; i < array.length(); i++) {
                ContentValues cv = new ContentValues();
                JSONObject jsonObject = array.getJSONObject(i);

                cv.put(WeatherProvider.FIELD_PM25, jsonObject.optInt("PM2.5"));
                cv.put(WeatherProvider.FIELD_PM10, jsonObject.optString("PM10"));
                cv.put(WeatherProvider.FIELD_NO2, jsonObject.optString("NO2"));
                cv.put(WeatherProvider.FIELD_NOX, jsonObject.optString("NOx"));
                cv.put(WeatherProvider.FIELD_SO2, jsonObject.optString("SO2"));
                cv.put(WeatherProvider.FIELD_NO, jsonObject.optString("NO"));
                cv.put(WeatherProvider.FIELD_CO, jsonObject.optString("CO"));
                cv.put(WeatherProvider.FIELD_NO, jsonObject.optString("NO"));
                cv.put(WeatherProvider.FIELD_O3, jsonObject.optString("O3"));
                cv.put(WeatherProvider.FIELD_PSI, jsonObject.optString("PSI"));
                cv.put(WeatherProvider.FIELD_TIME, jsonObject.optString("PublishTime"));
                providerClient.update(uri, cv, WeatherProvider.FIELD_SITE_NAME + "=?", new String[]{jsonObject.optString("SiteName")});
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
