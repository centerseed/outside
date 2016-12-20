package com.barry.outside.utils;

import android.content.Context;

import com.barry.outside.R;

/**
 * Created by Mac on 15/11/20.
 */
public class ColorUtils {

    public static int getPM25Color(Context context, int pm25) {
        int colors[] = context.getResources().getIntArray(R.array.color_pm25_array);
        if (pm25 < 15) {
            return colors[0];
        } else if (pm25 >= 15 && pm25 < 35) {
            return colors[1];
        } else if (pm25 >= 35 && pm25 < 54) {
            return colors[2];
        } else if (pm25 >= 54 && pm25 < 150) {
            return colors[3];
        } else {
            return colors[4];
        }
    }

    public static int getPSIColor(Context context, float psi) {
        int colors[] = context.getResources().getIntArray(R.array.color_pm25_array);
        if (psi < 51) {
            return colors[0];
        } else if (psi >= 51 && psi < 101) {
            return colors[1];
        } else if (psi >= 101 && psi < 151) {
            return colors[2];
        } else if (psi >= 151 && psi < 201) {
            return colors[3];
        } else {
            return colors[4];
        }
    }

    public static int getPM10(Context context, int pm10) {
        int colors[] = context.getResources().getIntArray(R.array.color_pm25_array);
        if (pm10 < 54) {
            return colors[0];
        } else if (pm10 >= 55 && pm10 < 125) {
            return colors[1];
        } else if (pm10 >= 125 && pm10 < 254) {
            return colors[2];
        } else if (pm10 >= 254 && pm10 < 354) {
            return colors[3];
        } else {
            return colors[4];
        }
    }

    public static int getO3(Context context, float o3) {
        int colors[] = context.getResources().getIntArray(R.array.color_pm25_array);
        if (o3 < 54) {
            return colors[0];
        } else if (o3 >= 55 && o3 < 71) {
            return colors[1];
        } else if (o3 >= 71 && o3 < 86) {
            return colors[2];
        } else if (o3 >= 86 && o3 < 105) {
            return colors[3];
        } else {
            return colors[4];
        }
    }

    public static int getCO(Context context, float co) {
        int colors[] = context.getResources().getIntArray(R.array.color_pm25_array);
        if (co < 4.4) {
            return colors[0];
        } else if (co >= 4.5 && co < 9.5) {
            return colors[1];
        } else if (co >= 9.5 && co < 12.5) {
            return colors[2];
        } else if (co >= 12.4 && co < 15.5) {
            return colors[3];
        } else {
            return colors[4];
        }
    }
}
