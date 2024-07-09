package com.example.madcamp2_frontend.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.madcamp2_frontend.view.fragment.RecentRankingFragment
import com.example.madcamp2_frontend.view.fragment.TotalRankingFragment

class RankingPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecentRankingFragment()
            1 -> TotalRankingFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
