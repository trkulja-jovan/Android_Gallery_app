package com.android.gallery.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.gallery.R;
import com.android.gallery.adapters.ImageAdapter;
import com.android.gallery.camera.CameraActivity;
import com.android.gallery.fragments.ImagesFragment;
import com.android.gallery.fragments.OneImageFragment;
import com.android.gallery.map.MapsActivity;
import com.android.gallery.utils.Init;

public class ViewImagesActivity extends AppCompatActivity implements ImagesFragment.OnOptionClickListener {

    private FragmentManager fm;
    private FragmentTransaction ft;

    private ImageButton openMap;
    private ImageButton openCam;
    private ImageButton openHome;

    private ImageButton btnShare;
    private ImageButton btnDelete;

    private Toolbar toolbar;
    private Toolbar toolbar3;

    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        isTablet = findViewById(R.id.fragmentTablet) != null;

        Init.getInstance().initComponents(this::initializeComponents);

        openMap.setOnClickListener(this::openMap);
        openCam.setOnClickListener(this::openCam);
        openHome.setOnClickListener(this::showHomePage);
    }

    private void initializeComponents(){
        fm = getSupportFragmentManager();

        openMap = findViewById(R.id.btnMap);
        openCam = findViewById(R.id.btnCam);
        openHome = findViewById(R.id.btnImages);

        btnShare = findViewById(R.id.btnShare);
        btnDelete = findViewById(R.id.btnDelete);

        toolbar = findViewById(R.id.toolbar);
        toolbar3 = findViewById(R.id.toolbar3);

        btnShare.setVisibility(View.INVISIBLE);
        btnDelete.setVisibility(View.INVISIBLE);
        toolbar3.setVisibility(View.INVISIBLE);

        setSupportActionBar(toolbar);

        if(isTablet)
            setTabletHomePage();
         else
            setHomePage();
    }

    private void openMap(View action){
        MapsActivity maps = new MapsActivity();
        if(isTablet){
            addViewToFragment(maps, false);
        } else {
            addViewToFragment(maps, true);
        }

    }

    private void openCam(View action){
        if(Init.getInstance().checkCameraHardware(this)){
            Intent cameraI = new Intent(this, CameraActivity.class);
            cameraI.setAction(Intent.ACTION_GET_CONTENT);
            startActivity(cameraI);
        }
    }

    private void showHomePage(View action){
        if(isTablet){
            setTabletHomePage();
        } else {
            setHomePage();
        }
        btnShare.setVisibility(View.INVISIBLE);
        btnDelete.setVisibility(View.INVISIBLE);
        toolbar3.setVisibility(View.INVISIBLE);
    }

    private void setTabletHomePage(){
        ft = fm.beginTransaction();

        ImagesFragment imagesF = new ImagesFragment();
        ft.add(R.id.frameFragmentLay, imagesF);

        MapsActivity maps = new MapsActivity();
        ft.add(R.id.fragmentTablet, maps).commit();
    }

    private void setHomePage(){
        ImagesFragment fragment = new ImagesFragment();
        addViewToFragment(fragment, false);
    }

    private void addViewToFragment(Fragment fragment, boolean backStack){
        ft = fm.beginTransaction();
        ft.replace(R.id.frameFragmentLay, fragment);

        if(backStack)
            ft.addToBackStack("YES");

        ft.commit();
    }

    private void addImageToFragment(Bitmap bitmap, Integer path){

        toolbar3.setVisibility(View.VISIBLE);
        btnShare.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);

        OneImageFragment oneImg = new OneImageFragment(bitmap, btnShare, btnDelete, toolbar3, path);
        addViewToFragment(oneImg, true);
    }

    @Override
    public void onOptionSelected(View view, Integer path) {
        Bitmap bitmap = getBitmap(view);
        assert bitmap != null;
        if(!isTablet)
            addImageToFragment(bitmap, path);
        else {
            OneImageFragment oneImg = new OneImageFragment(bitmap, btnShare, btnDelete, toolbar3, path);
            ft.replace(R.id.fragmentTablet, oneImg).addToBackStack("YES").commit();
        }
    }

    private Bitmap getBitmap(@NonNull View view){
        ImageView i = view.findViewById(R.id.sqrImg);

        BitmapDrawable bd;

        if(i != null) {
            try {
                bd = (BitmapDrawable) i.getDrawable();
                return bd.getBitmap();
            } catch (ClassCastException e) {
                throw new ClassCastException("Cannot cast Drawable into BitmapDrawable!");
            }
        }

        return null;
    }
}
