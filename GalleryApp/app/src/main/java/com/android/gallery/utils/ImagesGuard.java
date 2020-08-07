package com.android.gallery.utils;

import com.android.gallery.exceptions.InitializeException;

import java.util.List;

public class ImagesGuard {

    private static List<String> bitmaps;

    private ImagesGuard() {}

    public static void setBitmapsPath(List<String> bitmaps){
        if(bitmaps == null)
            throw new InitializeException("List cannot be null!");

        ImagesGuard.bitmaps = bitmaps;
    }

    public static List<String> getBitmapsPath(){
        return bitmaps;
    }
}
