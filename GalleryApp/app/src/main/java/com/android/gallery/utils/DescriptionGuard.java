package com.android.gallery.utils;

public class DescriptionGuard {

    private static String DESCRIPTION;

    static {
        DESCRIPTION = "";
    }

    public static String getDescription(){
        return DESCRIPTION;
    }

    public static void setDescription(String txt){
        DESCRIPTION = txt;
    }

}
