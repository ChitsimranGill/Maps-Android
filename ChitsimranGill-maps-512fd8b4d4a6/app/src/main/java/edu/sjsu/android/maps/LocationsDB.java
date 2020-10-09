package edu.sjsu.android.maps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocationsDB extends SQLiteOpenHelper {

    public static final String DBNAME = "LocationDB";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "locations";
    public static final String ID_COLUMN = "Primary key";
    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";
    public static final String ZOOM_LEVEL = "Zoom level";
    private SQLiteDatabase mDB;

    public LocationsDB(Context context) {
        super(context, DBNAME, null, DATABASE_VERSION);
        this.mDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + DATABASE_TABLE + " ( " + ID_COLUMN + " integer primary key autoincrement , " +
                LONGITUDE + " double ," + LATITUDE + " double , " + ZOOM_LEVEL + " text " + " ) ";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public long insert(ContentValues contentValues){
        long ID = mDB.insert(DATABASE_TABLE, null, contentValues);
        return ID;
    }

    public int delete(){
        int tb = mDB.delete(DATABASE_TABLE, null, null);
        return tb;
    }

    public Cursor getAll(){
        return mDB.query(DATABASE_TABLE, new String[] { ID_COLUMN, LONGITUDE, LATITUDE, ZOOM_LEVEL}, null, null, null, null, null);
    }
}
