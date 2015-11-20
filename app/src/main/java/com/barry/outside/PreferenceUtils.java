package com.barry.outside;

import android.content.Context;
import android.location.Location;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by Mac on 15/11/17.
 */
public class PreferenceUtils {

    public static void setLastCLocation(Context context, Location location) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat("last_lat", (float) location.getLatitude()).commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat("last_lng", (float) location.getLongitude()).commit();
    }

    public static Location getLastLocation(Context context) {
        Location location = new Location("");
        location.setLatitude(PreferenceManager.getDefaultSharedPreferences(context).getFloat("last_lat", (float) 25.0329636));
        location.setLongitude(PreferenceManager.getDefaultSharedPreferences(context).getFloat("last_lng", (float) 121.5654268));
        return location;
    }

    public static void setAutoLocalize(Context context, boolean b) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("auto_localize", b).commit();
    }

    public static boolean getAutoLocalize(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("auto_localize", true);
    }
}
