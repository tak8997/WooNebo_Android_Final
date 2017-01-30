package com.example.samsung.woonebo_android.service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by SAMSUNG on 2016-12-09.
 */

public interface KioskProductService {
    @GET("kiosks/{id}/products")
    Call<JsonObject> getKioskProduct(@Path("id") int kioskID, @Query("idToken") String idToken);
}
