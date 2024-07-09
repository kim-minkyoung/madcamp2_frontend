package com.example.madcamp2_frontend.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.model.network.UserRanking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {

    private val _userInfo = MutableLiveData<UserInfo?>()
    val userInfo: LiveData<UserInfo?> get() = _userInfo

    private val _userRankings = MutableLiveData<List<UserRanking>>()
    val userRankings: LiveData<List<UserRanking>> get() = _userRankings

    fun getUserInfo(userId: String) {
        apiService.getUserInfo(userId).enqueue(object : Callback<UserInfo> {
            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                if (response.isSuccessful) {
                    _userInfo.postValue(response.body())
                    Log.d("UserRepository", "User info posted: ${response.body()}")
                } else {
                    Log.e("UserRepository", "Failed to fetch user info: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                Log.e("UserRepository", "Failed to fetch user info: ${t.message}")
            }
        })
    }

    fun updateUserInfo(userInfo: UserInfo) {
        apiService.updateUserInfo(userInfo.userid, userInfo).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    _userInfo.postValue(userInfo)
                } else {
                    Log.e("UserRepository", "Failed to update user info: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("UserRepository", "Failed to update user info: ${t.message}")
            }
        })
    }

    fun deleteProfileImage(userId: String) {
        apiService.deleteUserProfileImage(userId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    _userInfo.postValue(userInfo.value?.copy(profileImage = null) ?: return)
                } else {
                    Log.e("UserRepository", "Failed to delete user profile image: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("UserRepository", "Failed to delete user: ${t.message}")
            }
        })
    }

    fun deleteUser(userId: String) {
        apiService.deleteUser(userId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    _userInfo.postValue(null) // Clear the user info upon successful deletion
                } else {
                    Log.e("UserRepository", "Failed to delete user: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("UserRepository", "Failed to delete user: ${t.message}")
            }
        })
    }

    fun setUserInfo(userInfo: UserInfo) {
        updateUserInfo(userInfo)
    }

    fun fetchUserRankings() {
        apiService.getAllUsers().enqueue(object : Callback<List<UserRanking>> {
            override fun onResponse(call: Call<List<UserRanking>>, response: Response<List<UserRanking>>) {
                if (response.isSuccessful) {
                    _userRankings.postValue(response.body())
                } else {
                    Log.e("UserRepository", "Failed to fetch user rankings: ${response.errorBody()}")
                    _userRankings.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<List<UserRanking>>, t: Throwable) {
                Log.e("UserRepository", "Failed to fetch user rankings: ${t.message}")
            }
        })
    }
}
