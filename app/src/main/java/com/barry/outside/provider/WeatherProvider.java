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

/**
 * Created by Owner on 2015/11/13.
 */
public class WeatherProvider extends ContentProvider {

    static String authority;
    public final static String STATUS_UPDATE = "update";

    public final static String TABLE_WEATHER = "_weather";

    public final static String FIELD_ID = "_id";
    public final static String FIELD_COUNTRY = "_country";
    public final static String FIELD_LOCATION = "_location";
    public final static String FIELD_PM25 = "_pm25";
    public final static String FIELD_UV = "_uv";
    public final static String FIELD_TEMPERATURE = "_temperature";
    public final static String FIELD_TIME = "_time";

    protected SQLiteOpenHelper m_db;

    @Override
    public boolean onCreate() {
        authority = getContext().getString(R.string.auth_provider_weather);
        m_db = new WeatherDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        return m_db.getReadableDatabase().query(TABLE_WEATHER, columns, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = m_db.getWritableDatabase().insertWithOnConflict(TABLE_WEATHER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return  m_db.getReadableDatabase().update(TABLE_WEATHER, values, selection, selectionArgs);
    }

    public static Uri getProviderUri(String authority) {
        Uri.Builder ub = new Uri.Builder()
                .scheme("content")
                .authority(authority);
        return ub.build();
    }

    private class WeatherDatabase extends SQLiteOpenHelper {

        private final static int _DBVersion = 2
                ;
        private final static String _DBName = "weather.db";

        public WeatherDatabase(Context context) {
            super(context, _DBName, null, _DBVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_WEATHER + " ( "
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_COUNTRY + " TEXT, "
                    + FIELD_LOCATION + " TEXT, "
                    + FIELD_PM25 + " INTEGER, "
                    + FIELD_UV + " TEXT, "
                    + FIELD_TEMPERATURE + " FLOAT, "
                    + FIELD_TIME + " TEXT "
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);
            onCreate(db);
        }
    }
}
