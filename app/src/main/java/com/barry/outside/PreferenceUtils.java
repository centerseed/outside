package com.barry.outside;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by Mac on 15/11/17.
 */
public class PreferenceUtils {

    public static void setLastCountry(Context context, String locaiton) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("last_country", locaiton).commit();
    }

    public static String getLastCountry(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("last_country", null);
    }
}
