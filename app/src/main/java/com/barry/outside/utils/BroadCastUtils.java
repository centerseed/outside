package com.barry.outside.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastUtils {
    public static void sendParcelableBroadcast(Activity activity, int action, String extraName, Parcelable parcelable) {
        if (activity == null) return;
        Intent intent = new Intent();
        intent.setAction(action + "");
        intent.putExtra(extraName, parcelable);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
