package com.facom.rvns.moscadochifreapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
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
import android.widget.Toast;

import com.facom.rvns.moscadochifreapp.MoscaDoChifreAppSingleton;
import com.facom.rvns.moscadochifreapp.R;
import com.facom.rvns.moscadochifreapp.database.AppDatabaseSingleton;
import com.facom.rvns.moscadochifreapp.database.model.Result;
import com.facom.rvns.moscadochifreapp.dialog.InsertInfoDialog;
import com.facom.rvns.moscadochifreapp.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class CountActivity extends AppCompatActivity  implements InsertInfoDialog.InsertInfoDialogListener {

    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int RESULT_LOAD_IMG = 2;

    public static final int RESULT_CONTAGEM_REALIZADA = 3;
    public static final int RESULT_INICIAR_CONTAGEM = 4;


    public static final String TAG = "MainActivity";

    private File cameraFile;
    private LinearLayout linearImagemCarregada;
    private TextView txtNomeContagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);

        linearImagemCarregada = (LinearLayout)findViewById(R.id.linearImagemCarregada);
        txtNomeContagem = (TextView)findViewById(R.id.txtNomeContagem);
        txtNomeContagem.setText(MoscaDoChifreAppSingleton.getInstance().getCountSelected().name.equals("")  ? txtNomeContagem.getText() : MoscaDoChifreAppSingleton.getInstance().getCountSelected().name) ;

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
                startActivityForResult(i, RESULT_CONTAGEM_REALIZADA);
            }
        });

        Button btnIniciarContagem = findViewById(R.id.btnIniciarContagem);
        btnIniciarContagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MoscaDoChifreAppSingleton.getInstance().getCountResultsNotProcessed().size() == 0) {
                    Toast.makeText(CountActivity.this, "Adicione pelo menos uma imagem!", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(CountActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Iniciar Contagem?")
                        .setMessage("Iniciar as contagem de moscas-dos-chifres de todas as fotos carregadas?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getBaseContext(), ResultsActivity.class);
                                i.putExtra("Tipo", ResultsActivity.INICIAR_CONTAGEM);
                                startActivityForResult(i, RESULT_INICIAR_CONTAGEM);
                            }

                        })
                        .setNegativeButton("Não", null)
                        .show();


            }
        });
        addImagesToLinearLayout();

    }

    /**
     * Metodo que inicia a activity responsável pela captura da imagem utilizando a camera do dispositivo
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            cameraFile = Utils.createImageFile(MoscaDoChifreAppSingleton.getInstance().getStorageDirSource());

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

            InsertInfoDialog insertInfoDialog = new InsertInfoDialog(requestCode, data);
            insertInfoDialog.show(getSupportFragmentManager(), "DIALOG");
        }

        else if (resultCode == FullScreenImage.DELETE ||
                resultCode == RESULT_CONTAGEM_REALIZADA ||
                resultCode == RESULT_INICIAR_CONTAGEM ||
                resultCode == ResultsActivity.VOLTAR){
            linearImagemCarregada.removeAllViews();
            addImagesToLinearLayout();
        }


        //caso a captura da imagem pela camera seja cancelada
        //else{
        //    //deleta o arquivo que seria a imagem
        //    if (cameraFile != null)
        //        cameraFile.delete();
        //}
    }


    private void setImageViewBitmap(final Result result){

        final File file = new File(result.photoPath);

        View child = getLayoutInflater().inflate(R.layout.image_thumbnail, null);

        final ImageView imageThumbnail = child.findViewById(R.id.imageBoi);
        TextView txtArquivo = child.findViewById(R.id.txtArquivo);
        TextView txtIdentificador = child.findViewById(R.id.txtIdentificador);
        TextView txtProcessada = child.findViewById(R.id.txtProcessada);
        txtArquivo.setText(file.getName());
        txtIdentificador.setText(result.identification);

        if (result.indProcessado == 1) {
            txtProcessada.setText("Processada!");
        }

        imageThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startFullscreen(CountActivity.this, result);
            }
        });

        linearImagemCarregada.addView(child);
        Picasso.with(this).load(file).fit().centerCrop().into(imageThumbnail, new Callback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: TRUE");
            }

            @Override
            public void onError() {
                Log.i(TAG, "onError: TRUE");
                Picasso.with(CountActivity.this).load(file).into(imageThumbnail);
            }
        });
    }

    /**
     * Inicia a activity para carregar um arquivo de imagem salvo no dispositivo
     */
    private void loadImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    private void addImagesToLinearLayout(){

        List<Result> results = MoscaDoChifreAppSingleton.getInstance().getCountResults();

        for (Result result : results) {
            Log.d("Files", "FileName:" + result.id);
            setImageViewBitmap(result);
        }
    }

    @Override
    public void onDialogPositiveClick(String strCowIdentification, int requestCode, Intent data) {

        Result result = new Result();
        result.identification = strCowIdentification;

        switch (requestCode) {

            case REQUEST_TAKE_PHOTO:
                result.photoPath = cameraFile.getAbsolutePath();
                break;
            case RESULT_LOAD_IMG:

                File photoFile = Utils.createImageFile(MoscaDoChifreAppSingleton.getInstance().getStorageDirSource());
                Utils.copyStream(this, data.getData(), photoFile);
                result.photoPath = photoFile.getAbsolutePath();
                break;
        }
        MoscaDoChifreAppSingleton.getInstance().insertResult(result);
        setImageViewBitmap(result);
    }

}