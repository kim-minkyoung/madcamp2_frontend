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
import com.example.madcamp2_frontend.viewmodel.UserViewModel
import com.google.android.material.tabs.TabLayoutMediator

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding
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
                    updateMyRanking(fetchedUserInfo)
                }
            })
        }

        setupViewPager()

        Glide.with(this)
            .load(R.raw.drawdle)
            .into(binding.drawdleLogo)
    }

    private fun setupViewPager() {
        val pagerAdapter = RankingPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "일간 랭킹 보기"
                1 -> "누적 랭킹 보기"
                else -> null
            }
        }.attach()
    }

    private fun updateMyRanking(userInfo: UserInfo) {
        binding.myRankName.text = userInfo.nickname
        binding.myRankScore.text = userInfo.score.toString()
        val profileImageResId = resources.getIdentifier(userInfo.profileImage, "drawable", packageName)
        Glide.with(this)
            .load(profileImageResId)
            .into(binding.myRankAvatar)
    }
}
