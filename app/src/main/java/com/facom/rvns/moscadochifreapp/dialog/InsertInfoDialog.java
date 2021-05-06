package com.facom.rvns.moscadochifreapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.facom.rvns.moscadochifreapp.R;

public class InsertInfoDialog extends DialogFragment {

    private final int requestCode;
    private final Intent data;

    public interface NoticeDialogListener {
        void onDialogPositiveClick(String strCowIdentification, int requestCode, Intent data);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener listener;


    public InsertInfoDialog(int requestCode, Intent data){
        this.requestCode = requestCode;
        this.data = data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.dialog_insert_info_cow, container, false);
        setCancelable(false);
        final EditText editIdentification = view.findViewById(R.id.editIdentification);
        Button btnDialogOk = view.findViewById(R.id.btnDialogOk);

        btnDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogPositiveClick(editIdentification.getText().toString(), requestCode, data);
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
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
