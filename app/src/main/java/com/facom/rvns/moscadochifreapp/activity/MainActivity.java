package com.facom.rvns.moscadochifreapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facom.rvns.moscadochifreapp.R;
import com.facom.rvns.moscadochifreapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;

public class MainActivity extends AppCompatActivity  {

    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int RESULT_LOAD_IMG = 2;
    public static final String TAG = "MainActivity";

    private File cameraFile;
    private LinearLayout linearImagemCarregada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.init(this);

        linearImagemCarregada = (LinearLayout)findViewById(R.id.linearImagemCarregada);

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

        Button btnContagensRealizadas = findViewById(R.id.btnContagensRealizadas);
        btnContagensRealizadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ResultsActivity.class);
                i.putExtra("Tipo", ResultsActivity.CONTAGENS_REALIZADAS);
                startActivity(i);
            }
        });

        Button btnIniciarContagem = findViewById(R.id.btnIniciarContagem);
        btnIniciarContagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ResultsActivity.class);
                i.putExtra("Tipo", ResultsActivity.INICIAR_CONTAGEM);
                startActivity(i);
            }
        });


        //carrega as imagens e adiciona a listagem
        addImagesToLinearLayout(Utils.getStorageDirSource().getAbsolutePath());

    }

    /**
     * Metodo que inicia a activity responsável pela captura da imagem utilizando a camera do dispositivo
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            cameraFile = Utils.createImageFile(Utils.getStorageDirSource());

            // Continue only if the File was successfully created
            if (cameraFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.facom.rvns.moscadochifreapp.fileprovider",
                        cameraFile);
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
                    setImageViewBitmap(cameraFile);
                    break;
                case RESULT_LOAD_IMG:

                    File photoFile = Utils.createImageFile(Utils.getStorageDirSource());
                    Utils.copyStream(this, data.getData(), photoFile);

                    setImageViewBitmap(photoFile);

                    break;
            }
        }
    }


    /**
     * Carrega o arquivo de imagem situado no path e exibe na miniatura da tela (ImageView)
     * @param imageView
     * @param path
     */
    private void setImageViewBitmap(final File file){


        View child = getLayoutInflater().inflate(R.layout.image_thumbnail, null);

        ImageView imageThumbnail = child.findViewById(R.id.imageBoi);
        TextView txtIdentificador = child.findViewById(R.id.txtIdentificador);
        txtIdentificador.setText(file.getName());

        imageThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startFullscreen(MainActivity.this, file.getAbsolutePath());
            }
        });

        Picasso.with(this).load(file).fit().centerCrop().into(imageThumbnail);

        linearImagemCarregada.addView(child);
    }

    /**
     * Inicia a activity para carregar um arquivo de imagem salvo no dispositivo
     */
    private void loadImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    private void addImagesToLinearLayout(String path){

        File[] files = Utils.loadFiles(path);

        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
            setImageViewBitmap(files[i]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        linearImagemCarregada.removeAllViews();
        addImagesToLinearLayout(Utils.getStorageDirSource().getAbsolutePath());
    }
}