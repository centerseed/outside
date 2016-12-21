package com.barry.outside.network;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AsyncCallback implements Callback {
    Context mContext;

    public AsyncCallback(Context context) {
        mContext = context;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        /*Intent intent = new Intent();
        intent.setAction(ConstantDef.NETWORK_FAIL);
        intent.putExtra(ConstantDef.ARG_STRING, e.toString());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        */
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

    }
}
