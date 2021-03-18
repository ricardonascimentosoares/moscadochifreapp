package com.facom.rvns.moscadochifreapp.database;

import android.content.Context;

import androidx.room.Room;

public class AppDatabaseSingleton {

    private static AppDatabase db;
    private static Context c;

    public static void init(Context context){
        c = context;
    }

    public static AppDatabase getInstance(){

        if (c == null)
            return null;

        if (db == null)
            db = Room.databaseBuilder(c, AppDatabase.class, "database-name").allowMainThreadQueries().build();
        return db;
    }
}
