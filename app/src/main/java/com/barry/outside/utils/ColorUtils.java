package com.barry.outside.utils;

import android.content.Context;

import com.barry.outside.R;

/**
 * Created by Mac on 15/11/20.
 */
public class ColorUtils {

    public static int getPM25Color(Context context, int pm25) {
        int colors[] = context.getResources().getIntArray(R.array.color_pm25_array);
        if (pm25 < 11) {
            return colors[0];
        } else if (pm25 >= 11 && pm25 < 35) {
            return colors[1];
        } else if (pm25 >= 25 && pm25 < 53) {
            return colors[2];
        } else if (pm25 >= 53 && pm25 < 70) {
            return colors[3];
        } else {
            return colors[4];
        }
    }

    public static int getPSIColor(Context context, int psi) {
        int colors[] = context.getResources().getIntArray(R.array.color_pm25_array);
        if (psi < 50) {
            return colors[0];
        } else if (psi >= 50 && psi < 100) {
            return colors[1];
        } else if (psi >= 100 && psi < 200) {
            return colors[2];
        } else if (psi >= 200 && psi < 299) {
            return colors[3];
        } else {
            return colors[4];
        }
    }

    public static int getPM10(Context context, int pm10) {
        int colors[] = context.getResources().getIntArray(R.array.color_pm25_array);
        if (pm10 < 50) {
            return colors[0];
        } else if (pm10 >= 50 && pm10 < 150) {
            return colors[1];
        } else if (pm10 >= 150 && pm10 < 350) {
            return colors[2];
        } else if (pm10 >= 350 && pm10 < 420) {
            return colors[3];
        } else {
            return colors[4];
        }
    }

    public static int getO3(Context context, float o3) {
        int colors[] = context.getResources().getIntArray(R.array.color_pm25_array);
        if (o3 < 60) {
            return colors[0];
        } else if (o3 >= 60 && o3 < 120) {
            return colors[1];
        } else if (o3 >= 120 && o3 < 400) {
            return colors[2];
        } else if (o3 >= 400 && o3 < 500) {
            return colors[3];
        } else {
            return colors[4];
        }
    }

    public static int getCO(Context context, float co) {
        int colors[] = context.getResources().getIntArray(R.array.color_pm25_array);
        if (co < 4.5) {
            return colors[0];
        } else if (co >= 4.5 && co < 9) {
            return colors[1];
        } else if (co >= 9 && co < 15) {
            return colors[2];
        } else if (co >= 15 && co < 30) {
            return colors[3];
        } else {
            return colors[4];
        }
    }
}
