package com.facom.rvns.moscadochifreapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Result.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ResultDao resultDao();
}