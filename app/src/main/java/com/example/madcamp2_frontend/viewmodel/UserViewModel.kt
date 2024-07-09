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

    fun deleteProfileImage(userId: String) {
        viewModelScope.launch {
            userRepository.deleteProfileImage(userId)
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
                            val isExistingUser = jsonObject.optBoolean("isExistingUser", false)
                            val userObject = jsonObject.optJSONObject("user")
                            if (userObject != null) {
                                val userid = userObject.optString("_id", "")
                                val userNickname = userObject.optString("nickname", nickname)
                                val userEmail = userObject.optString("email", email)
                                Log.d("postUserEmail", "User ID: $userid")
                                userRepository.setUserInfo(UserInfo(userid, userEmail, userNickname, 0, 0, 0,"monkey"))
                            } else {
                                Log.e("postUserEmail", "User object is null")
                            }
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

}