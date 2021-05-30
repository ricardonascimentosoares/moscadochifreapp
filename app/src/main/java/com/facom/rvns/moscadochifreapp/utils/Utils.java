package com.facom.rvns.moscadochifreapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.room.TypeConverter;

import com.facom.rvns.moscadochifreapp.activity.FullScreenImage;
import com.facom.rvns.moscadochifreapp.activity.FullScreenImageProcessed;
import com.facom.rvns.moscadochifreapp.activity.CountActivity;
import com.facom.rvns.moscadochifreapp.database.model.Result;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class Utils {
    public static String TAG = "Utils";



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
        if (context instanceof CountActivity) {
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


    @TypeConverter
    public static Date toDate(Long dateLong){
        return dateLong == null ? null: new Date(dateLong);
    }

    @TypeConverter
    public static Long fromDate(Date date){
        return date == null ? null : date.getTime();
    }

    public static String toDateFormat(Date date){
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
    }

    public static String toDateFormat(long millis){
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(millis));
    }



    public static ByteArrayOutputStream convertJPEGToPNGByteArray(String sourceFilePath, String targetFilePath){
        try {
            FileOutputStream out = new FileOutputStream(targetFilePath);
            Bitmap bmp = BitmapFactory.decodeFile(sourceFilePath);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); //100-best quality
            out.close();


            File file = new File(targetFilePath);

            FileInputStream fis = new FileInputStream(file);
            //System.out.println(file.exists() + "!!");
            //InputStream in = resource.openStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            try {
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    bos.write(buf, 0, readNum); //no doubt here is 0
                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
//                    System.out.println("read " + readNum + " bytes,");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return bos;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
