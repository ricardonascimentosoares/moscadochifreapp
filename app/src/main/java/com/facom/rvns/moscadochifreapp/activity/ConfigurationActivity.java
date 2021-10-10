package com.facom.rvns.moscadochifreapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facom.rvns.moscadochifreapp.MoscaDoChifreAppSingleton;
import com.facom.rvns.moscadochifreapp.R;
import com.facom.rvns.moscadochifreapp.database.model.Configuration;

public class ConfigurationActivity extends AppCompatActivity {

    private EditText editSuav1;
    private EditText editSuav2;
    private EditText editSuav3;
    private EditText editEro1;
    private EditText editEro2;
    private EditText editEro3;
    private EditText editDil1;
    private EditText editDil2;
    private EditText editDil3;
    private EditText editLimiarPix1;
    private EditText editLimiarPix2;
    private EditText editLimiarBorda1;
    private EditText editLimiarBorda2;
    private Button btnSalvarConfig;
    private Button btnRestaurarPadroes;
    private Button btnCancelarConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        editSuav1 = findViewById(R.id.editSuav1);
        editSuav2 = findViewById(R.id.editSuav2);
        editSuav3 = findViewById(R.id.editSuav3);
        editEro1 = findViewById(R.id.editEro1);
        editEro2 = findViewById(R.id.editEro2);
        editEro3 = findViewById(R.id.editEro3);

        editDil1 = findViewById(R.id.editDil1);
        editDil2 = findViewById(R.id.editDil2);
        editDil3 = findViewById(R.id.editDil3);

        editLimiarPix1 = findViewById(R.id.editLimiarPix1);
        editLimiarPix2 = findViewById(R.id.editLimiarPix2);

        editLimiarBorda1 = findViewById(R.id.editLimiarBorda1);
        editLimiarBorda2 = findViewById(R.id.editLimiarBorda2);

        btnSalvarConfig = findViewById(R.id.btnSalvarConfig);
        btnSalvarConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Configuration configuration = MoscaDoChifreAppSingleton.getInstance().getConfiguration();

                try {
                    configuration.valorSuav1 = Integer.parseInt(editSuav1.getText().toString());
                    configuration.valorSuav2 = Integer.parseInt(editSuav2.getText().toString());
                    configuration.valorSuav3 = Integer.parseInt(editSuav3.getText().toString());

                    configuration.valorErosao1 = Integer.parseInt(editEro1.getText().toString());
                    configuration.valorErosao2 = Integer.parseInt(editEro2.getText().toString());
                    configuration.valorErosao3 = Integer.parseInt(editEro3.getText().toString());

                    configuration.valorDilat1 = Integer.parseInt(editDil1.getText().toString());
                    configuration.valorDilat2 = Integer.parseInt(editDil2.getText().toString());
                    configuration.valorDilat3 = Integer.parseInt(editDil3.getText().toString());

                    configuration.valorLimiarPixel1 = Integer.parseInt(editLimiarPix1.getText().toString());
                    configuration.valorLimiarPixel2 = Integer.parseInt(editLimiarPix2.getText().toString());

                    configuration.valorLimiarBorda1 = Integer.parseInt(editLimiarBorda1.getText().toString());
                    configuration.valorLimiarBorda2 = Integer.parseInt(editLimiarBorda2.getText().toString());

                    MoscaDoChifreAppSingleton.getInstance().updateConfiguration(configuration);

                    Toast.makeText(ConfigurationActivity.this, "Parâmetros salvos com Sucesso!", Toast.LENGTH_SHORT).show();
                }
                catch (NumberFormatException e){
                    Toast.makeText(ConfigurationActivity.this, "Erro! Todos os parâmetros devem ser preenchidos!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnRestaurarPadroes = findViewById(R.id.btnRestaurarPadroes);
        btnRestaurarPadroes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoscaDoChifreAppSingleton.getInstance().setConfigurationDefaultValues();
                loadSavedValues();

                Toast.makeText(ConfigurationActivity.this, "Parâmetros de fábrica restaurados!", Toast.LENGTH_SHORT).show();

            }
        });

        btnCancelarConfig = findViewById(R.id.btnCancelarConfig);
        btnCancelarConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        loadSavedValues();
    }

    private void loadSavedValues() {
        Configuration configuration = MoscaDoChifreAppSingleton.getInstance().getConfiguration();
        editSuav1.setText(String.valueOf(configuration.valorSuav1));
        editSuav2.setText(String.valueOf(configuration.valorSuav2));
        editSuav3.setText(String.valueOf(configuration.valorSuav3));

        editEro1.setText(String.valueOf(configuration.valorErosao1));
        editEro2.setText(String.valueOf(configuration.valorErosao2));
        editEro3.setText(String.valueOf(configuration.valorErosao3));

        editDil1.setText(String.valueOf(configuration.valorDilat1));
        editDil2.setText(String.valueOf(configuration.valorDilat2));
        editDil3.setText(String.valueOf(configuration.valorDilat3));

        editLimiarPix1.setText(String.valueOf(configuration.valorLimiarPixel1));
        editLimiarPix2.setText(String.valueOf(configuration.valorLimiarPixel2));

        editLimiarBorda1.setText(String.valueOf(configuration.valorLimiarBorda1));
        editLimiarBorda2.setText(String.valueOf(configuration.valorLimiarBorda2));
    }
}