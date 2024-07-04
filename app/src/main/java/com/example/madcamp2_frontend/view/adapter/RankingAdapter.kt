package com.example.madcamp2_frontend.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.madcamp2_frontend.databinding.ItemRankingBinding

class RankingAdapter(context: Context, private val rankingData: List<String>) :
    ArrayAdapter<String>(context, 0, rankingData) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemRankingBinding
        val view: View

        if (convertView == null) {
            // View Binding을 사용하여 뷰 바인딩
            binding = ItemRankingBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemRankingBinding
            view = convertView
        }

        // 데이터 바인딩
        val item = rankingData[position]
        binding.rankingItemTextView.text = item

        return view
    }
}
