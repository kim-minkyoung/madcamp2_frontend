package com.example.madcamp2_frontend.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserEmailRequest
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.model.network.UserEmailResponse
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

    fun updateUserScore(userId: String, score: Int, playCount: Int) {
        viewModelScope.launch {
            userRepository.updateScoreInfo(userId, score, playCount)
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
            apiService.postUserEmail(request).enqueue(object : Callback<UserEmailResponse> {
                override fun onResponse(call: Call<UserEmailResponse>, response: Response<UserEmailResponse>) {
                    if (response.isSuccessful) {
                        Log.d("postUserEmail", "User email posted successfully")
                        val userEmailResponse = response.body()
                        if (userEmailResponse != null) {
                            val isExistingUser = userEmailResponse.isExistingUser
                            val user = userEmailResponse.user
                            Log.d("postUserEmail", "User ID: ${user.userid}")
                            userRepository.setUserInfo(user)
                        } else {
                            Log.e("postUserEmail", "User object is null")
                        }
                    } else {
                        Log.e("UserViewModel", "Failed to check email, response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UserEmailResponse>, t: Throwable) {
                    Log.e("postUserEmail", "Failed to post user email, error: ${t.message}")
                    _error.postValue("Failed to post user email, error: ${t.message}")
                }
            })
        }
    }
}
