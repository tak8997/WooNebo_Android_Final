package com.example.samsung.woonebo_android.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by SAMSUNG on 2016-11-09.
 */
public class ProductData implements Serializable {
    private int id;
    private String desc;
    private String name;
    private String image;    //image Url
    private String url;     //webView Url
    private int price;

    public ProductData(String desc, int id, String image, String name, int price, String url) {
        this.desc = desc;
        this.id = id;
        this.image = image;
        this.name = name;
        this.price = price;
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
