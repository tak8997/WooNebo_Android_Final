package com.example.samsung.woonebo_android.service;

import com.example.samsung.woonebo_android.R;

import com.example.samsung.woonebo_android.model.KioskData;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by SAMSUNG on 2016-10-27.
 */
public interface KioskService {
//    public static final String ARI_URL = "http://jsonplaceholder.typicode.com/";
//    http://jsonplaceholder.typicode.com/comments?postId=1
//    @GET("comments")        //@GET("api주소")
//    Call<List<KioskData>> getKiosk(@Query("postId") int postId);        //postId->변수이름, int postId->안드로이드에서 보낼 변수, 비동기

    @GET("kiosks")
    Call<JsonObject> getKioskGPS(@QueryMap Map<String, String> location);

    @GET("kiosks")
    Call<JsonObject> getKioskBeacon(@Query("ble") List<String> ble);
}
