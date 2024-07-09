package com.example.madcamp2_frontend.view.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.madcamp2_frontend.view.fragment.RecentRankingFragment
import com.example.madcamp2_frontend.view.fragment.TotalRankingFragment

class RankingPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            TotalRankingFragment()
        } else {
            RecentRankingFragment()
        }
    }
}
