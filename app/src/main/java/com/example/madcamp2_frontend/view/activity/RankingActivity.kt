package com.example.madcamp2_frontend.view.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivityRankingBinding
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.view.adapter.RankingPagerAdapter
import com.example.madcamp2_frontend.viewmodel.RankingViewModel
import com.example.madcamp2_frontend.viewmodel.UserViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding
    private val userViewModel: UserViewModel by viewModels()
    private val rankingViewModel: RankingViewModel by viewModels()

    private var userInfo: UserInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("RankingActivity", "onCreate: Starting activity")

        val userid = intent.getStringExtra("userid")
        if (userid != null) {
            Log.d("RankingActivity", "onCreate: Fetching user info for userid: $userid")
            userViewModel.getUserInfo(userid)
            userViewModel.userInfo.observe(this, Observer { fetchedUserInfo ->
                if (fetchedUserInfo != null) {
                    Log.d("RankingActivity", "onCreate: User info fetched: $fetchedUserInfo")
                    userInfo = fetchedUserInfo
                    updateMyRankingUI(binding.tabLayout.selectedTabPosition)
                } else {
                    Log.d("RankingActivity", "onCreate: User info is null")
                }
            })
        }

        setupViewPagerAndTabs()

        rankingViewModel.userTotalRankings.observe(this, Observer { totalRankings ->
            Log.d("RankingActivity", "onCreate: Received user total rankings: $totalRankings")
            updateMyRankingUI(binding.tabLayout.selectedTabPosition)
        })

        rankingViewModel.userRecentRankings.observe(this, Observer { recentRankings ->
            Log.d("RankingActivity", "onCreate: Received user recent rankings: $recentRankings")
            updateMyRankingUI(binding.tabLayout.selectedTabPosition)
        })
    }

    private fun setupViewPagerAndTabs() {
        val rankingPagerAdapter = RankingPagerAdapter(this)
        binding.viewPager.adapter = rankingPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "누적 랭킹" else "최근 랭킹"
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d("RankingActivity", "onTabSelected: Selected tab position: ${tab.position}")
                updateMyRankingUI(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {
                updateMyRankingUI(tab.position)
            }
        })
    }

    private fun updateMyRankingUI(selectedTabPosition: Int) {
        Log.d("RankingActivity", "updateMyRankingUI: Updating UI for tab position: $selectedTabPosition")

        if (userInfo == null) {
            Log.d("RankingActivity", "updateMyRankingUI: userInfo is null")
            return
        }

        val rankingList = if (selectedTabPosition == 0) {
            rankingViewModel.userTotalRankings.value
        } else {
            rankingViewModel.userRecentRankings.value
        }

        rankingList?.let { rankings ->
            val myRanking = rankings.find { it.email == userInfo?.email }
            myRanking?.let {
                binding.myRankPosition.text = (rankings.indexOf(it) + 1).toString()
                binding.myRankName.text = it.nickname
                binding.myRankScore.text = if (selectedTabPosition == 0) it.totalScore.toString() else it.score.toString()
                Log.d("RankingActivity", "updateMyRankingUI: My ranking updated: $it")
                // Load profile image if available
                Glide.with(this)
                    .load(resources.getIdentifier(it.profileImage, "drawable", packageName))
                    .into(binding.myRankAvatar)
            } ?: Log.d("RankingActivity", "updateMyRankingUI: My ranking not found")
        } ?: Log.d("RankingActivity", "updateMyRankingUI: Ranking list is null")
    }
}
