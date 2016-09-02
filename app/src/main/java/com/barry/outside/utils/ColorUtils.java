package com.barry.outside.utils;

import android.content.Context;
import android.graphics.Color;

import com.barry.outside.R;

import java.util.Arrays;
import java.util.List;

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
        } else if (pm25 >=25 && pm25 < 53) {
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
        } else if (psi >= 51 && psi < 100) {
            return colors[1];
        } else if (psi >=101 && psi < 199) {
            return colors[2];
        } else if (psi >= 200 && psi < 299) {
            return colors[3];
        } else {
            return colors[4];
        }
    }
}
