package com.facom.rvns.moscadochifreapp.callback;

import com.facom.rvns.moscadochifreapp.database.AppDatabaseSingleton;
import com.facom.rvns.moscadochifreapp.database.Result;
import com.facom.rvns.moscadochifreapp.interfaces.OutputWritable;
import com.facom.rvns.moscadochifreapp.utils.Utils;

import java.util.Date;

public class PythonCallback implements OutputWritable {

    private Result result;

    @Override
    public void writeOutput(String output) {
        String s = output;

    }

    @Override
    public void writeResult(int fliesCount) {

        result = new Result();
        result.fliesCount = fliesCount;

    }

    public Result getResult() {
        return result;
    }

    public void saveResult(String outputFilename) {
        result.photoPath = outputFilename;
        result.countDate = Utils.fromDate(new Date());
        AppDatabaseSingleton.getInstance().resultDao().insertAll(result);
    }
}
