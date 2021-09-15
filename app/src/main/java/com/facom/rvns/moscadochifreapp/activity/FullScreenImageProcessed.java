package com.facom.rvns.moscadochifreapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.IOException;

public class FullScreenImageProcessed extends AppCompatActivity {

    public static final int DELETE =  7;
    private Result result;
    private PhotoView imgDisplay;
    private float pivotX;
    private float pivotY;
    private float scale;
    private float x;
    private float y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen_image);

        Bundle extras = getIntent().getExtras();

        result = (Result)extras.getSerializable("result");

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
                setImage(isChecked ? result.photoPath : result.photoProcessedPath);
            }
        });

        setImage(result.photoProcessedPath);
    }


    private void setImage(String imagePath){
        pivotX = imgDisplay.getPivotX();
        pivotY = imgDisplay.getPivotY();
        scale = imgDisplay.getScale();
        try {
            imgDisplay.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse("file:///" + imagePath)));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (scale < imgDisplay.getMinimumScale())
            scale = imgDisplay.getMinimumScale();
        else if (scale > imgDisplay.getMaximumScale())
            scale = imgDisplay.getMaximumScale();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                imgDisplay.setScale(scale, pivotX, pivotY, false);
            }
        });



    }



}