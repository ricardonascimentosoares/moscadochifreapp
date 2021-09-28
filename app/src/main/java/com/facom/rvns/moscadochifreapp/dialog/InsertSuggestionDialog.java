package com.facom.rvns.moscadochifreapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.facom.rvns.moscadochifreapp.R;
import com.facom.rvns.moscadochifreapp.activity.ResultsActivity;
import com.facom.rvns.moscadochifreapp.database.AppDatabaseSingleton;
import com.facom.rvns.moscadochifreapp.database.model.Result;

public class InsertSuggestionDialog extends DialogFragment {

    private Result result;

    public interface InsertSuggestionDialogListener {
        void onDialogPositiveClick();
    }

    // Use this instance of the interface to deliver action events
    InsertSuggestionDialogListener listener;


    public InsertSuggestionDialog(Result result){
        this.result = result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.dialog_insert_suggestion, container, false);
        setCancelable(true);
        final EditText editValorSugestao = view.findViewById(R.id.editValorSugestao);
        Button btnDialogOk = view.findViewById(R.id.btnDialogOk);

        //se já houver valor informado pelo usuário
        if (result.fliesCountSuggested != 0)
            editValorSugestao.setText(String.valueOf(result.fliesCountSuggested ));

        btnDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                result.fliesCountSuggested = Integer.parseInt(editValorSugestao.getText().toString());
                int i = AppDatabaseSingleton.getInstance().resultDao().update(result);

                if (i > 0)
                    Toast.makeText(getContext(), "Valor salvo com sucesso!", Toast.LENGTH_SHORT).show();

                listener.onDialogPositiveClick();
                dismiss();

            }
        });


        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (InsertSuggestionDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
