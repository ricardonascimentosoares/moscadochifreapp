package com.facom.rvns.moscadochifreapp.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Count {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "count_date")
    public Long countDate;

    @ColumnInfo(name = "average_flies_count")
    public int averageFliesCount;
}
