package com.barry.outside.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.barry.outside.base.BaseContentProvider;

public class WeatherProvider extends BaseContentProvider {

    public final static String TABLE_PM25 = "_table_pm25";

    // PM2.5
    public final static String FIELD_COUNTRY = "_country";
    public final static String FIELD_SITE_NAME = "_location";
    public final static String FIELD_LAT = "_lnt";
    public final static String FIELD_LNG = "_lng";
    public final static String FIELD_PM25 = "_pm25";
    public final static String FIELD_PM10 = "_pm10";
    public final static String FIELD_SO2 = "_so2";
    public final static String FIELD_NO = "_no";
    public final static String FIELD_NO2 = "_no2";
    public final static String FIELD_NOX = "_nox";
    public final static String FIELD_CO = "_co";
    public final static String FIELD_O3 = "_o3";
    public final static String FIELD_PSI = "_psi";
    public final static String FIELD_TEMPERATURE = "_temperature";
    public final static String FIELD_TIME = "_time";

    @Override
    public boolean onCreate() {
        mDb = new WeatherDatabase(getContext());
        return true;
    }

    private class WeatherDatabase extends SQLiteOpenHelper {

        private final static int _DBVersion = 6;
        private final static String _DBName = "weather.db";

        public WeatherDatabase(Context context) {
            super(context, _DBName, null, _DBVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PM25 + " ( "
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_COUNTRY + " TEXT, "
                    + FIELD_SITE_NAME + " TEXT, "
                    + FIELD_PM25 + " INTEGER, "
                    + FIELD_PM10 + " INTEGER, "
                    + FIELD_SO2 + " FLOAT, "
                    + FIELD_CO + " FLOAT, "
                    + FIELD_NO + " FLOAT, "
                    + FIELD_NO2 + " FLOAT, "
                    + FIELD_NOX + " FLOAT, "
                    + FIELD_O3 + " FLOAT, "
                    + FIELD_PSI + " INTEGER, "
                    + FIELD_LAT + " FLOAT, "
                    + FIELD_LNG + " FLOAT, "
                    + FIELD_TIME + " TEXT "
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PM25);
            onCreate(db);
        }
    }
}
