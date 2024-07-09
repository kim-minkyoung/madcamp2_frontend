package com.example.madcamp2_frontend.view.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivityRankingBinding
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.model.network.UserRanking
import com.example.madcamp2_frontend.model.repository.UserRepository
import com.example.madcamp2_frontend.view.adapter.RankingAdapter
import com.example.madcamp2_frontend.viewmodel.RankingViewModel
import com.example.madcamp2_frontend.viewmodel.RankingViewModelFactory
import com.example.madcamp2_frontend.viewmodel.UserViewModel

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding
    private val rankingViewModel: RankingViewModel by viewModels {
        val apiService = ApiService.create()
        val userRepository = UserRepository(apiService)
        RankingViewModelFactory(userRepository)
    }
    private lateinit var adapter: RankingAdapter
    private val userViewModel: UserViewModel by viewModels()

    private var userInfo: UserInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userid = intent.getStringExtra("userid")
        if (userid != null) {
            userViewModel.getUserInfo(userid)
            userViewModel.userInfo.observe(this, Observer { fetchedUserInfo ->
                if (fetchedUserInfo != null) {
                    userInfo = fetchedUserInfo
                }
            })
        }

        setupRecyclerView()
        observeViewModel()

        rankingViewModel.fetchUserRankings()

        Glide.with(this)
            .load(R.raw.drawdle)
            .into(binding.drawdleLogo)
    }

    private fun setupRecyclerView() {
        adapter = RankingAdapter()
        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rankingRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        rankingViewModel.userRankings.observe(this, Observer { rankings ->
            rankings?.let {
                adapter.submitList(it)
                updateMyRanking(it)
            }
        })
    }

    private fun updateMyRanking(rankings: List<UserRanking>) {
        val myRanking = rankings.find { it.email == userInfo?.email }
        myRanking?.let {
            binding.myRankPosition.text = (rankings.indexOf(it) + 1).toString()
            binding.myRankName.text = it.nickname
            binding.myRankScore.text = it.score.toString()
            // Load profile image if available
            Glide.with(this)
                .load(userInfo?.profileImage ?: R.drawable.default_profile_light)
                .into(binding.myRankAvatar)
        }
    }
}
