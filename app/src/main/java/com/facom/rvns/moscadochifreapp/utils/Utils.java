package com.facom.rvns.moscadochifreapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.room.TypeConverter;

import com.facom.rvns.moscadochifreapp.activity.FullScreenImage;
import com.facom.rvns.moscadochifreapp.activity.FullScreenImageProcessed;
import com.facom.rvns.moscadochifreapp.activity.MainActivity;
import com.facom.rvns.moscadochifreapp.database.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String TAG = "Utils";

    private static File storageDir;
    private static File storageDirSource;
    private static File storageDirTarget;

    public static void init(Context context){
        storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storageDirSource = new File(storageDir, "Imagens_Capturadas");
        storageDirTarget = new File(storageDir, "Imagens_Processadas");

        //cria os diretorios onde as imagens ficarao salvas
        if (storageDirSource.mkdirs())
            Log.d(TAG, "Diretorio 'Imagens_Capturadas' criado");

        if (storageDirTarget.mkdirs())
            Log.d(TAG, "Diretorio 'Imagens_Processadas' criado");
    }

    public static void copyStream(Context context, Uri uri, File file){
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            // Copying
            copyStream(inputStream, fileOutputStream);
            fileOutputStream.close();
            inputStream.close();
        }
        catch (Exception e){

        }
    }

    private static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    /**
     * Cria o arquivo de imagem dentro do diretório onde o applicativo é instalado
     * @return
     * @throws IOException
     */
    public static File createImageFile(File dir)  {
        // Create an image file name
        File image = null;
        try {
            image = File.createTempFile(
                    "image",  /* prefix */
                    ".jpg",         /* suffix */
                    dir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static File[] loadFiles(String path){
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        return files;

    }

    /**
     *
     * @param context
     * @param result
     */
    public static void startFullscreen(Activity context, Result result){
        Intent intent;
        if (context instanceof MainActivity) {
            intent = new Intent(context, FullScreenImage.class);
        }
        else{
            intent = new Intent(context, FullScreenImageProcessed.class);
        }

        Bundle extras = new Bundle();
        extras.putSerializable("result", result);
        intent.putExtras(extras);
        context.startActivityForResult(intent, FullScreenImage.DELETE);
    }

    public static File getStorageDirSource(){
        return storageDirSource;
    }

    public static File getStorageDirTarget(){
        return storageDirTarget;
    }

    @TypeConverter
    public static Date toDate(Long dateLong){
        return dateLong == null ? null: new Date(dateLong);
    }

    @TypeConverter
    public static Long fromDate(Date date){
        return date == null ? null : date.getTime();
    }
}
