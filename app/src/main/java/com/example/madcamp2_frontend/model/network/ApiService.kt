package com.example.madcamp2_frontend.model.network

import android.os.Parcelable
import com.example.madcamp2_frontend.view.utils.Constants
import com.example.madcamp2_frontend.view.utils.RetrofitInstance
import com.google.android.gms.common.api.Response
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/user/checkEmail")
    fun postUserEmail(@Body request: UserEmailRequest): Call<ResponseBody>

    @PUT("/user/{userid}")
    fun updateUserInfo(@Path("userid") userid: String, @Body userInfo: UserInfo): Call<ResponseBody>

    @DELETE("/user/{userid}/deleteProfileImage")
    fun deleteUserProfileImage(@Path("userid") userid: String): Call<ResponseBody>

    @GET("/user/{userid}")
    fun getUserInfo(@Path("userid") userid: String): Call<UserInfo>

    @DELETE("/user/{userid}")
    fun deleteUser(@Path("userid") userid: String): Call<ResponseBody>

    @GET("/user")
    fun getAllUsers(): Call<List<UserRanking>>

    @GET("/game/globalWord")
    fun getGlobalWord(): Call<ResponseBody>

    companion object {
        fun create(): ApiService {
            return RetrofitInstance.apiService
        }
    }
}

@Parcelize
data class UserInfo(
    @SerializedName("_id") val userid: String,
    @SerializedName("email") val email: String,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("score") val score: Int? = null,
    @SerializedName("totalScore") val totalScore: Int? = null,
    @SerializedName("playCount") val playCount: Int? = null,
    @SerializedName("profileImage") val profileImage: String? = "monkey"
) : Parcelable

data class UserEmailRequest(
    val email: String,
    val nickname: String? = "No name"
)

data class UserRanking(
    val _id: String,
    val email: String,
    val nickname: String,
    val score: Int
)