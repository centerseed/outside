package com.barry.outside;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.barry.outside.provider.WeatherProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        new LoadingSiteTask().execute();
    }

    class LoadingSiteTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String authority = getResources().getString(R.string.auth_provider_weather);
                Uri uri = WeatherProvider.getProviderUri(authority, WeatherProvider.TABLE_PM25);

                JSONArray array = new JSONArray(getString(R.string.site_infos));
                for (int i = 0; i < array.length(); i++) {
                    ContentValues cv = new ContentValues();
                    JSONObject jsonObject = array.getJSONObject(i);

                    String siteName = jsonObject.optString("SiteName");
                    cv.put(WeatherProvider.FIELD_ID, (siteName).hashCode());
                    cv.put(WeatherProvider.FIELD_SITE_NAME, jsonObject.optString("SiteName"));
                    cv.put(WeatherProvider.FIELD_COUNTRY, jsonObject.optString("County"));
                    cv.put(WeatherProvider.FIELD_LAT, jsonObject.optDouble("TWD97Lat"));
                    cv.put(WeatherProvider.FIELD_LNG, jsonObject.optDouble("TWD97Lon"));
                    getContentResolver().insert(uri, cv);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Intent intent = new Intent(InitActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            finish();
        }
    }
}
