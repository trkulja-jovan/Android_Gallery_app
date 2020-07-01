package com.android.gallery.utils;

import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

import com.android.gallery.exceptions.InitializeException;
import com.android.gallery.exceptions.PermissibleException;
import com.android.gallery.interfaces.Permissible;

public final class Permissions {

    private static int STORAGE_ALLOWED = 0;
    private static int WRITE_STORAGE = 0;
    private static int CAMERA_ALLOWED = 0;
    private static int LOCATION_ALLOWED = 0;
    private static int LOCATION_COARSE = 0;

    public static final int STORAGE_PERMISSION_CODE = 100;
    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int LOCATION_PERMISSION_CODE = 102;
    public static final int LOCATION_COARSE_PERMISSION_CODE = 103;
    public static final int WRITE_STORAGE_PERMISSION_CODE = 104;

    Permissions() {}

    public void checkPermission(Context context, Activity activity, String permission, int reqCode) {

        if(!(activity instanceof Permissible)){
            throw new PermissibleException("Class " + activity.toString() + " cannot request Permissions!");
        }

        if (ContextCompat.checkSelfPermission(context, permission) == PERMISSION_DENIED)
            ActivityCompat.requestPermissions(activity, new String[]{permission}, reqCode);
        else {
            switch (reqCode) {
                case STORAGE_PERMISSION_CODE  : STORAGE_ALLOWED  = 1;  break;
                case CAMERA_PERMISSION_CODE   : CAMERA_ALLOWED   = 1;  break;
                case LOCATION_PERMISSION_CODE : LOCATION_ALLOWED = 1;  break;
                case LOCATION_COARSE_PERMISSION_CODE : LOCATION_COARSE = 1;  break;
                case WRITE_STORAGE_PERMISSION_CODE : WRITE_STORAGE = 1; break;
                default: throw new PermissibleException("Permission request code " + reqCode + " is wrong!");
            }
        }
    }

    public static int getStorageAllowedStatus(){
        return STORAGE_ALLOWED;
    }

    public void setStorageAllowedStatus(int status){
        if(status < 0 || status > 1)
            throw new InitializeException("Code status " + status + " is wrong!");

        Permissions.STORAGE_ALLOWED = status;
    }

    public void setLocationCoarseAllowedStatus(int status){
        if(status < 0 || status > 1)
            throw new InitializeException("Code status " + status + " is wrong!");

        Permissions.LOCATION_COARSE = status;
    }
    
    public void setCameraAllowedStatus(int status){
        if(status < 0 || status > 1)
            throw new InitializeException("Code status " + status + " is wrong!");

        Permissions.CAMERA_ALLOWED = status;
    }

    public void setLocationAllowedStatus(int status){
        if(status < 0 || status > 1)
            throw new InitializeException("Code status " + status + " is wrong!");

        Permissions.LOCATION_ALLOWED = status;
    }

    public void setWriteStorageAllowedStatus(int status){
        if(status < 0 || status > 1)
            throw new InitializeException("Code status " + status + " is wrong!");

        Permissions.WRITE_STORAGE = status;
    }
}
