package com.facom.rvns.moscadochifreapp.model;

public class CowModel {

    private String id;
    private String picturePath;

    public CowModel(String id, String picturePath){
        this.id = id;
        this.picturePath = picturePath;
    }


    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
