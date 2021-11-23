package com.facom.rvns.moscadochifreapp.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.facom.rvns.moscadochifreapp.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;

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

    @ColumnInfo(name = "count_path")
    public String countPath;


    @Override
    public String toString() {
        return "\n Coleta: " + (name.equals("") ? "Sem identificação" : name)  + '\n'+
                " Data de Início: " + Utils.toDateFormat(countDate) + '\n';
    }


    public void deleteCountFiles() {
        deleteDir(new File(countPath));
    }

    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}
