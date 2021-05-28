package com.facom.rvns.moscadochifreapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.facom.rvns.moscadochifreapp.MoscaDoChifreAppSingleton;
import com.facom.rvns.moscadochifreapp.R;
import com.facom.rvns.moscadochifreapp.database.AppDatabaseSingleton;
import com.facom.rvns.moscadochifreapp.database.model.Result;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;

public class FullScreenImage extends AppCompatActivity {

    private Result result;
    public static final int DELETE =  7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen_image);

        Bundle extras = getIntent().getExtras();

        result = (Result)extras.getSerializable("result");

        PhotoView imgDisplay = findViewById(R.id.imgDisplay);
        Button btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FullScreenImage.this.finish();
            }
        });

        Button btnApagar = findViewById(R.id.btnApagar);
        btnApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(FullScreenImage.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Apagar Foto")
                        .setMessage("Tem certeza que deseja apagar essa foto?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                MoscaDoChifreAppSingleton.getInstance().deleteResult(result);

                                Toast.makeText(FullScreenImage.this, "Foto Apagada Com Sucesso!", Toast.LENGTH_SHORT).show();
                                Intent returnIntent = new Intent();
                                setResult(FullScreenImage.DELETE,returnIntent);
                                finish();
                            }

                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });

        try {
            imgDisplay.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse("file:///"+result.photoPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
}