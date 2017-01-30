package com.example.samsung.woonebo_android.service;

import com.example.samsung.woonebo_android.model.ProductData;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by SAMSUNG on 2016-10-31.
 */

public interface ProductService {
    @GET("products/{id}")
    Call<ProductData> getProduct(@Path("id") int productID, @Query("idToken") String idToken);
}
