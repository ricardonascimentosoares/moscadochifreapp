package com.facom.rvns.moscadochifreapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.facom.rvns.moscadochifreapp.database.model.Count;
import com.facom.rvns.moscadochifreapp.database.model.Result;

import java.util.List;

@Dao
public interface CountDao {

    @Query("SELECT * FROM count")
    List<Count> getAll();

    @Insert
    long insert(Count count);

    @Insert
    void insertAll(Count... counts);

    @Delete
    void delete(Count count);

    @Update
    int update(Count count);

    @Query("SELECT * FROM count WHERE id IN (:countId)")
    Count getCountById(int countId);

}
