package com.barry.outside.syncadapter;

import android.database.Cursor;
import android.location.Location;

import com.barry.outside.CoordinateTransform;
import com.barry.outside.LocationUtils;
import com.barry.outside.provider.WeatherProvider;

/**
 * Created by Mac on 15/11/21.
 */
public class UVInfo {
    String siteName;
    String country;
    String updateTime;
    double UV;
    double lat;
    double lng;

    public double distance;

    public UVInfo(Cursor c) {
        siteName = c.getString(c.getColumnIndex(WeatherProvider.FIELD_SITE_NAME));
        country = c.getString(c.getColumnIndex(WeatherProvider.FIELD_COUNTRY));
        updateTime = c.getString(c.getColumnIndex(WeatherProvider.FIELD_TIME));
        UV = c.getDouble(c.getColumnIndex(WeatherProvider.FIELD_UV));

        CoordinateTransform transform = new CoordinateTransform();

        String latWGS = c.getString(c.getColumnIndex(WeatherProvider.FIELD_LAT_WGS));
        String lngWGS = c.getString(c.getColumnIndex(WeatherProvider.FIELD_LNG_WGS));

        try {
            String lngs[] = lngWGS.split(",");

            int lonD = Integer.valueOf(lngs[0]);
            int lonM = Integer.valueOf(lngs[1]);
            int lonS = Integer.valueOf(lngs[2]);

            String lats[] = latWGS.split(",");
            int latD = Integer.valueOf(lats[0]);
            int latM = Integer.valueOf(lats[1]);
            int latS =  Integer.valueOf(lats[2]);

            String TWD = transform.lonlat_To_twd97(lonD, lonM, lonS, latD, latM, latS);
            String TWDs[] = TWD.split(",");
            lat = Double.valueOf(TWDs[0]);
            lng = Double.valueOf(TWDs[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void caculateDistance(Location location) {
        distance = LocationUtils.getDistance(lat, lng, location.getLatitude(), location.getLongitude());
    }

    public String getSiteName() {return siteName;}
    public String getCountry() {return country;}
    public String getUpdateTime() {return updateTime;}
    public double getUV() {return UV;}
    public double getLat() {return lat;}
    public double getLng() {return lng;}
}
