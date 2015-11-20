package com.barry.outside;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;

import com.barry.outside.provider.WeatherProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Mac on 15/11/16.
 */
public class LocationUtils {
    private static final double EARTH_RADIUS = 6378137.0;

    public static String getNearbySite(long latitude, long longitude) {
        return "";
    }

    public static String getNearbyCountry(Context c, double latitude, double longitude) {
        List<String> side_name = Arrays.asList(c.getResources().getStringArray(R.array.country));
        List<String> lats = Arrays.asList(c.getResources().getStringArray(R.array.latitude));
        List<String> longs = Arrays.asList(c.getResources().getStringArray(R.array.longitude));

        double minDistance = 99999;
        int minDistanceId = 0;

        for (int i = 0; i < side_name.size(); i++) {
            double lat = Double.valueOf(lats.get(i));
            double lon = Double.valueOf(longs.get(i));

            double distance = getDistance(latitude - lat, longitude - lon);
            if (distance < minDistance) {
                minDistance = distance;
                minDistanceId = i;
            }
        }

        return side_name.get(minDistanceId);
    }

    public static String getNearbySite(Context c, double latitude, double longitude) {

        double distance = 999999999;
        String siteName = "";

        ContentResolver resolver = c.getContentResolver();

        Uri uri = WeatherProvider.getProviderUri(c.getResources().getString(R.string.auth_provider_weather));
        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                double lat = cursor.getDouble(cursor.getColumnIndex(WeatherProvider.FIELD_LAT));
                double lng = cursor.getDouble(cursor.getColumnIndex(WeatherProvider.FIELD_LNG));

                double dis = getDistance(lat - latitude, lng - longitude);

                if (dis < distance) {
                    distance = dis;
                    siteName = cursor.getString(cursor.getColumnIndex(WeatherProvider.FIELD_SITE_NAME));
                }

                cursor.moveToNext();
            }
        }
        cursor.close();

        return siteName;
    }

    public static ArrayList<SiteInfo> getNearestSiteArray(Cursor c, Location location) {
        ArrayList<SiteInfo> siteInfos = new ArrayList<>();

        if (c == null || !c.moveToFirst()) {
            return siteInfos;
        }

        while (!c.isAfterLast()) {
            SiteInfo info = new SiteInfo(c);
            info.caculateDistance(location);
            siteInfos.add(info);
            c.moveToNext();
        }

        Collections.sort(siteInfos, new Comparator<SiteInfo>() {
            @Override
            public int compare(SiteInfo siteInfo, SiteInfo t1) {
                return (int) (siteInfo.distance - t1.distance);
            }
        });

        ArrayList<SiteInfo> subInfos = new ArrayList<SiteInfo>(siteInfos.subList(0, 5));
        return subInfos;
    }

    public static Location getLocationByName(Context c, String name) {
        List<String> side_name = Arrays.asList(c.getResources().getStringArray(R.array.country));
        List<String> lats = Arrays.asList(c.getResources().getStringArray(R.array.latitude));
        List<String> longs = Arrays.asList(c.getResources().getStringArray(R.array.longitude));

        int i = side_name.indexOf(name);

        Location location = new Location("");
        location.setLatitude(Double.valueOf(lats.get(i)));
        location.setLongitude(Double.valueOf(longs.get(i)));

        return location;
    }

    public static double getDistance(double x, double y) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) * 1000000;
    }

    public static double getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
