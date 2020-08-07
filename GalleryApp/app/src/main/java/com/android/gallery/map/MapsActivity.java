package com.android.gallery.map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static MyAppDatabase myAppDatabase;

    private List<ImageEntity> images;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_maps, container, false);

        Init.getInstance().initComponents(() -> {

            assert getFragmentManager() != null;
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(this);

            myAppDatabase = Init.createDatabaseInstance(getContext());

            images = myAppDatabase.myDao().getAllImages();
        });

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(images != null){
            int br = 0;
            for(ImageEntity ie : images){
                double latitude = ie.getLatitude();
                double longitude = ie.getLongitude();
                System.err.println(latitude + " | " + longitude);
                LatLng marker = new LatLng(latitude, longitude);

                mMap.addMarker(new MarkerOptions().position(marker).title("Marker " + ++br));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
            }
        }
    }
}
