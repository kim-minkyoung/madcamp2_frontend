package com.example.madcamp2_frontend.model.network

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/user/checkEmail")
    fun postUserEmail(@Body userInfo: UserInfo): Call<ResponseBody>
}

data class UserInfo(
    @SerializedName("email") val email: String,
    @SerializedName("nickname") val nickname: String? = "No name",
    @SerializedName("profileImage") val profileImage: String? = null
)

