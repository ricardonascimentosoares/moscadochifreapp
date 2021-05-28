package com.facom.rvns.moscadochifreapp.callback;

import com.facom.rvns.moscadochifreapp.MoscaDoChifreAppSingleton;
import com.facom.rvns.moscadochifreapp.database.AppDatabaseSingleton;
import com.facom.rvns.moscadochifreapp.database.model.Result;
import com.facom.rvns.moscadochifreapp.interfaces.OutputWritable;
import com.facom.rvns.moscadochifreapp.utils.Utils;

import java.util.Date;

public class PythonCallback implements OutputWritable {

    private int fliesCount;


    @Override
    public void writeOutput(String output) {
        String s = output;

    }

    @Override
    public void writeResult(int fliesCount) {

        this.fliesCount = fliesCount;

    }

    public int getFliesCount() {
        return fliesCount;
    }
}
