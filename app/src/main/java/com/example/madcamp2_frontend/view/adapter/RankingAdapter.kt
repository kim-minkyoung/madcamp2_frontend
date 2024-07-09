package com.example.madcamp2_frontend.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp2_frontend.databinding.ItemRankingBinding
import com.example.madcamp2_frontend.model.network.UserInfo

class RankingAdapter : ListAdapter<UserInfo, RankingAdapter.RankingViewHolder>(RankingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = ItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val userRanking = getItem(position)
        holder.bind(userRanking, position)
    }

    class RankingViewHolder(private val binding: ItemRankingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userRanking: UserInfo, position: Int) {
            binding.rankingPositionTextView.text = (position + 1).toString()
            binding.rankingNicknameTextView.text = userRanking.nickname
            binding.rankingScoreTextView.text = userRanking.score.toString()
            // Load profile image if available
        }
    }

    class RankingDiffCallback : DiffUtil.ItemCallback<UserInfo>() {
        override fun areItemsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem.userid == newItem.userid
        }

        override fun areContentsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem == newItem
        }
    }
}
