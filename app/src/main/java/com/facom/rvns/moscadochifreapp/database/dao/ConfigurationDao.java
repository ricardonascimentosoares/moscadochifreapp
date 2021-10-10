package com.facom.rvns.moscadochifreapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.facom.rvns.moscadochifreapp.database.model.Configuration;
import com.facom.rvns.moscadochifreapp.database.model.Count;

import java.util.List;

@Dao
public interface ConfigurationDao {

    @Query("SELECT * FROM configuration")
    List<Configuration> getAll();

    @Insert
    long insert(Configuration configuration);

    @Insert
    void insertAll(Configuration... configurations);

    @Delete
    void delete(Configuration configuration);

    @Update
    int update(Configuration configuration);

    @Query("SELECT * FROM configuration WHERE id IN (:configuration)")
    Configuration getConfigurationById(int configuration);

}
