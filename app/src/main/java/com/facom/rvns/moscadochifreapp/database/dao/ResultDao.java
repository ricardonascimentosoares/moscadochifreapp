package com.facom.rvns.moscadochifreapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.facom.rvns.moscadochifreapp.database.model.Result;

import java.util.List;

@Dao
public interface ResultDao {

    @Query("SELECT * FROM result")
    List<Result> getAll();

    @Query("SELECT * FROM result WHERE id IN (:resultsId)")
    List<Result> getAllByIds(int[] resultsId);

    @Query("SELECT * FROM result WHERE ind_processado = 0")
    List<Result> getAllResultsNotProcessed();

    @Query("SELECT * FROM result WHERE ind_processado = 1")
    List<Result> getAllResultsProcessed();

    @Query("SELECT * FROM result WHERE photo_path = :photoPath")
    Result getByPath(String photoPath);

    @Query("SELECT * FROM result WHERE description_cow like :descriptionCow")
    Result getByDescription(String descriptionCow);

    @Insert
    void insertAll(Result... results);

    @Insert
    long insert(Result result);

    @Delete
    void delete(Result result);

    @Query("DELETE FROM result WHERE photo_path = :photoPath")
    void deleteByPath(String photoPath);

    @Update
    int update(Result result);

    @Query("SELECT * FROM result WHERE count_id IN (:countId)")
    List<Result> getAllResultsByCountId(int countId);

    @Query("SELECT * FROM result WHERE ind_processado = 0 AND count_id = :countId")
    List<Result> getAllResultsNotProcessedByCountId(int countId);

    @Query("SELECT * FROM result WHERE ind_processado = 1 AND count_id = :countId")
    List<Result> getAllResultsProcessedByCountId(int countId);


    @Query("DELETE FROM result WHERE count_id = :countId")
    void deleteByCountId(int countId);
}
