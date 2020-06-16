package com.android.gallery.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.android.gallery.description.DescriptionAlert;
import com.android.gallery.exceptions.InitializeException;
import com.android.gallery.interfaces.Initializable;

import java.util.ArrayList;
import java.util.List;

public final class Init {

    private Context context;
    private Cursor cursor;
    private Uri external_uri;

    private static Permissions perm;

    private static boolean create = false;

    @SuppressLint("StaticFieldLeak")
    private static Init init;

    private List<String> listOfAllImages;

    private Init(){
        this.listOfAllImages = new ArrayList<>();
        this.context = null;
        this.cursor = null;
        this.external_uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        create = true;
        init = this;
        perm = null;
    }

    private static boolean isCreate(){
        return create;
    }

    public static Init getInstance(){
        return isCreate() ? init : new Init();
    }

    public void initComponents(@NonNull Initializable i){
        i.initialize();
    }

    public static String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    public List<String> getImagesFromUri(Context context, boolean var){

        if(context == null)
            throw new InitializeException("Context cannot be null!");

        this.context = context;

        if(var)
            listOfAllImages.addAll(getExternalImagesPath());

        return var ? listOfAllImages : null;
    }

    private List<String> getImagesPathFromUri(Uri uri) {

        int column_index_data;

        String absolutePathOfImage;
        String[] projection = new String[0];

        //projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Images.Media.TITLE};

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        }
        this.cursor = context.getContentResolver().query(uri, projection, null, null, "date_added DESC");

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                if(listOfAllImages.contains(absolutePathOfImage))
                    continue;
                listOfAllImages.add(absolutePathOfImage);
            }
            cursor.close();
        }
        return listOfAllImages;
    }

    private List<String> getExternalImagesPath() {

        return getImagesPathFromUri(external_uri);
    }

    public static Permissions getPermissionInstance(){
        return perm == null ? perm = new Permissions() : perm;
    }

    public DescriptionAlert getAlertDescriptionInstance(Activity activity){
        if(activity == null)
            throw new ActivityNotFoundException("Activity not found");

        return new DescriptionAlert(activity);
    }

    public boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

}
