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
import com.android.gallery.utils.Init;

public class ViewImagesActivity extends AppCompatActivity{

    private FragmentManager fm;
    private FragmentTransaction ft;

    private ImageButton openMap;
    private ImageButton openCam;

    private Toolbar toolbar;
    //razlika sa fragmentima za tablet i mobilne telefone
    //u landscape mode sa leve strane drawer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        Init.getInstance().initComponents( () -> {

            fm = getSupportFragmentManager();
            ImagesFragment fragment = new ImagesFragment();

            addViewToFragment(fragment, false);

            openMap = findViewById(R.id.btnMap);
            openCam = findViewById(R.id.btnCam);

            toolbar = findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

        });

        openMap.setOnClickListener(actionMap -> {
            //create map fragment with pinned location of images
        });

        openCam.setOnClickListener(actionCam -> {
            if(Init.getInstance().checkCameraHardware(this)){
                Intent cameraI = new Intent(this, CameraActivity.class);
                cameraI.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(cameraI, 102);
            }
        });
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
        OneImageFragment oneImg = new OneImageFragment(bitmap);
        addViewToFragment(oneImg, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){
            return;
        }
    }

}
