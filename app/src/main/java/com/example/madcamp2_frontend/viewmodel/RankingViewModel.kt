package com.example.madcamp2_frontend.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.model.repository.UserRepository
import kotlinx.coroutines.launch

class RankingViewModel : ViewModel() {
    private val apiService = ApiService.create()
    private val userRepository = UserRepository(apiService)

    private val _userRecentRankings = MutableLiveData<List<UserInfo>>()
    val userRecentRankings: LiveData<List<UserInfo>> get() = _userRecentRankings

    private val _userTotalRankings = MutableLiveData<List<UserInfo>>()
    val userTotalRankings: LiveData<List<UserInfo>> get() = _userTotalRankings

    fun fetchUserRecentRankings() {
        viewModelScope.launch {
            try {
                userRepository.fetchUserRecentRankings()
                userRepository.userRankings.observeForever { rankings ->
                    if (rankings != null) {
                        Log.d("RankingViewModel", "Fetched user recent rankings: $rankings")
                        _userRecentRankings.postValue(rankings)
                    } else {
                        Log.e("RankingViewModel", "User recent rankings are null")
                    }
                }
            } catch (e: Exception) {
                Log.e("RankingViewModel", "Error fetching user recent rankings", e)
            }
        }
    }

    fun fetchUserTotalRankings() {
        viewModelScope.launch {
            try {
                userRepository.fetchUserTotalRankings()
                userRepository.userTotalRankings.observeForever { totalRankings ->
                    if (totalRankings != null) {
                        Log.d("RankingViewModel", "Fetched user total rankings: $totalRankings")
                        _userTotalRankings.postValue(totalRankings)
                    } else {
                        Log.e("RankingViewModel", "User total rankings are null")
                    }
                }
            } catch (e: Exception) {
                Log.e("RankingViewModel", "Error fetching user total rankings", e)
            }
        }
    }
}
