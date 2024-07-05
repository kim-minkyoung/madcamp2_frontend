package com.example.madcamp2_frontend.model.`interface`

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("/retrofit/get")
    fun getFunc(@Query("data") data: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/retrofit/post")
    fun postFunc(@Field("data") data: String): Call<ResponseBody>

    @FormUrlEncoded
    @PUT("/retrofit/put/{id}")
    fun putFunc(@Path("id") id: String, @Field("data") data: String): Call<ResponseBody>

    @DELETE("/retrofit/delete/{id}")
    fun deleteFunc(@Path("id") id: String): Call<ResponseBody>
}
