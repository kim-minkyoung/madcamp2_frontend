package com.example.madcamp2_frontend.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madcamp2_frontend.databinding.ActivityRankingBinding
import com.example.madcamp2_frontend.view.adapter.RankingAdapter

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dummy data for ranking
        val rankings = listOf("사용자1: 100점", "사용자2: 90점", "사용자3: 80점")

        // Set up RecyclerView with RankingAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = RankingAdapter(this, rankings)
    }
}
