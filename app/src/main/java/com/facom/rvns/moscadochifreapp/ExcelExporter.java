package com.facom.rvns.moscadochifreapp;

import android.os.Environment;

import com.facom.rvns.moscadochifreapp.database.model.Count;
import com.facom.rvns.moscadochifreapp.database.model.Result;
import com.facom.rvns.moscadochifreapp.utils.Utils;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelExporter {

    public static File export(Count count, List<Result> results) {
        String csvFile = "export_"+new Date().getTime()+".xls";

        File directory = new File(MoscaDoChifreAppSingleton.getInstance().getCountSelected().countPath);

        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Locale.ENGLISH.getLanguage(), Locale.ENGLISH.getCountry()));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);

            //Excel sheetA first sheetA
            WritableSheet sheetA = workbook.createSheet("Resultados", 0);

            // column and row titles
            sheetA.addCell(new Label(0, 0, "Identificação do Boi"));
            sheetA.addCell(new Label(1, 0, "Data/Hora da contagem"));
            sheetA.addCell(new Label(2, 0, "Foto Original"));
            sheetA.addCell(new Label(3, 0, "Foto Processada"));
            sheetA.addCell(new Label(4, 0, "Moscas-dos-chifres Encontradas"));

            for (int i = 1; i <= results.size(); i++){
                sheetA.addCell(new Label(0, i, String.valueOf(results.get(i-1).identification)));
                sheetA.addCell(new Label(1, i, Utils.toDateFormat(results.get(i - 1).countDate)));
                sheetA.addCell(new Label(2, i, new File(results.get(i - 1).photoPath).getName()));
                sheetA.addCell(new Label(3, i, new File(results.get(i - 1).photoProcessedPath).getName()));
                sheetA.addCell(new Label(4, i, String.valueOf(results.get(i-1).fliesCount)));
            }

            //Excel sheetB represents second sheet
            WritableSheet sheetB = workbook.createSheet("Geral", 1);

            // column and row titles
            sheetB.addCell(new Label(0, 0, "Identificador da Contagem"));
            sheetB.addCell(new Label(1, 0, "Data Início da Contagem"));
            sheetB.addCell(new Label(2, 0, "Média de Moscas-dos-chifres Encontrada"));

            sheetB.addCell(new Label(0, 1, count.name));
            sheetB.addCell(new Label(1, 1, Utils.toDateFormat(count.countDate)));
            sheetB.addCell(new Label(2, 1, String.valueOf(count.averageFliesCount)));

            // close workbook
            workbook.write();
            workbook.close();

            return file;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
