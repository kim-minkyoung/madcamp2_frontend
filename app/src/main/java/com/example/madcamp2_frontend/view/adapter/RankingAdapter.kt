package com.example.madcamp2_frontend.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp2_frontend.R

class RankingAdapter(
    private val context: Context,
    private val rankings: List<String>
) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankingTextView: TextView = view.findViewById(R.id.rankingItemTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_ranking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rankingTextView.text = rankings[position]
    }

    override fun getItemCount(): Int {
        return rankings.size
    }
}
