package com.example.madcamp2_frontend.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.model.repository.UserRepository

class RankingViewModel : ViewModel() {
    private val apiService = ApiService.create()
    private val userRepository = UserRepository(apiService)

    val userTotalRankings: LiveData<List<UserInfo>> get() = userRepository.userTotalRankings
    val userRecentRankings: LiveData<List<UserInfo>> get() = userRepository.userRecentRankings

    init {
        fetchUserTotalRankings()
        fetchUserRecentRankings()
    }

    fun fetchUserTotalRankings() {
        Log.d("RankingViewModel", "fetchUserTotalRankings: Fetching total rankings")
        userRepository.fetchUserTotalRankings()
    }

    fun fetchUserRecentRankings() {
        Log.d("RankingViewModel", "fetchUserRecentRankings: Fetching recent rankings")
        userRepository.fetchUserRecentRankings()
    }
}
