package com.example.madcamp2_frontend.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ItemRankingBinding

class RankingAdapter(
    private val context: Context,
    private val rankings: List<String>
) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    inner class RankingViewHolder(val binding: ItemRankingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = ItemRankingBinding.inflate(LayoutInflater.from(context), parent, false)
        return RankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val data = rankings[position].split(": ")
        holder.binding.rankingPositionTextView.text = "${position + 1}"
        holder.binding.rankingNicknameTextView.text = data[0]
        holder.binding.rankingScoreTextView.text = data[1]
        // Set avatar image if available
        holder.binding.rankingAvatarImageView.setImageResource(R.drawable.default_profile)
    }

    override fun getItemCount(): Int {
        return rankings.size // Exclude top 3 from the main list
    }
}
