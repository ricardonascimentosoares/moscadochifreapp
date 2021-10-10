package com.facom.rvns.moscadochifreapp.database;

import android.content.Context;

import androidx.room.Room;

public class AppDatabaseSingleton {

    private static AppDatabase db;

    public static AppDatabase getInstance(Context c){

        if (db == null)
            db = Room.databaseBuilder(c, AppDatabase.class, "database-name").allowMainThreadQueries().build();
        return db;
    }
}
