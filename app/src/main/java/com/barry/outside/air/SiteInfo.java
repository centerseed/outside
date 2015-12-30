package com.barry.outside.air;

import android.database.Cursor;
import android.location.Location;

import com.barry.outside.LocationUtils;
import com.barry.outside.provider.WeatherProvider;

/**
 * Created by Mac on 15/11/20.
 */
public class SiteInfo {
    String name;
    String country;
    String updateTime;
    int pm25;
    double distance;
    double lat;
    double lng;

    public SiteInfo(Cursor c) {
        name = c.getString(c.getColumnIndex(WeatherProvider.FIELD_SITE_NAME));
        country = c.getString(c.getColumnIndex(WeatherProvider.FIELD_COUNTRY));
        updateTime = c.getString(c.getColumnIndex(WeatherProvider.FIELD_TIME));
        pm25 = c.getInt(c.getColumnIndex(WeatherProvider.FIELD_PM25));

        lat = c.getDouble(c.getColumnIndex(WeatherProvider.FIELD_LAT));
        lng = c.getDouble(c.getColumnIndex(WeatherProvider.FIELD_LNG));
    }

    public void caculateDistance(Location location) {
        distance = LocationUtils.getDistance(lat, lng, location.getLatitude(), location.getLongitude());
    }

    public String getName() { return  name;}
    public String getCountry() { return country;}
    public String getUpdateTime() {return updateTime;}
    public int getPm25() { return pm25; }
    public double getDistance() {return distance;}
}
