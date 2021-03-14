package com.facom.rvns.moscadochifreapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.facom.rvns.moscadochifreapp.OutputWritable;
import com.facom.rvns.moscadochifreapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements OutputWritable {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int RESULT_LOAD_IMG = 2;

    private ImageView imageView1;
    private ImageView imageView2;
    private String currentPhotoPath;
    private String outputFilename;
    private TextView txtStatusProcessamento;
    private TextView txtTotalMoscas;
    private AlertDialog dialog;
    private File storageDir;
    private File storageDirSource;
    private File storageDirTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storageDirSource = new File(storageDir, "Imagens_Capturadas");
        storageDirTarget = new File(storageDir, "Imagens_Processadas");

        //cria os diretorios onde as imagens ficarao salvas
        storageDirSource.mkdirs();
        storageDirTarget.mkdirs();


        Button btnCapturar = findViewById(R.id.btnCapturar);
        btnCapturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        Button btnCarregar = findViewById(R.id.btnCarregar);
        btnCarregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });

        txtTotalMoscas = findViewById(R.id.txtTotalMoscas);

        loadFiles(storageDirSource.getAbsolutePath());

    }

    /**
     * Metodo que inicia a activity responsável pela captura da imagem utilizando a camera do dispositivo
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(storageDirSource);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.facom.rvns.moscadochifreapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    //startPython();
                    break;
                case RESULT_LOAD_IMG:
                    try {
                        // Creating file
                        File photoFile = null;
                        try {
                            photoFile = createImageFile(storageDirSource);
                            setImageViewBitmap(photoFile);
                        } catch (IOException ex) {
                            Log.d(TAG, "Error occurred while creating the file");
                        }

                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                        // Copying
                        copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        inputStream.close();

                    } catch (Exception e) {
                        Log.d(TAG, "onActivityResult: " + e.toString());
                    }

                    //startPython();
                    break;
            }
        }
    }

    /**
     * Cria o arquivo de imagem dentro do diretório onde o applicativo é instalado
     * @return
     * @throws IOException
     */
    private File createImageFile(File dir) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File image = null;
        try {
            image = File.createTempFile(
                    timeStamp,  /* prefix */
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


    /**
     * Metodo principal que inicia a integracao do Java com Python e faz a chamada dos métodos
     */
    private void startPython(){

        // "context" must be an Activity, Service or Application object from your app.
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(getApplicationContext()));
        }

        createProgressDialog();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {

                //inicia o single do Python
                Python py = Python.getInstance();
                PyObject pyobj = py.getModule("main");
                OutputWritable activityRef = MainActivity.this;
                PyObject pyResponse = pyobj.callAttr("realiza_contagem", currentPhotoPath, activityRef);

                outputFilename = pyResponse.toString();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null)
                            dialog.dismiss();
                        //setImageViewBitmap(outputFilename);
                        Toast.makeText(MainActivity.this, "Contagem Realizada!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Cria o dialog que indica para o usuário que a contagem está sendo realizada
     */
    private void createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_loading_dialog, null);
        txtStatusProcessamento = dialogView.findViewById(R.id.txtStatusProcessamento);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
    }

    /**
     * Carrega o arquivo de imagem situado no path e exibe na miniatura da tela (ImageView)
     * @param imageView
     * @param path
     */
    private  void setImageViewBitmap(final File file){

        LinearLayout item = (LinearLayout)findViewById(R.id.linearImagemCarregada);
        View child = getLayoutInflater().inflate(R.layout.image_thumbnail, null);

        if(file.exists()){

            ImageView imageThumbnail = child.findViewById(R.id.imageBoi);
            imageThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startFullscreen(file.getAbsolutePath());
                }
            });

            Picasso.with(this).load(file).fit().centerCrop().into(imageThumbnail);
        }
        item.addView(child);
    }

    /**
     * Inicia a activity para carregar um arquivo de imagem salvo no dispositivo
     */
    private void loadImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }


    @Override
    public void writeOutput(final String output) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatusProcessamento.setText(output);
            }
        });
    }

    @Override
    public void writeResult(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtTotalMoscas.setText(result);
            }
        });
    }

    /**
     * Inicia a activity que amplia o arquivo de imagem selecionado
     * @param path caminho do arquivo de imagem
     */
    private void startFullscreen(String path){
        Intent intent = new Intent(MainActivity.this, FullScreenImage.class);

        Bundle extras = new Bundle();
        extras.putString("imagebitmap", path);
        intent.putExtras(extras);
        startActivity(intent);
    }


    private void loadFiles(String path){
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            if (path == storageDirSource.getAbsolutePath()){
                Log.d("Files", "FileName:" + files[i].getName());
                setImageViewBitmap(files[i]);
            }

        }
    }
}