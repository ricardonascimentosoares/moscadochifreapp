package com.facom.rvns.moscadochifreapp.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.facom.rvns.moscadochifreapp.OutputWritable;
import com.facom.rvns.moscadochifreapp.R;
import com.facom.rvns.moscadochifreapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultsActivity extends AppCompatActivity implements OutputWritable {

    public static final int INICIAR_CONTAGEM = 1;
    public static final int CONTAGENS_REALIZADAS = 2;

    private AlertDialog dialog;
    private TextView txtStatusProcessamento;
    private int numberOfFiles;
    private int processCounter = 0;
    private LinearLayout linearImagemProcessada;
    private ExecutorService executor;
    private LinearLayout linearProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        linearProgress = findViewById(R.id.linearProgress);
        txtStatusProcessamento = findViewById(R.id.txtStatusProcessamento);
        linearImagemProcessada = findViewById(R.id.linearImagemProcessada);

        int type = getIntent().getIntExtra("Tipo", 0);

        addImagesToLinearLayout(Utils.getStorageDirTarget().getAbsolutePath());

        if (type == INICIAR_CONTAGEM) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //createProgressDialog();

            linearProgress.setVisibility(View.VISIBLE);

            initCounterExecution();

            File[] files = Utils.loadFiles(Utils.getStorageDirSource().getAbsolutePath());
            numberOfFiles = files.length;
            txtStatusProcessamento.setText(String.valueOf(numberOfFiles));

            for (File file : files) {
                startPython(file);
            }
        }

    }

    @Override
    public void writeOutput(String output) {

    }

    @Override
    public void writeResult(String result) {

    }

    /**
     * Metodo principal que inicia a integracao do Java com Python e faz a chamada dos métodos
     */
    private void startPython(final File file){

        final Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {

                //inicia o single do Python
                Python py = Python.getInstance();
                PyObject pyobj = py.getModule("main");
                OutputWritable activityRef = ResultsActivity.this;
                PyObject pyResponse = pyobj.callAttr("realiza_contagem", file.getAbsolutePath(), Utils.getStorageDirTarget().getAbsolutePath(), activityRef);

                final String outputFilename = pyResponse.toString();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        processCounter++;
                        txtStatusProcessamento.setText(String.valueOf(numberOfFiles-processCounter));

                        if (processCounter == numberOfFiles)
                            linearProgress.setVisibility(View.GONE);
                        setImageViewBitmap(new File(outputFilename));
                        Toast.makeText(ResultsActivity.this, "Contagem Realizada!", Toast.LENGTH_SHORT).show();

                        file.delete();
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

    private void addImagesToLinearLayout(String path){

        File[] files = Utils.loadFiles(path);

        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
            setImageViewBitmap(files[i]);
        }
    }

    /**
     *
     * @param file
     */
    private void setImageViewBitmap(final File file){

        View child = getLayoutInflater().inflate(R.layout.image_thumbnail, null);

        ImageView imageThumbnail = child.findViewById(R.id.imageBoi);
        TextView txtIdentificador = child.findViewById(R.id.txtIdentificador);
        txtIdentificador.setText(file.getName());

        imageThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startFullscreen(ResultsActivity.this, file.getAbsolutePath());
            }
        });

        Picasso.with(this).load(file).fit().centerCrop().into(imageThumbnail);

        linearImagemProcessada.addView(child);
    }

    @Override
    protected void onResume() {
        super.onResume();
        linearImagemProcessada.removeAllViews();
        addImagesToLinearLayout(Utils.getStorageDirTarget().getAbsolutePath());
    }


    private void initCounterExecution(){
        // "context" must be an Activity, Service or Application object from your app.
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(getApplicationContext()));
        }

        executor = Executors.newFixedThreadPool(3);
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
}