package com.android.gallery.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.gallery.database.MyAppDatabase;
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

    private int position;

    private static RecyclerView rc;

    private Init(){
        this.listOfAllImages = new ArrayList<>();
        this.context = null;
        this.cursor = null;
        this.external_uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        create = true;
        init = this;
        perm = null;
    }

    private static boolean isCreated(){
        return create;
    }

    public static Init getInstance(){
        return isCreated() ? init : new Init();
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
        String[] projection;
        Environment.getExternalStorageState();
        projection = new String[]{MediaStore.Images.Media.DATA};

        this.cursor = context.getContentResolver().query(uri, projection, null, null, "date_added DESC");

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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

    public boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public static MyAppDatabase createDatabaseInstance(Context context){
        return Room.databaseBuilder(context, MyAppDatabase.class, "imagedb")
                   .allowMainThreadQueries()
                   .fallbackToDestructiveMigration()
                   .build();
    }

    public static void setRecyclerView(@NonNull RecyclerView rc){
        Init.rc = rc;
    }

    public static RecyclerView getRecyclerView(){
        return rc;
    }

}
