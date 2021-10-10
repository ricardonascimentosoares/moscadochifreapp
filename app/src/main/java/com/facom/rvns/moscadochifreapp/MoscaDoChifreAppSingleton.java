package com.facom.rvns.moscadochifreapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.facom.rvns.moscadochifreapp.database.AppDatabaseSingleton;
import com.facom.rvns.moscadochifreapp.database.model.Configuration;
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
    private Configuration configurationSaved;
    private File storageCountDirSource;
    private File storageCountDirTarget;
    private File storageCountDir;
    private static Context c;


    public MoscaDoChifreAppSingleton(){
        Configuration configuration = AppDatabaseSingleton.getInstance(c).configurationDao().getConfigurationById(1);
        if (configuration == null) {
            configuration = new Configuration();
            configuration.setDefaultValues();
            long id = AppDatabaseSingleton.getInstance(c).configurationDao().insert(configuration);
            if (id == 1) {
                configuration.id = (int) id;
            }
        }
        configurationSaved = configuration;
    }

    public static void init(Context context){
        c = context;
        if (instance == null)
            instance = new MoscaDoChifreAppSingleton();
    }


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

        long id = AppDatabaseSingleton.getInstance(c).countDao().insert(countSelected);
        countSelected.id = (int) id;

        return isCreated;
    }
    
    public Configuration getConfiguration(){
        return configurationSaved;
    }

    public void loadCount(int id){

        countSelected = AppDatabaseSingleton.getInstance(c).countDao().getCountById(id);

        storageCountDir = new File(countSelected.countPath);
        storageCountDirSource = new File(storageCountDir, "Imagens_Capturadas");
        storageCountDirTarget = new File(storageCountDir, "Imagens_Processadas");

    }

    public List<Result>  getCountResultsNotProcessed(){
        List<Result> results = AppDatabaseSingleton.getInstance(c).resultDao().getAllResultsNotProcessedByCountId(countSelected.id);
        return results;
    }

    public List<Result>  getCountResultsProcessed(){
        List<Result> results = AppDatabaseSingleton.getInstance(c).resultDao().getAllResultsProcessedByCountId(countSelected.id);
        return results;
    }


    public List<Result>  getCountResults(){
        List<Result> results = AppDatabaseSingleton.getInstance(c).resultDao().getAllResultsByCountId(countSelected.id);
        return results;
    }

    public List<Count>  getAllCounts(){
        List<Count> results = AppDatabaseSingleton.getInstance(c).countDao().getAll();
        return results;
    }

    public void insertResult(Result result){
        result.countId = countSelected.id;
        result.id = (int) AppDatabaseSingleton.getInstance(c).resultDao().insert(result);
    }

    public Result processResult(Result result) {
        result.countDate = Utils.fromDate(new Date());
        result.indProcessado = 1;
        AppDatabaseSingleton.getInstance(c).resultDao().update(result);
        return result;

    }

    public int updateResult(Result result) {
        return AppDatabaseSingleton.getInstance(c).resultDao().update(result);
    }

    public void updateConfiguration(Configuration configuration) {
        int i =  AppDatabaseSingleton.getInstance(c).configurationDao().update(configuration);
        if (i > 0)
            configurationSaved = configuration;
    }

    public void setConfigurationDefaultValues(){
        configurationSaved.setDefaultValues();
        AppDatabaseSingleton.getInstance(c).configurationDao().update(configurationSaved);
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
        List<Result> results = AppDatabaseSingleton.getInstance(c).resultDao().getAllResultsProcessedByCountId(countSelected.id);
        int numberOfResults = results.size();

        if (numberOfResults == 0){
            countSelected.averageFliesCount = 0;
            AppDatabaseSingleton.getInstance(c).countDao().update(countSelected);
            return countSelected.averageFliesCount;
        }

        int sumFliesCount = 0;
        int averageFliesCount = 0;
        for (Result result : results){
            sumFliesCount =  sumFliesCount + result.fliesCount;
        }

        averageFliesCount = sumFliesCount/numberOfResults;
        countSelected.averageFliesCount = averageFliesCount;
        AppDatabaseSingleton.getInstance(c).countDao().update(countSelected);
        return countSelected.averageFliesCount;
    }


    public void deleteCount(Count count){
        count.deleteCountFiles();

        AppDatabaseSingleton.getInstance(c).resultDao().deleteByCountId(count.id);
        AppDatabaseSingleton.getInstance(c).countDao().delete(count);

    }


    public void deleteResult(Result result){
        result.deleteImages();
        AppDatabaseSingleton.getInstance(c).resultDao().delete(result);

        //se a foto nao tiver sido processada
        if (result.photoProcessedPath != null)
            calculateAVG();
    }

    public void deleteResultProcessed(Result result){
        result.deleteImageProcessed();
        AppDatabaseSingleton.getInstance(c).resultDao().update(result);

        calculateAVG();
    }
}
