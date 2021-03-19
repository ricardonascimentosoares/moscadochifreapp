package com.facom.rvns.moscadochifreapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ResultDao {

    @Query("SELECT * FROM result")
    List<Result> getAll();

    @Query("SELECT * FROM result WHERE id IN (:resultsId)")
    List<Result> loadAllByIds(int[] resultsId);

    @Query("SELECT * FROM result WHERE description_cow like :descriptionCow")
    Result findByDescription(String descriptionCow);

    @Insert
    void insertAll(Result... results);

    @Delete
    void delete(Result result);

    @Query("DELETE FROM result WHERE photo_path = :photoPath")
    void deleteByPath(String photoPath);
}
