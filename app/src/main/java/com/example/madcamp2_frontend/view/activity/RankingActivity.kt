package com.example.madcamp2_frontend.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivityRankingBinding
import com.example.madcamp2_frontend.databinding.ItemTop3Binding
import com.example.madcamp2_frontend.view.adapter.RankingAdapter

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dummy data for ranking
        val rankings = listOf("Svetlana: 1463", "Yunus: 1397", "Raquel: 1351", "Andrea: 1296", "Kristina: 1257", "Dayana: 1186", "Vitaly: 1103", "Marek: 1099")

        // Set up RecyclerView with RankingAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = RankingAdapter(this, rankings)

        // Set up top 3 players
        setUpTop3Players(rankings.subList(0, 3))

        // Set up my rank
        setUpMyRank("My Name: 1000") // Replace with actual user data
    }

    private fun setUpTop3Players(top3: List<String>) {
        top3.forEachIndexed { index, ranking ->
            val itemBinding = ItemTop3Binding.inflate(layoutInflater, binding.top3Container, false)
            val data = ranking.split(": ")
            itemBinding.nameTextView.text = data[0]
            itemBinding.scoreTextView.text = data[1]
            itemBinding.avatarImageView.setImageResource(R.drawable.ic_launcher_foreground) // Placeholder, replace with actual image loading logic
            binding.top3Container.addView(itemBinding.root)
        }
    }

    private fun setUpMyRank(myRank: String) {
        val data = myRank.split(": ")
        binding.myRankTextView.text = "My Rank: ${data[1]}"
        // Set my rank avatar if needed
    }
}
