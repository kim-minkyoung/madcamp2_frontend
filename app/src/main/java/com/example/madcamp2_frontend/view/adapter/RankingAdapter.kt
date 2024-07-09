package com.example.madcamp2_frontend.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp2_frontend.databinding.ItemRankingBinding
import com.example.madcamp2_frontend.model.network.UserInfo

class RankingAdapter : ListAdapter<UserInfo, RankingAdapter.RankingViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = ItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class RankingViewHolder(private val binding: ItemRankingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userInfo: UserInfo, position: Int) {
            binding.rankingPositionTextView.text = (position+1).toString()
            binding.rankingNicknameTextView.text = userInfo.nickname
            binding.rankingScoreTextView.text = userInfo.score.toString()

            val resourceId = binding.root.context.resources.getIdentifier(userInfo.profileImage, "drawable", binding.root.context.packageName)
            if (resourceId != 0) {
                binding.rankingAvatarImageView.setImageResource(resourceId)
            } else {
                binding.rankingAvatarImageView.setImageResource(com.example.madcamp2_frontend.R.drawable.default_profile_light)
            }
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
