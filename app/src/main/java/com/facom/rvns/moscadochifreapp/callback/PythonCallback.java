package com.facom.rvns.moscadochifreapp.callback;

import com.facom.rvns.moscadochifreapp.database.AppDatabaseSingleton;
import com.facom.rvns.moscadochifreapp.database.model.Result;
import com.facom.rvns.moscadochifreapp.interfaces.OutputWritable;
import com.facom.rvns.moscadochifreapp.utils.Utils;

import java.util.Date;

public class PythonCallback implements OutputWritable {

    private Result result;

    public PythonCallback (Result result){
        this.result = result;
    }

    @Override
    public void writeOutput(String output) {
        String s = output;

    }

    @Override
    public void writeResult(int fliesCount) {

        result.fliesCount = fliesCount;

    }

    public Result getResult() {
        return result;
    }

    public void saveResult(String outputFilename) {
        result.photoProcessedPath = outputFilename;
        result.countDate = Utils.fromDate(new Date());
        result.indProcessado = 1;
        AppDatabaseSingleton.getInstance().resultDao().update(result);
    }
}
