package com.example.madcamp2_frontend.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp2_frontend.databinding.ItemRankingBinding
import com.example.madcamp2_frontend.model.network.UserInfo

class RankingAdapter(private val isTotalRanking: Boolean) : ListAdapter<UserInfo, RankingAdapter.RankingViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = ItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val userInfo = getItem(position)
        holder.bind(userInfo, position + 1, isTotalRanking)
    }

    class RankingViewHolder(private val binding: ItemRankingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userInfo: UserInfo, position: Int, isTotalRanking: Boolean) {
            binding.rankingPositionTextView.text = position.toString()
            binding.rankingNicknameTextView.text = userInfo.nickname
            binding.rankingScoreTextView.text = if (isTotalRanking) userInfo.totalScore.toString() else userInfo.score.toString()
            binding.rankingAvatarImageView.setImageResource(
                binding.root.context.resources.getIdentifier(userInfo.profileImage, "drawable", binding.root.context.packageName)
            )
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<UserInfo>() {
        override fun areItemsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem.userid == newItem.userid
        }

        override fun areContentsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem == newItem
        }
    }
}
