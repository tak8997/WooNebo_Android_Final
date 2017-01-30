package com.example.samsung.woonebo_android.util;

import com.example.samsung.woonebo_android.model.ProductData;

import java.util.ArrayList;

/**
 * Created by SAMSUNG on 2016-11-15.
 */

public class ShoppingCartHelper {
    private static ArrayList<ProductData> cart = new ArrayList<ProductData>();

    public static ArrayList<ProductData> getCart() {
        return cart;
    }
    public static void add(ProductData product) {
        cart.add(product);
    }
    public static void remove(int index) {
        cart.remove(index);
    }
}