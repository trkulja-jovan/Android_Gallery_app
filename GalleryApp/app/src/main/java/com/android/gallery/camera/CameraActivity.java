package com.android.gallery.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.gallery.R;
import com.android.gallery.database.MyAppDatabase;
import com.android.gallery.database.ImageEntity;
import com.android.gallery.interfaces.Permissible;
import com.android.gallery.utils.Init;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.CAMERA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.android.gallery.utils.Permissions.CAMERA_PERMISSION_CODE;
import static com.android.gallery.utils.Permissions.LOCATION_PERMISSION_CODE;
import static com.android.gallery.utils.Permissions.LOCATION_COARSE_PERMISSION_CODE;
import static com.android.gallery.utils.Permissions.WRITE_STORAGE_PERMISSION_CODE;

public class CameraActivity extends AppCompatActivity implements Permissible {

    private Camera mCamera;
    private CameraPreview mPreview;

    private FrameLayout preview;

    private Button captureImg;
    private Button btnDissmis;

    private FusedLocationProviderClient mFusedLocationClient;
    private static MyAppDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Init.getPermissionInstance()
             .checkPermission(getApplicationContext(), this, CAMERA, CAMERA_PERMISSION_CODE);

        Init.getPermissionInstance()
                .checkPermission(this.getApplicationContext(),
                        this, Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_CODE);

        Init.getPermissionInstance()
             .checkPermission(this.getApplicationContext(),
                       this, Manifest.permission.ACCESS_COARSE_LOCATION,
                              LOCATION_COARSE_PERMISSION_CODE);

        Init.getPermissionInstance()
            .checkPermission(this.getApplicationContext(),
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            WRITE_STORAGE_PERMISSION_CODE);

        Init.getInstance().initComponents(this::initializeComponents);

        captureImg.setOnClickListener(this::captureImage);
        //ONLY FOR TEST
        preview.setOnClickListener(this::captureImage);

        btnDissmis.setOnClickListener(this::finishActivity);
    }

    private void initializeComponents(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mCamera = getCameraInstance();

        captureImg = findViewById(R.id.button6);
        btnDissmis = findViewById(R.id.btnDissmis);

        mPreview = new CameraPreview(this, mCamera);
        preview = findViewById(R.id.camera_frame);

        assert preview != null;
        preview.addView(mPreview);

        myDatabase = Init.createDatabaseInstance(getApplicationContext());
    }

    private void captureImage(View action){
        if (mCamera != null) {
            mCamera.takePicture(null, null, (data, camera) -> {
                File picture_file = getOutputMediaFile();
                if (picture_file == null)
                    return;
                else {
                    try (FileOutputStream fos = new FileOutputStream(picture_file)) {
                        getTag(picture_file.getAbsolutePath());
                        galleryAddPic(picture_file.getPath());

                        fos.write(data);

                        Toast.makeText(getApplicationContext(), R.string.uspesnoSacuvano, Toast.LENGTH_SHORT)
                                .show();

                        mCamera.startPreview();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void finishActivity(View action){
        this.finish();
    }

    private void galleryAddPic(String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File getOutputMediaFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        } else {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "GUI");
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File media;

            String name = "IMG_" + timeStamp + ".jpg";
            media = new File(mediaStorageDir, name);

            return media;
        }
    }

    private ImageEntity createImageEntity(@NonNull String path, @NonNull double[] geoInfo){

        ImageEntity ie = new ImageEntity();

        ie.setPath(path);
        ie.setLatitude(geoInfo[0]);
        ie.setLongitude(geoInfo[1]);

        return ie;
    }

    private void getTag(String path) {
        double[] arr = new double[2];

        mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, (location) -> {
                                if(location != null){
                                    arr[0] = location.getLatitude();
                                    arr[1] = location.getLongitude();

                                    ImageEntity ie = createImageEntity(path, arr);
                                    CameraActivity.myDatabase.myDao().addImage(ie);
                                }
                            });

    }

    private Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED )
                Init.getPermissionInstance().setCameraAllowedStatus(1);
            else
                Init.getPermissionInstance().setCameraAllowedStatus(0);
        }
        if(requestCode == LOCATION_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED )
                Init.getPermissionInstance().setLocationAllowedStatus(1);
            else
                Init.getPermissionInstance().setLocationAllowedStatus(0);
        }
        if(requestCode == LOCATION_COARSE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED )
                Init.getPermissionInstance().setLocationCoarseAllowedStatus(1);
            else
                Init.getPermissionInstance().setLocationCoarseAllowedStatus(0);
        }

        if(requestCode == WRITE_STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED )
                Init.getPermissionInstance().setWriteStorageAllowedStatus(1);
            else
                Init.getPermissionInstance().setWriteStorageAllowedStatus(0);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (mCamera != null){
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }
}
