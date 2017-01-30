package com.example.samsung.woonebo_android.service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by SAMSUNG on 2016-11-11.
 */
public interface AuthService {
    @FormUrlEncoded
    @POST("users/auth")
    Call<JsonObject> getAuth(@Field("idToken") String idToken);
}
