package com.facom.rvns.moscadochifreapp.interfaces;

/**
 * Interface necessaria para o Python chamar um método Java
 */
public interface OutputWritable {

    void writeOutput(String output);
    void writeResult(int result);
}
