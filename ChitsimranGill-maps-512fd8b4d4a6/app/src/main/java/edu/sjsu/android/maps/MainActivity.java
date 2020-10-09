package edu.sjsu.android.maps;


import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final LatLng LOCATION_UNIV = new LatLng(37.335371, -121.881050);
    private final LatLng LOCATION_CS = new LatLng(37.333714,-121.881860);
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
    }

    public void onMapReady(final GoogleMap googleMap){
        map = googleMap;
        getSupportLoaderManager().initLoader(0, null, this);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                drawMarker(latLng);

                ContentValues contentValues = new ContentValues();
                contentValues.put(LocationsDB.LATITUDE, latLng.latitude);
                contentValues.put(LocationsDB.LONGITUDE, latLng.longitude);
                contentValues.put(LocationsDB.ZOOM_LEVEL, googleMap.getCameraPosition().zoom);
                LocationInsertTask insertTask = new LocationInsertTask();
                insertTask.execute(contentValues);
                Toast.makeText(getBaseContext(), "Marker is add to the Map", Toast.LENGTH_SHORT).show();
            }
        });

        map.setOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.clear();
                LocationDeleteTask deleteTask = new LocationDeleteTask();
                deleteTask.execute();
                Toast.makeText(getBaseContext(), "All markers are removed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        map.addMarker(markerOptions);
    }

    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void>{
        @Override
        protected Void doInBackground(ContentValues... contentValues) {
            getContentResolver().insert(LocationsContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class LocationDeleteTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
            return null;
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Loader<Cursor> c = null;
        Uri uri = LocationsContentProvider.CONTENT_URI;
        c = new CursorLoader(this, uri, null, null, null, null);
        return c;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int locationCount = 0;
        double lat = 0;
        double lng = 0;
        float zoom = 0;
        if(data!=null){
            locationCount = data.getCount();
            data.moveToFirst();
        }
        else {
            locationCount = 0;
        }
        for(int i =0; i<locationCount;i++)
        {

            lat = data.getDouble(data.getColumnIndex(LocationsDB.LATITUDE));
            lng = data.getDouble(data.getColumnIndex(LocationsDB.LONGITUDE));
            zoom = data.getFloat(data.getColumnIndex(LocationsDB.ZOOM_LEVEL));
            LatLng location = new LatLng(lat, lng);
            drawMarker(location);
            data.moveToNext();

        }
        if(locationCount>0)
        {

            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
            map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public  void onClick_CS(View v){
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_CS, 18);
        map.animateCamera(update);
    }

    public  void onClick_Univ(View v){
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 14);
        map.animateCamera(update);
    }

    public void onClick_City(View v){
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV,10);
        map.animateCamera(update);
    }
}
