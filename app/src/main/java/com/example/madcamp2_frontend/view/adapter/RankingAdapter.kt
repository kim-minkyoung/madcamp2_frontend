package com.example.madcamp2_frontend.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp2_frontend.databinding.ItemRankingBinding
import com.example.madcamp2_frontend.model.network.UserRanking

class RankingAdapter : ListAdapter<UserRanking, RankingAdapter.RankingViewHolder>(RankingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = ItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val userRanking = getItem(position)
        holder.bind(userRanking, position)
    }

    class RankingViewHolder(private val binding: ItemRankingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userRanking: UserRanking, position: Int) {
            binding.rankingPositionTextView.text = (position + 1).toString()
            binding.rankingNicknameTextView.text = userRanking.nickname
            binding.rankingScoreTextView.text = userRanking.score.toString()
            // Load profile image if available
        }
    }

    class RankingDiffCallback : DiffUtil.ItemCallback<UserRanking>() {
        override fun areItemsTheSame(oldItem: UserRanking, newItem: UserRanking): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: UserRanking, newItem: UserRanking): Boolean {
            return oldItem == newItem
        }
    }
}
