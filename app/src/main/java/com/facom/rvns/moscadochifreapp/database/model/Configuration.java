package com.facom.rvns.moscadochifreapp.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Configuration {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "valor_suav_1")
    public int valorSuav1;

    @ColumnInfo(name = "valor_suav_2")
    public int valorSuav2;

    @ColumnInfo(name = "valor_suav_3")
    public int valorSuav3;

    @ColumnInfo(name = "valor_erosao_1")
    public int valorErosao1;

    @ColumnInfo(name = "valor_erosao_2")
    public int valorErosao2;

    @ColumnInfo(name = "valor_erosao_3")
    public int valorErosao3;

    @ColumnInfo(name = "valor_dilat_1")
    public int valorDilat1;

    @ColumnInfo(name = "valor_dilat_2")
    public int valorDilat2;

    @ColumnInfo(name = "valor_dilat_3")
    public int valorDilat3;

    @ColumnInfo(name = "valor_lim_pix_1")
    public int valorLimiarPixel1;

    @ColumnInfo(name = "valor_lim_pix_2")
    public int valorLimiarPixel2;

    @ColumnInfo(name = "valor_lim_borda_1")
    public int valorLimiarBorda1;

    @ColumnInfo(name = "valor_lim_borda_2")
    public int valorLimiarBorda2;

    public void setDefaultValues(){
        valorSuav1 = 5;
        valorSuav2 = 25;
        valorSuav3 = 25;
        valorErosao1 = 3;
        valorErosao2 = 3;
        valorErosao3 = 1;
        valorDilat1 = 21;
        valorDilat2 = 21;
        valorDilat3 = 5;
        valorLimiarPixel1 = 15;
        valorLimiarPixel2 = 70;
        valorLimiarBorda1 = 15;
        valorLimiarBorda2 = 45;
    }

}
