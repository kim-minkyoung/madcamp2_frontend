package com.example.madcamp2_frontend.viewmodel

import androidx.lifecycle.*
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserRanking
import com.example.madcamp2_frontend.model.repository.UserRepository
import kotlinx.coroutines.launch

class RankingViewModel(private val userRepository: UserRepository) : ViewModel() {

    val userRankings: LiveData<List<UserRanking>> = userRepository.userRankings

    fun fetchUserRankings() {
        viewModelScope.launch {
            userRepository.fetchUserRankings()
        }
    }
}

class RankingViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RankingViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
