package com.example.madcamp2_frontend.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.model.repository.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {

    private val apiService = ApiService.create()
    private val userRepository = UserRepository(apiService)

    val userInfo: LiveData<UserInfo?> = userRepository.userInfo

    fun getUserInfo(userId: String) {
        viewModelScope.launch {
            userRepository.getUserInfo(userId)
        }
    }

    fun updateUserInfo(userInfo: UserInfo) {
        viewModelScope.launch {
            userRepository.updateUserInfo(userInfo)
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            userRepository.deleteUser(userId)
        }
    }

    fun postUserEmail(account: GoogleSignInAccount) {
        val email = account.email ?: return
        val nickname = account.displayName?.ifEmpty { "No name" } ?: "No name"
        val profileImage = account.photoUrl?.toString() ?: ""
        val userInfo = UserInfo("", email, nickname, profileImage)

        viewModelScope.launch(Dispatchers.IO) {
            apiService.postUserEmail(userInfo).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.d("updateUserInfoOnServer", "User info updated successfully")
                    } else {
                        Log.e("updateUserInfoOnServer", "Failed to update User info, response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("updateUserNicknameOnServer", "Failed to update nickname, error: ${t.message}")
                }
            })
        }
    }

    fun updateUserNicknameOnServer(userInfo: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            apiService.updateUserInfo(userInfo.userid, userInfo).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.d("updateUserInfoOnServer", "User info updated successfully")
                    } else {
                        Log.e("updateUserInfoOnServer", "Failed to update User info, response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("updateUserNicknameOnServer", "Failed to update nickname, error: ${t.message}")
                }
            })
        }
    }
}
