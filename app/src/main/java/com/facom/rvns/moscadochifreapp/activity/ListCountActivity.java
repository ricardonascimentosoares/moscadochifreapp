package com.facom.rvns.moscadochifreapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.facom.rvns.moscadochifreapp.MoscaDoChifreAppSingleton;
import com.facom.rvns.moscadochifreapp.R;
import com.facom.rvns.moscadochifreapp.database.AppDatabaseSingleton;
import com.facom.rvns.moscadochifreapp.database.model.Count;
import com.facom.rvns.moscadochifreapp.dialog.InsertCountNameDialog;

import java.util.List;

public class ListCountActivity extends AppCompatActivity implements InsertCountNameDialog.InsertCountNameDialogListener {



    public static final String TAG = "ListCountActivity";

    public static final int REQUEST_CODE = 100;
    private static final int RESULT_NOVA_CONTAGEM = 123;
    private static final Integer READ_EXST = 333;
    private static final Integer WRITE_EXST = 444;
    private ListView listContagens;
    private ArrayAdapter<Count> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_list);

        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST);
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXST);

        // "context" must be an Activity, Service or Application object from your app.
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(getApplicationContext()));
        }

        AppDatabaseSingleton.init(getApplicationContext());

        listContagens = (ListView) findViewById(R.id.listContagens);

        List<Count> counts = MoscaDoChifreAppSingleton.getInstance().getAllCounts();

        adapter = new ArrayAdapter<Count>(this,
                android.R.layout.simple_list_item_1, counts);

        listContagens.setAdapter(adapter);
        listContagens.setEmptyView(findViewById((R.id.empty_list_item)));
        listContagens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoscaDoChifreAppSingleton.getInstance().loadCount(adapter.getItem(position).id);
                Intent i = new Intent(getBaseContext(), CountActivity.class);
                startActivity(i);
            }
        });

        listContagens.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(ListCountActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Apagar Contagem?")
                        .setMessage("Deseja apagar essa contagem e todas as fotos capturadas e processadas nela?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Count count = adapter.getItem(position);
                                MoscaDoChifreAppSingleton.getInstance().deleteCount(count);


                                adapter.clear();
                                adapter.addAll(MoscaDoChifreAppSingleton.getInstance().getAllCounts());
                                adapter.notifyDataSetChanged();
                            }

                        })
                        .setNegativeButton("Não", null)
                        .show();
                return true;
            }
        });


        Button btnNovaContagem = findViewById(R.id.btnNovaContagem);
        btnNovaContagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InsertCountNameDialog insertCountNameDialog = new InsertCountNameDialog(REQUEST_CODE);
                insertCountNameDialog.show(getSupportFragmentManager(), "DIALOG");

            }
        });

    }


    @Override
    public void onDialogPositiveClick(String countName, int requestCode) {

        MoscaDoChifreAppSingleton.getInstance().createNewDir(ListCountActivity.this, countName);
        adapter.clear();
        adapter.addAll(MoscaDoChifreAppSingleton.getInstance().getAllCounts());
        adapter.notifyDataSetChanged();
        Intent i = new Intent(getBaseContext(), CountActivity.class);
        startActivityForResult(i, RESULT_NOVA_CONTAGEM);

    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ListCountActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    ListCountActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ListCountActivity.this,
                        new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(ListCountActivity.this,
                        new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, permission + " já concedida!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}