package com.barry.outside;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.barry.outside.provider.WeatherProvider;
import com.barry.outside.network.WeatherSyncAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ConnectBuilder {

    private static final String TAG = "ConnectBuilder";
    public static final String RESPONSE_UNAUTHORIZED = "_unauthorized";
    public static final String RESPONSE_SUCCESS = "_success";
    public static final String RESPONSE_ERROR = "_error";
    private Context m_context;
    private String m_url;
    private String m_method = "GET";
    private String m_token;
    private String m_contentType;
    private byte[] m_body;
    private OnResponseListener m_responseListener;
    private Bundle m_response = new Bundle();

    public ConnectBuilder(Context context) {
        m_context = context;
    }

    public ConnectBuilder setUrl(String url) {
        m_url = url;
        return this;
    }

    public ConnectBuilder setMethod(String method) {
        m_method = method;
        return this;
    }

    public ConnectBuilder setToken(String token) {
        m_token = token;
        return this;
    }

    public ConnectBuilder setBody(byte[] body, String contentType) {
        m_body = body;
        m_contentType = contentType;
        return this;
    }

    public ConnectBuilder setBody(JSONObject json) {
        try {
            Log.d(TAG, "BODY " + json);
            m_body = json.toString().getBytes("UTF-8");
            m_contentType = "application/json; charset=utf-8";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ConnectBuilder setBody(JSONArray json) {
        try {
            Log.d(TAG, "BODY " + json);
            m_body = json.toString().getBytes("UTF-8");
            m_contentType = "application/json; charset=utf-8";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ConnectBuilder setBody(ContentValues cv) {
        try {
            String data = "";
            for (String key : cv.keySet()) {
                if (data.length() > 0)
                    data += "&";
                data += URLEncoder.encode(key, "UTF-8");
                data += "=";
                data += URLEncoder.encode(cv.getAsString(key), "UTF-8");
            }
            Log.d(TAG, "BODY " + data);
            m_body = data.getBytes("UTF-8");
            m_contentType = "application/x-www-form-urlencoded; charset=utf-8";
        } catch (UnsupportedEncodingException e) {
            m_body = null;
        }
        return this;
    }

    public ConnectBuilder setOnResponseListener(OnResponseListener l) {
        m_responseListener = l;
        return this;
    }

    public interface OnResponseListener {
        void onError(Exception e);

        void onUnauthorized(String url, String token);

        void onResponse(int resCode, InputStream is);
    }

    public void open() {
        try {
            URL url = new URL(m_url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30 * 1000);
            conn.setConnectTimeout(30 * 1000);

            conn.setRequestMethod(m_method);

            if (m_token != null)
                conn.setRequestProperty("Authorization", m_token);

            if (m_body != null) {
                conn.setFixedLengthStreamingMode(m_body.length);
                conn.setRequestProperty("Content-Type", m_contentType);
                conn.setDoOutput(true);
            }

            if (m_responseListener != null)
                conn.setDoInput(true);

            if (m_body != null) {
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(m_body);
                wr.close();
            }

            Log.d(TAG, m_method + " " + url);

            m_response.clear();
            int resCode = conn.getResponseCode();
            switch (resCode) {
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    if (m_responseListener != null)
                        m_responseListener.onUnauthorized(m_url, m_token);
                    else
                        m_response.putString(RESPONSE_UNAUTHORIZED, m_url);
                    break;

                default:
                    InputStream is = conn.getInputStream();
                    if (m_responseListener != null)
                        m_responseListener.onResponse(resCode, is);
                    else
                        m_response.putString(RESPONSE_SUCCESS, URLUtils.convertStreamToString(is));
                    is.close();
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            if (m_responseListener != null)
                m_responseListener.onError(e);
            else
                m_response.putString(RESPONSE_ERROR, e.getMessage());
        }
    }

    public Bundle getResponse() {
        return m_response;
    }

    public static class DummyResponseListener implements OnResponseListener {

        protected Context m_context;
        protected ContentProviderClient m_providerClient;
        protected Account m_account;

        public DummyResponseListener(Context context) {
            m_context = context;
        }

        public DummyResponseListener(Context context, ContentProviderClient providerClient, Account account) {
            super();
            m_context = context;
            m_providerClient = providerClient;
            m_account = account;
        }

        @Override
        public void onError(Exception e) {
            Log.d(TAG, "onError " + e.getMessage());
            Intent intent = new Intent(WeatherSyncAdapter.BROADCAST_SYNC_ERROR);
            intent.putExtra(WeatherSyncAdapter.ARG_BROADCAST_ERROR, e);
            m_context.sendBroadcast(intent);
        }

        @Override
        public void onUnauthorized(String url, String token) {
            Log.d(TAG, "onUnauthorized " + url + " " + token);
          //  AccountManager.get(m_context).invalidateAuthToken(m_context.getString(R.string.oauth_token_type), token);
        }

        @Override
        public void onResponse(int resCode, InputStream is) {
            Log.d(TAG, "onResponse " + resCode + " " + URLUtils.convertStreamToString(is));
        }
    }

    public static class RemoverItemResponseListener extends DummyResponseListener {

        protected Uri m_uri;
        protected int m_id;

        public RemoverItemResponseListener(Context context, ContentProviderClient providerClient, Account account, Uri uri, int id) {
            super(context, providerClient, account);
            m_uri = uri;
            m_id = id;
        }

        @Override
        public void onResponse(int resCode, InputStream is) {
            switch (resCode) {
                case HttpURLConnection.HTTP_CREATED:
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    try {
                        super.onResponse(resCode, is);
                        m_providerClient.delete(m_uri, WeatherProvider.FIELD_ID + "=?", new String[]{Integer.toString(m_id)});
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}

