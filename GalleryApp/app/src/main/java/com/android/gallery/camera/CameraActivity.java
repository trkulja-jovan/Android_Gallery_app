package com.android.gallery.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.gallery.R;
import com.android.gallery.interfaces.Permissible;
import com.android.gallery.utils.Init;

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

public class CameraActivity extends AppCompatActivity implements Permissible {

    private Camera mCamera;
    private CameraPreview mPreview;

    private FrameLayout preview;

    private Button captureImg;
    private Button btnDissmis;

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

        Init.getInstance().initComponents(() -> {
            mCamera = getCameraInstance();

            captureImg = findViewById(R.id.button6);
            btnDissmis = findViewById(R.id.btnDissmis);

            mPreview = new CameraPreview(this, mCamera);
            preview = findViewById(R.id.camera_frame);

            assert preview != null;
            preview.addView(mPreview);
        });

        captureImg.setOnClickListener(action -> {
            if (mCamera != null) {
                mCamera.takePicture(null, null, (data, camera) -> {
                    File picture_file = getOutputMediaFile();
                    if (picture_file == null)
                        return;
                    else {
                        try (FileOutputStream fos = new FileOutputStream(picture_file);) {
                            fos.write(data);
                            mCamera.startPreview();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        //ONLY FOR TEST
        preview.setOnClickListener(action -> {
            if (mCamera != null) {
                mCamera.takePicture(null, null, (data, camera) -> {
                    File picture_file = getOutputMediaFile();
                    if (picture_file == null)
                        return;
                    else {
                        try (FileOutputStream fos = new FileOutputStream(picture_file);) {
                            fos.write(data);
                            mCamera.startPreview();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        btnDissmis.setOnClickListener(action -> {
            this.finish();
        });
    }

    private File getOutputMediaFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        } else {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "GUI");
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdir();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File media;

            double[] geoInfo = getLastKnownLongitudeLatitude();

            media = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            //geoTag(media.getAbsolutePath(),geoInfo[0],geoInfo[1]);

            return media;
        }
    }

    @SuppressLint("MissingPermission")
    private double[] getLastKnownLongitudeLatitude() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert lm != null;
        Location location = getFreeLocationService(lm);
        if(location == null){
            return null;
        }
        return fillArray(location);
    }

    @SuppressLint("MissingPermission")
    private Location getFreeLocationService(@NonNull LocationManager lm){
        boolean gps_enabled = false;
        boolean net_enabled = false;
        boolean coa_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(gps_enabled)
                return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        try {
            net_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(net_enabled)
                return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        try {
            coa_enabled = lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
            if(coa_enabled)
                return lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private double[] fillArray(@NonNull  Location location){
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        double[] arr = new double[2];
        arr[0] = longitude;
        arr[1] = latitude;

        System.out.println("Longitude " + arr[0] + " | Latitude " + arr[1]);

        return arr;
    }

    private void geoTag(String filename, double latitude, double longitude){
        ExifInterface exif;

        try {
            exif = new ExifInterface(filename);
            int num1Lat = (int)Math.floor(latitude);
            int num2Lat = (int)Math.floor((latitude - num1Lat) * 60);
            double num3Lat = (latitude - ((double)num1Lat+((double)num2Lat/60))) * 3600000;

            int num1Lon = (int)Math.floor(longitude);
            int num2Lon = (int)Math.floor((longitude - num1Lon) * 60);
            double num3Lon = (longitude - ((double)num1Lon+((double)num2Lon/60))) * 3600000;

            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat+"/1,"+num2Lat+"/1,"+num3Lat+"/1000");
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon+"/1,"+num2Lon+"/1,"+num3Lon+"/1000");

            if (latitude > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
            }

            if (longitude > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
            }

            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }
}
