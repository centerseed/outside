package com.barry.outside.parser;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;

import com.barry.outside.provider.WeatherProvider;

import org.json.JSONObject;

/**
 * Created by Owner on 2015/11/13.
 */
public class pm25Parser extends BaseParser {

    Uri uri;

    public pm25Parser(Context context, ContentProviderClient provider) {
        super(context, provider);
        uri = WeatherProvider.getProviderUri();
    }

    @Override
    void parse(JSONObject object) {
        if (null == object) {
            return;
        }

        ContentValues cv = new ContentValues();
        try {
            providerClient.insert(uri, cv);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
