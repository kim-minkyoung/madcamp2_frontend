package com.example.madcamp2_frontend.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.model.network.UserEmailResponse
import com.example.madcamp2_frontend.model.network.ScoreRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {

    private val _userInfo = MutableLiveData<UserInfo?>()
    val userInfo: LiveData<UserInfo?> get() = _userInfo

    private val _userRecentRankings = MutableLiveData<List<UserInfo>>()
    val userRecentRankings: LiveData<List<UserInfo>> get() = _userRecentRankings

    private val _userTotalRankings = MutableLiveData<List<UserInfo>>()
    val userTotalRankings: LiveData<List<UserInfo>> get() = _userTotalRankings

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
        apiService.updateUserInfo(userInfo.userid, userInfo)
            .enqueue(object : Callback<UserInfo> {
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    if (response.isSuccessful) {
                        _userInfo.postValue(userInfo)
                    } else {
                        Log.e("UserRepository", "Failed to update user info: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    Log.e("UserRepository", "Failed to update user info: ${t.message}")
                }
            })
    }

    fun updateScoreInfo(userId: String, score: Int, playCount: Int) {
        apiService.updateUserScore(userId, ScoreRequest(score, playCount))
            .enqueue(object : Callback<UserInfo> {
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    if (response.isSuccessful) {
                        _userInfo.postValue(response.body())
                    } else {
                        Log.e("UserRepository", "Failed to update user score: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    Log.e("UserRepository", "Failed to update user score: ${t.message}")
                }
            })
    }

    fun deleteUser(userId: String) {
        apiService.deleteUser(userId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    _userInfo.postValue(null)
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
        updateScoreInfo(userInfo.userid, 0, 0)
    }

    fun fetchUserTotalRankings() {
        apiService.getTotalScores().enqueue(object : Callback<List<UserInfo>> {
            override fun onResponse(call: Call<List<UserInfo>>, response: Response<List<UserInfo>>) {
                if (response.isSuccessful) {
                    _userTotalRankings.postValue(response.body())
                } else {
                    Log.e("UserRepository", "Failed to fetch user rankings: ${response.code()}")
                    _userTotalRankings.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<List<UserInfo>>, t: Throwable) {
                Log.e("UserRepository", "Failed to fetch user rankings", t)
                _userTotalRankings.postValue(emptyList())
            }
        })
    }

    fun fetchUserRecentRankings() {
        apiService.getScores().enqueue(object : Callback<List<UserInfo>> {
            override fun onResponse(call: Call<List<UserInfo>>, response: Response<List<UserInfo>>) {
                if (response.isSuccessful) {
                    _userRecentRankings.postValue(response.body())
                } else {
                    Log.e("UserRepository", "Failed to fetch all user scores: ${response.code()}")
                    _userRecentRankings.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<List<UserInfo>>, t: Throwable) {
                Log.e("UserRepository", "Failed to fetch all user scores", t)
                _userRecentRankings.postValue(emptyList())
            }
        })
    }
}
