package com.facom.rvns.moscadochifreapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.facom.rvns.moscadochifreapp.database.dao.ConfigurationDao;
import com.facom.rvns.moscadochifreapp.database.dao.CountDao;
import com.facom.rvns.moscadochifreapp.database.dao.ResultDao;
import com.facom.rvns.moscadochifreapp.database.model.Configuration;
import com.facom.rvns.moscadochifreapp.database.model.Count;
import com.facom.rvns.moscadochifreapp.database.model.Result;

@Database(entities = {Result.class, Count.class, Configuration.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ResultDao resultDao();
    public abstract CountDao countDao();
    public abstract ConfigurationDao configurationDao();
}