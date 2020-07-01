package com.android.gallery.map;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.android.gallery.R;
import com.android.gallery.database.ImageEntity;
import com.android.gallery.database.MyAppDatabase;
import com.android.gallery.utils.Init;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static MyAppDatabase myAppDatabase;

    private List<ImageEntity> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Init.getInstance().initComponents(() -> {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(this);

            myAppDatabase = Init.createDatabaseInstance(getApplicationContext());

            images = myAppDatabase.myDao().getAllImages();
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(images != null){
            for(ImageEntity ie : images){
                double latitude = ie.getLatitude();
                double longitude = ie.getLongitude();
                System.err.println(latitude + " | " + longitude);
                LatLng marker = new LatLng(latitude, longitude);

                mMap.addMarker(new MarkerOptions().position(marker).title("Marker"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
            }
        }
    }
}
