package com.facom.rvns.moscadochifreapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.facom.rvns.moscadochifreapp.database.AppDatabaseSingleton;
import com.facom.rvns.moscadochifreapp.database.model.Count;
import com.facom.rvns.moscadochifreapp.database.model.Result;
import com.facom.rvns.moscadochifreapp.utils.Utils;

import java.io.File;
import java.util.Date;
import java.util.List;

public class MoscaDoChifreAppSingleton {

    public static String TAG = "MoscaDoChifreAppSingleton";

    private static MoscaDoChifreAppSingleton instance;
    private Count countSelected;
    private File storageCountDirSource;
    private File storageCountDirTarget;
    private File storageCountDir;


    public static MoscaDoChifreAppSingleton getInstance(){
        if (instance == null)
            instance = new MoscaDoChifreAppSingleton();
        return instance;
    }


    public boolean createNewDir(Context context, String name){
        boolean isCreated = false;

        storageCountDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), String.valueOf(new Date().getTime()));

        if (storageCountDir.mkdirs())
            Log.d(TAG, "Diretorio de contagem criado");

        storageCountDirSource = new File(storageCountDir, "Imagens_Capturadas");
        storageCountDirTarget = new File(storageCountDir, "Imagens_Processadas");

        //cria os diretorios onde as imagens ficarao salvas
        if (storageCountDirSource.mkdirs() && storageCountDirTarget.mkdirs()) {
            Log.d(TAG, "Diretorios de imagens criados");
            isCreated = true;
        }

        countSelected = new Count();
        countSelected.countPath = storageCountDir.getAbsolutePath();
        countSelected.countDate = Utils.fromDate(new Date());
        countSelected.name = name;

        long id = AppDatabaseSingleton.getInstance().countDao().insert(countSelected);
        countSelected.id = (int) id;

        return isCreated;
    }

    public void loadCount(int id){

        countSelected = AppDatabaseSingleton.getInstance().countDao().getCountById(id);

        storageCountDir = new File(countSelected.countPath);
        storageCountDirSource = new File(storageCountDir, "Imagens_Capturadas");
        storageCountDirTarget = new File(storageCountDir, "Imagens_Processadas");

    }

    public List<Result>  getCountResultsNotProcessed(){
        List<Result> results = AppDatabaseSingleton.getInstance().resultDao().getAllResultsNotProcessedByCountId(countSelected.id);
        return results;
    }

    public List<Result>  getCountResultsProcessed(){
        List<Result> results = AppDatabaseSingleton.getInstance().resultDao().getAllResultsProcessedByCountId(countSelected.id);
        return results;
    }


    public List<Result>  getCountResults(){
        List<Result> results = AppDatabaseSingleton.getInstance().resultDao().getAllResultsByCountId(countSelected.id);
        return results;
    }

    public List<Count>  getAllCounts(){
        List<Count> results = AppDatabaseSingleton.getInstance().countDao().getAll();
        return results;
    }

    public void insertResult(Result result){
        result.countId = countSelected.id;
        AppDatabaseSingleton.getInstance().resultDao().insertAll(result);
    }

    public Result processResult(Result result) {
        result.countDate = Utils.fromDate(new Date());
        result.indProcessado = 1;
        AppDatabaseSingleton.getInstance().resultDao().update(result);
        return result;

    }


    public Count getCountSelected() {
        return countSelected;
    }

    public void setCountSelected(Count countSelected) {
        this.countSelected = countSelected;
    }


    public File getStorageDirSource(){
        return storageCountDirSource;
    }

    public File getStorageDirTarget(){
        return storageCountDirTarget;
    }


    public int calculateAVG(){
        List<Result> results = AppDatabaseSingleton.getInstance().resultDao().getAllResultsProcessedByCountId(countSelected.id);
        int numberOfResults = results.size();
        int sumFliesCount = 0;
        int averageFliesCount = 0;
        for (Result result : results){
            sumFliesCount =  sumFliesCount + result.fliesCount;
        }
        averageFliesCount = sumFliesCount/numberOfResults;
        countSelected.averageFliesCount = averageFliesCount;
        AppDatabaseSingleton.getInstance().countDao().update(countSelected);
        return countSelected.averageFliesCount;
    }


    public void deleteCount(Count count){
        count.deleteCountFiles();

        AppDatabaseSingleton.getInstance().resultDao().deleteByCountId(count.id);
        AppDatabaseSingleton.getInstance().countDao().delete(count);

    }


    public void deleteResult(Result result){
        result.deleteImages();
        AppDatabaseSingleton.getInstance().resultDao().delete(result);

        calculateAVG();
    }

    public void deleteResultProcessed(Result result){
        result.deleteImageProcessed();
        AppDatabaseSingleton.getInstance().resultDao().update(result);

        calculateAVG();
    }
}
