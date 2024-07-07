package com.example.madcamp2_frontend.model.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/users/checkEmail")
    fun postUserEmail(@Body userInfo: UserInfo): Call<ResponseBody>

    @PUT("/users/{userid}")
    fun updateUserInfo(@Path("userid") userid: String, @Body userInfo: UserInfo): Call<ResponseBody>

    @DELETE("/users/{userid}/deleteProfileImage")
    fun deleteUserProfileImage(@Path("userid") userid: String): Call<ResponseBody>

    @GET("/users/{userid}")
    fun getUserInfo(@Path("userid") userid: String): Call<ResponseBody>

    @DELETE("/users/{userid}")
    fun deleteUser(@Path("userid") userid: String): Call<ResponseBody>
}

@Parcelize
data class UserInfo(
    @SerializedName("userid") val userid: String,
    @SerializedName("email") val email: String,
    @SerializedName("nickname") val nickname: String? = "No name",
    @SerializedName("profileImage") val profileImage: String? = null
) : Parcelable
