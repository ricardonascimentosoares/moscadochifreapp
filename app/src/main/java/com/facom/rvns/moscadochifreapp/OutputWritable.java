package com.facom.rvns.moscadochifreapp;

/**
 * Interface necessaria para o Python chamar um método Java
 */
public interface OutputWritable {

    void writeOutput(String output);
    void writeResult(String result);
}
