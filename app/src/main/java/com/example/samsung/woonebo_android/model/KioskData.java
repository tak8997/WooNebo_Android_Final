package com.example.samsung.woonebo_android.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by SAMSUNG on 2016-10-27.
 */
public class KioskData implements Serializable {
    String kioskImageUrl;
    String kioskDesc;
    int kioskID;

    public KioskData(int kioskID, String kioskImageUrl, String kioskDesc) {
        this.kioskID = kioskID;
        this.kioskImageUrl = kioskImageUrl;
        this.kioskDesc = kioskDesc;
    }

    public String getKioskDesc() {
        return kioskDesc;
    }

    public void setKioskDesc(String kioskDesc) {
        this.kioskDesc = kioskDesc;
    }

    public int getKioskID() {
        return kioskID;
    }

    public void setKioskID(int kioskID) {
        this.kioskID = kioskID;
    }

    public String getKioskImageUrl() {
        return kioskImageUrl;
    }

    public void setKioskImageUrl(String kioskImageUrl) {
        this.kioskImageUrl = kioskImageUrl;
    }
}
