package com.barry.outside.provider;

import android.accounts.Account;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.barry.outside.R;

import java.util.List;

/**
 * Created by Owner on 2015/11/13.
 */
public class WeatherProvider extends ContentProvider {

    static String authority;
    public final static String STATUS_UPDATE = "update";

    public final static String TABLE_WEATHER = "_weather";
    public final static String TABLE_UV = "_uv";

    // Common Field
    public final static String FIELD_ID = "_id";
    public final static String FIELD_TIME = "_time";

    // PM2.5
    public final static String FIELD_COUNTRY = "_country";
    public final static String FIELD_SITE_NAME = "_location";
    public final static String FIELD_LAT = "_lnt";
    public final static String FIELD_LNG = "_lng";
    public final static String FIELD_PM25 = "_pm25";
    public final static String FIELD_TEMPERATURE = "_temperature";

    // UV
    public final static String FIELD_UV = "_uv";
    public final static String FIELD_LAT_WGS = "_lnt_wgs";
    public final static String FIELD_LNG_WGS = "_lng_swgs";

    protected SQLiteOpenHelper m_db;

    @Override
    public boolean onCreate() {
        authority = getContext().getString(R.string.auth_provider_weather);
        m_db = new WeatherDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        return m_db.getReadableDatabase().query(getTableName(uri), columns, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = getTableName(uri);
        long id = m_db.getWritableDatabase().insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table = getTableName(uri);
        return  m_db.getReadableDatabase().update(table, values, selection, selectionArgs);
    }

    public static Uri getProviderUri(String authority, String table) {
        Uri.Builder ub = new Uri.Builder()
                .scheme("content")
                .authority(authority)
                .appendPath(table);
        return ub.build();
    }

    private class WeatherDatabase extends SQLiteOpenHelper {

        private final static int _DBVersion = 4;
        private final static String _DBName = "weather.db";

        public WeatherDatabase(Context context) {
            super(context, _DBName, null, _DBVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_WEATHER + " ( "
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_COUNTRY + " TEXT, "
                    + FIELD_SITE_NAME + " TEXT, "
                    + FIELD_PM25 + " INTEGER, "
                    + FIELD_LAT + " FLOAT, "
                    + FIELD_LNG + " FLOAT, "
                    + FIELD_TEMPERATURE + " FLOAT, "
                    + FIELD_TIME + " TEXT "
                    + ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_UV + " ( "
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_SITE_NAME + " TEXT, "
                    + FIELD_COUNTRY + " TEXT, "
                    + FIELD_LAT_WGS + " TEXT, "
                    + FIELD_LNG_WGS + " TEXT, "
                    + FIELD_UV + " FLOAT, "
                    + FIELD_TIME + " TEXT "
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);
            onCreate(db);
        }
    }

    private String getTableName(Uri uri) {
        List<String> paths = uri.getPathSegments();
        if (paths != null && paths.size() > 0)
            return paths.get(0);
        return null;
    }
}
