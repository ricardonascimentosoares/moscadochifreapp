package com.facom.rvns.moscadochifreapp.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Result implements Serializable {

    @PrimaryKey (autoGenerate = true)
    public int id;

    @ColumnInfo(name = "identification")
    public String identification;

    @ColumnInfo(name = "description_cow")
    public String descriptionCow;

    @ColumnInfo(name = "photo_path")
    public String photoPath;

    @ColumnInfo(name = "photo_processed_path")
    public String photoProcessedPath;

    @ColumnInfo(name = "photo_date")
    public Long photoDate;

    @ColumnInfo(name = "count_date")
    public Long countDate;

    @ColumnInfo(name = "flies_count")
    public int fliesCount;

    @ColumnInfo(name = "ind_processado")
    public int indProcessado = 0;

    @ColumnInfo(name = "count_id")
    public int countId;


    public void deleteImageProcessed(){
        if (new File(photoProcessedPath).delete()){
            this.fliesCount = 0;
            this.countDate = null;
            this.photoProcessedPath = null;
            this.indProcessado = 0;
        }
    }

    public void deleteImages(){

        if (new File(photoPath).delete()){
             if (photoProcessedPath != null)
                 new File(photoProcessedPath).delete();

            this.photoPath = null;
            this.fliesCount = 0;
            this.countDate = null;
            this.photoProcessedPath = null;
            this.indProcessado = 0;
        }
    }
}
