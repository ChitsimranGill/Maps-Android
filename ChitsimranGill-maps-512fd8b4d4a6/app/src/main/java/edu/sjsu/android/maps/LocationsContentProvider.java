package edu.sjsu.android.maps;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LocationsContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "edu.sjsu.android.maps";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/locations");
    private static final int LCTS = 1;
    private static final UriMatcher uriMatcher ;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "locations", LCTS);
    }

    LocationsDB mLocationDB;

    @Override
    public boolean onCreate() {
        mLocationDB = new LocationsDB(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if(uriMatcher.match(uri)==LCTS){
            return mLocationDB.getAll();
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long ID = mLocationDB.insert(values);
        Uri turi = null;
        if(ID>0){
            turi = ContentUris.withAppendedId(CONTENT_URI, ID);
        } else {
            try {
                throw new SQLException("Failed to insert : " + uri);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return turi;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int tb = 0;
        tb = mLocationDB.delete();
        return tb;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
