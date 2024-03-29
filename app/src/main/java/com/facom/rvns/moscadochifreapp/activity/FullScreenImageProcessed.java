package com.facom.rvns.moscadochifreapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facom.rvns.moscadochifreapp.MoscaDoChifreAppSingleton;
import com.facom.rvns.moscadochifreapp.R;
import com.facom.rvns.moscadochifreapp.database.model.Result;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.io.IOException;

public class FullScreenImageProcessed extends AppCompatActivity {

    public static final int DELETE =  7;
    private Result result;
    private PhotoView imgDisplay;
    private PhotoViewAttacher mAttacher;
    private RectF rectF;
    private Bitmap imageBitmap;
    private Bitmap imageProcessedBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen_image);

        Bundle extras = getIntent().getExtras();

        result = (Result)extras.getSerializable("result");

        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse("file:///" + result.photoPath));
            imageProcessedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse("file:///" + result.photoProcessedPath));

        } catch (IOException e) {
            e.printStackTrace();
        }

        imgDisplay = findViewById(R.id.imgDisplay);

        Button btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FullScreenImageProcessed.this.finish();
            }
        });

        Button btnApagar = findViewById(R.id.btnApagar);
        btnApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(FullScreenImageProcessed.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Apagar Contagem")
                        .setMessage("Tem certeza que deseja apagar essa contagem?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                MoscaDoChifreAppSingleton.getInstance().deleteResultProcessed(result);

                                Toast.makeText(FullScreenImageProcessed.this, "Contagem Apagada Com Sucesso!", Toast.LENGTH_SHORT).show();

                                Intent returnIntent = new Intent();
                                setResult(FullScreenImageProcessed.DELETE,returnIntent);
                                finish();
                            }

                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });

        ToggleButton btnComparar = findViewById(R.id.btnComparar);
        btnComparar.setVisibility(View.VISIBLE);
        btnComparar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setImage(isChecked ? imageBitmap : imageProcessedBitmap);
            }
        });


        imgDisplay.setImageBitmap(imageProcessedBitmap);

        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        mAttacher = new PhotoViewAttacher(imgDisplay);
    }


    private void setImage(Bitmap bitmap){

        // salva os valores de zoom e translacao da imagem anterior
        final float scaleValue = mAttacher.getScale();
        rectF = mAttacher.getDisplayRect();
        final float xPrev = (float) (imgDisplay.getWidth()/2.0 - rectF.left); // this referes to onSingleTapConfirmed in DefaultOnDoubleTapListener.java
        final float yPrev = (float) (imgDisplay.getHeight()/2.0 - rectF.top);

        imgDisplay.setImageBitmap(bitmap);

        //aplica os valores de zoom e translacao na nova imagem para permitir melhor comparacao
        //deve estar dentro Handler para que seja aplicado corretamente
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                //caso o zoom entre as imagens seja incompativel, seta os valores maximo e minimo
                if (scaleValue < imgDisplay.getMinimumScale())
                    mAttacher.setScale(imgDisplay.getMinimumScale(),false);
                else if (scaleValue > imgDisplay.getMaximumScale())
                    mAttacher.setScale(imgDisplay.getMaximumScale(),false);
                else
                    mAttacher.setScale(scaleValue,false);

                rectF = mAttacher.getDisplayRect();
                float xAfter = (float) (imgDisplay.getWidth()/2.0 - rectF.left);
                float yAfter = (float) (imgDisplay.getHeight()/2.0 - rectF.top);
                mAttacher.onDrag(xAfter - xPrev,yAfter - yPrev);
            }
        });

    }



}