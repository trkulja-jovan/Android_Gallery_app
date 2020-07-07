package com.android.gallery.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.gallery.R;
import com.android.gallery.camera.CameraActivity;
import com.android.gallery.fragments.ImagesFragment;
import com.android.gallery.fragments.OneImageFragment;
import com.android.gallery.map.MapsActivity;
import com.android.gallery.utils.Init;

public class ViewImagesActivity extends AppCompatActivity{

    private FragmentManager fm;
    private FragmentTransaction ft;

    private ImageButton openMap;
    private ImageButton openCam;
    private ImageButton openHome;

    private ImageButton btnShare;
    private ImageButton btnDelete;

    private Toolbar toolbar;
    private Toolbar toolbar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

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
        setHomePage();
    }

    private void openMap(View action){
        Intent mapI = new Intent(this, MapsActivity.class);
        startActivity(mapI);
    }

    private void openCam(View action){
        if(Init.getInstance().checkCameraHardware(this)){
            Intent cameraI = new Intent(this, CameraActivity.class);
            cameraI.setAction(Intent.ACTION_GET_CONTENT);
            startActivity(cameraI);
        }
    }

    private void showHomePage(View action){
        setHomePage();
        btnShare.setVisibility(View.INVISIBLE);
        btnDelete.setVisibility(View.INVISIBLE);
        toolbar3.setVisibility(View.INVISIBLE);
    }

    private void setHomePage(){
        ImagesFragment fragment = new ImagesFragment();
        addViewToFragment(fragment, false);
    }

    public void showFullImage(View v){
        ImageView i = v.findViewById(R.id.sqrImg);
        BitmapDrawable bd;
        if(i != null){
            try {

                bd = (BitmapDrawable) i.getDrawable();
            } catch(ClassCastException e){
                throw new ClassCastException("Cannot cast Drawable into BitmapDrawable!");
            }
            if(bd != null){
                Bitmap bitmap = bd.getBitmap();
                addImageToFragment(bitmap);
            }
        }
    }

    private void addViewToFragment(Fragment fragment, boolean backStack){
        ft = fm.beginTransaction();
        ft.replace(R.id.frameFragmentLay, fragment);

        if(backStack)
            ft.addToBackStack("YES");

        ft.commit();
    }

    private void addImageToFragment(Bitmap bitmap){

        toolbar3.setVisibility(View.VISIBLE);
        btnShare.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);

        OneImageFragment oneImg = new OneImageFragment(bitmap, btnShare, btnDelete, toolbar3);
        addViewToFragment(oneImg, true);
    }
}
