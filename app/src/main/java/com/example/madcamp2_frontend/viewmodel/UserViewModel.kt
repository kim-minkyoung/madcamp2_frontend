package com.example.madcamp2_frontend.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserEmailRequest
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.model.repository.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.annotations.SerializedName
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

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

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
        val request = UserEmailRequest(email, nickname)

        viewModelScope.launch(Dispatchers.IO) {
            apiService.postUserEmail(request).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.d("postUserEmail", "User email posted successfully")
                        response.body()?.string()?.let {
                            val jsonObject = JSONObject(it)
                            val userid = jsonObject.optString("userid", "")
                            userRepository.setUserInfo(UserInfo(userid, email, nickname, account.photoUrl?.toString() ?: ""))
                        }
                    } else {
                        Log.e("UserViewModel", "Failed to check email, response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("postUserEmail", "Failed to post user email, error: ${t.message}")
                    _error.postValue("Failed to post user email, error: ${t.message}")
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

data class UserIdResponse(
    @SerializedName("userId") val userId: String
)
