package com.android.gallery.database;

import android.location.Location;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ImageEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "opis_slike")
    private String description;

    @ColumnInfo(name = "latitude")
    private Integer latitude;//double

    @ColumnInfo(name = "longitude")
    private Integer longitude;

    @ColumnInfo(name = "slika")
    private String nazSlike;

    public void setNazSlike(String nazSlike) {
        this.nazSlike = nazSlike;
    }

    public String getNazSlike() {
        return nazSlike;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Integer getLatitude() {
        return latitude;
    }

    public Integer getLongitude() {
        return longitude;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(Integer latitude) { this.latitude = latitude; }

    public void setLongitude(Integer longitude) {  this.longitude = longitude; }
}
