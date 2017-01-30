package com.example.samsung.woonebo_android.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by SAMSUNG on 2016-12-13.
 */
public interface TargetingService {
    @FormUrlEncoded
    @POST("products/{id}/star")
    Call<ResponseBody> addInterest(@Path("id") int kioskProductID, @Field("idToken") String idToken);
}
