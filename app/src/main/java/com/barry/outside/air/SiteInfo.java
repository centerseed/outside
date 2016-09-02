package com.barry.outside.air;

import android.database.Cursor;
import android.location.Location;

import com.barry.outside.provider.WeatherProvider;
import com.barry.outside.utils.LocationUtils;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mac on 15/11/20.
 */
public class SiteInfo {
    String name;
    String country;
    String updateTime;
    int pm25;
    float PSI;
    float O3;
    float CO;
    int pm10;
    double distance;
    double lat;
    double lng;

    public SiteInfo(Cursor c) {
        name = c.getString(c.getColumnIndex(WeatherProvider.FIELD_SITE_NAME));
        country = c.getString(c.getColumnIndex(WeatherProvider.FIELD_COUNTRY));
        updateTime = c.getString(c.getColumnIndex(WeatherProvider.FIELD_TIME));
        pm25 = c.getInt(c.getColumnIndex(WeatherProvider.FIELD_PM25));
        pm10 = c.getInt(c.getColumnIndex(WeatherProvider.FIELD_PM10));
        PSI = c.getFloat(c.getColumnIndex(WeatherProvider.FIELD_PSI));
        O3 = c.getFloat(c.getColumnIndex(WeatherProvider.FIELD_O3));
        CO = c.getFloat(c.getColumnIndex(WeatherProvider.FIELD_CO));

        lat = c.getDouble(c.getColumnIndex(WeatherProvider.FIELD_LAT));
        lng = c.getDouble(c.getColumnIndex(WeatherProvider.FIELD_LNG));
    }

    public void caculateDistance(Location location) {
        distance = LocationUtils.getDistance(lat, lng, location.getLatitude(), location.getLongitude());
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public int getPm25() {
        return pm25;
    }

    public int getPm10() {
        return pm10;
    }

    public float getPSI() {
        return PSI;
    }

    public float getO3() {
        return O3;
    }

    public float getCO() {
        return CO;
    }

    public double getDistance() {
        return distance;
    }

    public LatLng getLatlng() {
        return new LatLng(lat, lng);
    }
}
