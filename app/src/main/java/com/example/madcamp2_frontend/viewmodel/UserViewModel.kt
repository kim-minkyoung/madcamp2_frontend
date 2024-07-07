package com.example.madcamp2_frontend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.madcamp2_frontend.model.network.UserInfo

class UserViewModel : ViewModel() {

    private val _userInfo = MutableLiveData<UserInfo>()
    val userInfo: LiveData<UserInfo> get() = _userInfo

    fun setUserInfo(userInfo: UserInfo) {
        _userInfo.value = userInfo
    }
}
