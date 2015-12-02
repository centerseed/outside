package com.barry.outside;

import android.content.Context;
import android.graphics.Color;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mac on 15/11/20.
 */
public class ColorUtils {

    public static int getColor(Context context, int pm25) {
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
}
