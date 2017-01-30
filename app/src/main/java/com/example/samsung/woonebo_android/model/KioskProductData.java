package com.example.samsung.woonebo_android.model;

import java.util.Date;

/**
 * Created by SAMSUNG on 2016-12-09.
 */

public class KioskProductData {
    int kioskProductID;
    String kioskProductDesc;
    String kioskProductImage;

    public KioskProductData(int kioskProductID, String kioskProductDesc, String kioskProductImage) {
        this.kioskProductDesc = kioskProductDesc;
        this.kioskProductID = kioskProductID;
        this.kioskProductImage = kioskProductImage;

    }

    public String getKioskProductDesc() {
        return kioskProductDesc;
    }

    public void setKioskProductDesc(String kioskProductDesc) {
        this.kioskProductDesc = kioskProductDesc;
    }

    public int getKioskProductID() {
        return kioskProductID;
    }

    public void setKioskProductID(int kioskProductID) {
        this.kioskProductID = kioskProductID;
    }

    public String getKioskProductImage() {
        return kioskProductImage;
    }

    public void setKioskProductImage(String kioskProductImage) {
        this.kioskProductImage = kioskProductImage;
    }
}
