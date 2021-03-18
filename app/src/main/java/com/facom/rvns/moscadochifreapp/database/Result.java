package com.facom.rvns.moscadochifreapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Result {

    @PrimaryKey (autoGenerate = true)
    public int id;

    @ColumnInfo(name = "description_cow")
    public String descriptionCow;

    @ColumnInfo(name = "photo_path")
    public String photoPath;

    @ColumnInfo(name = "photo_date")
    public Long photoDate;

    @ColumnInfo(name = "count_date")
    public Long countDate;

    @ColumnInfo(name = "flies_count")
    public int fliesCount;
}
