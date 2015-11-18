package com.barry.outside;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mac on 15/11/16.
 */
public class LocationUtils {
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

    public static double getDistance(double x, double y) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}
