package com.facom.rvns.moscadochifreapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.facom.rvns.moscadochifreapp.ExcelExporter;
import com.facom.rvns.moscadochifreapp.MoscaDoChifreAppSingleton;
import com.facom.rvns.moscadochifreapp.callback.PythonCallback;
import com.facom.rvns.moscadochifreapp.database.AppDatabaseSingleton;
import com.facom.rvns.moscadochifreapp.database.model.Result;
import com.facom.rvns.moscadochifreapp.R;
import com.facom.rvns.moscadochifreapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultsActivity extends AppCompatActivity  {

    public static final int INICIAR_CONTAGEM = 1;
    public static final int CONTAGENS_REALIZADAS = 2;

    private AlertDialog dialog;
    private TextView txtStatusProcessamento;
    private int numberOfFiles;
    private int processCounter = 0;
    private LinearLayout linearImagemProcessada;
    private ExecutorService executor;
    private LinearLayout linearProgress;
    private PyObject pyobj;
    private TextView txtContagem;
    private TextView txtMediaGeral;
    private TextView txtDataContagem;
    private View btnExportarDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        linearProgress = findViewById(R.id.linearProgress);
        txtStatusProcessamento = findViewById(R.id.txtStatusProcessamento);
        linearImagemProcessada = findViewById(R.id.linearImagemProcessada);
        txtContagem = findViewById(R.id.txtContagem);
        txtMediaGeral = findViewById(R.id.txtMediaGeral);
        txtDataContagem = findViewById(R.id.txtDataContagem);
        btnExportarDados = findViewById(R.id.btnExportarDados);

        btnExportarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Result> resultsProcessed = MoscaDoChifreAppSingleton.getInstance().getCountResultsProcessed();

                if (resultsProcessed.size() == 0){
                    Toast.makeText(ResultsActivity.this, "Não há resultados para exportar!", Toast.LENGTH_LONG).show();
                    return;
                }

                File file = ExcelExporter.export(MoscaDoChifreAppSingleton.getInstance().getCountSelected(), MoscaDoChifreAppSingleton.getInstance().getCountResultsProcessed());

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = FileProvider.getUriForFile(ResultsActivity.this,"com.facom.rvns.moscadochifreapp.fileprovider", file);
                sharingIntent.setType("*/*");
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Compartilhar arquivo"));
            }
        });

        int type = getIntent().getIntExtra("Tipo", 0);

        if (type == INICIAR_CONTAGEM) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //createProgressDialog();

            linearProgress.setVisibility(View.VISIBLE);

            executor = Executors.newFixedThreadPool(2);

            List<Result> resultsNotProcessed = MoscaDoChifreAppSingleton.getInstance().getCountResultsNotProcessed();

            numberOfFiles = resultsNotProcessed.size();
            txtStatusProcessamento.setText(String.valueOf(numberOfFiles));

            for (Result result : resultsNotProcessed) {
                startPython(result);
            }
        }

        addImagesToLinearLayout();
    }


    /**
     * Metodo principal que inicia a integracao do Java com Python e faz a chamada dos métodos
     */
    private void startPython(final Result result){

        final Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {

                //inicia o single do Python
                if (pyobj == null)
                    pyobj = Python.getInstance().getModule("main");

                PythonCallback pythonCallback = new PythonCallback();
                PyObject pyResponse = pyobj.callAttr("realiza_contagem", result.photoPath, MoscaDoChifreAppSingleton.getInstance().getStorageDirTarget().getAbsolutePath(), pythonCallback);

                result.photoProcessedPath = pyResponse.toString();
                result.fliesCount = pythonCallback.getFliesCount();
                final Result resultProcessed = MoscaDoChifreAppSingleton.getInstance().processResult(result);

                processCounter++;

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        txtStatusProcessamento.setText(String.valueOf(numberOfFiles-processCounter));

                        if (processCounter == numberOfFiles) {
                            linearProgress.setVisibility(View.GONE);
                            int media = MoscaDoChifreAppSingleton.getInstance().calculateAVG();
                            txtMediaGeral.setText(String.valueOf(media));
                        }

                        setImageViewBitmap(resultProcessed);
                        Toast.makeText(ResultsActivity.this, "Contagem Realizada!", Toast.LENGTH_SHORT).show();

                        //apaga o arquivo de origem
                        //file.delete();
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
        //txtStatusProcessamento = dialogView.findViewById(R.id.txtStatusProcessamento);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
    }

    private void addImagesToLinearLayout(){

        txtContagem.setText(MoscaDoChifreAppSingleton.getInstance().getCountSelected().name.equals("")  ? txtContagem.getText() : MoscaDoChifreAppSingleton.getInstance().getCountSelected().name) ;
        txtMediaGeral.setText(String.valueOf(MoscaDoChifreAppSingleton.getInstance().getCountSelected().averageFliesCount));
        txtDataContagem.setText(Utils.toDateFormat(MoscaDoChifreAppSingleton.getInstance().getCountSelected().countDate));

        List<Result> results = MoscaDoChifreAppSingleton.getInstance().getCountResultsProcessed();

        for (Result result : results) {
            Log.d("Files", "FileName:" + result.id);
            setImageViewBitmap(result);
        }
    }

    /**
     *
     * @param result
     */
    private void setImageViewBitmap(final Result result){

        File file = new File(result.photoProcessedPath);

        View child = getLayoutInflater().inflate(R.layout.image_thumbnail, null);

        LinearLayout linearMoscasIdentificadas = child.findViewById(R.id.linearMoscasIdentificadas);
        LinearLayout linearDataContagem = child.findViewById(R.id.linearDataContagem);

        linearMoscasIdentificadas.setVisibility(View.VISIBLE);
        linearDataContagem.setVisibility(View.VISIBLE);

        ImageView imageThumbnail = child.findViewById(R.id.imageBoi);
        TextView txtIdentificador = child.findViewById(R.id.txtIdentificador);
        TextView txtDataContagem = child.findViewById(R.id.txtDataContagem);
        TextView txtMoscasIdentificadas = child.findViewById(R.id.txtMoscasIdentificadas);

        txtIdentificador.setText(file.getName());
        txtDataContagem.setText(Utils.toDateFormat(result.countDate));
        txtMoscasIdentificadas.setText(String.valueOf(result.fliesCount));

        imageThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startFullscreen(ResultsActivity.this, result);
            }
        });

        Picasso.with(this).load(file).fit().centerCrop().into(imageThumbnail);
        linearImagemProcessada.addView(child);
    }


    @Override
    public void onBackPressed() {

        if (linearProgress.getVisibility() == View.VISIBLE){
            new AlertDialog.Builder(ResultsActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Contagem Em Execução!")
                    .setMessage("Tem certeza que deseja para a contagem?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.shutdownNow();
                            finish();
                        }

                    })
                    .setNegativeButton("Não", null)
                    .show();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == FullScreenImage.DELETE){
            linearImagemProcessada.removeAllViews();
            addImagesToLinearLayout();
        }
    }
}
