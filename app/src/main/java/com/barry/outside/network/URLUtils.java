package com.barry.outside.network;

import android.content.ContentValues;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Scanner;

public class URLUtils {

    private static final String TAG = "URLUtils";

    public static byte[] makeBody(ContentValues cv) throws UnsupportedEncodingException {
        String data = "";
        for (String key : cv.keySet()) {
            if (data.length() > 0)
                data += "&";
            data += URLEncoder.encode(key, "UTF-8");
            data += "=";
            data += URLEncoder.encode(cv.getAsString(key), "UTF-8");
        }
        byte[] byteData = data.getBytes("UTF-8");
        return byteData;
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
