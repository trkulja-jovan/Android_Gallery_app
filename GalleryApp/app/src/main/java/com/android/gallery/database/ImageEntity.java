package com.android.gallery.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "imagedb")
public class ImageEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "latitude")
    private float latitude;//double

    @ColumnInfo(name = "longitude")
    private float longitude;

    @ColumnInfo(name = "path")
    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Integer getId() {
        return id;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLatitude(float latitude) { this.latitude = latitude; }

    public void setLongitude(float longitude) {  this.longitude = longitude; }
}
