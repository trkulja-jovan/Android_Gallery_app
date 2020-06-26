package com.android.gallery.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyDao {

    @Insert
    void addImage(ImageEntity ie);

    @Query("select * from imagedb")
    List<ImageEntity> getAllImages();
}
