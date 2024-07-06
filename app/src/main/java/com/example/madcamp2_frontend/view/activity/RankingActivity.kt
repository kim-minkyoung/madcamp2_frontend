package com.example.madcamp2_frontend.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivityRankingBinding
import com.example.madcamp2_frontend.view.adapter.RankingAdapter

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load(R.raw.drawdle)
            .into(binding.drawdleLogo);

        // Dummy data for ranking
        val rankings = listOf("Svetlana: 1463", "Yunus: 1397", "Raquel: 1351", "Andrea: 1296", "Kristina: 1257", "Dayana: 1186", "Vitaly: 1103", "Marek: 1099")

        // Set up RecyclerView with RankingAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = RankingAdapter(this, rankings)

        // Set up my rank
        setUpMyRank("My Name: 1000") // Replace with actual user data
    }

    private fun setUpMyRank(myRank: String) {
        val data = myRank.split(": ")
        binding.myRankName.text = "${data[0]}"
        binding.myRankScore.text = "${data[1]}"
        // Set my rank avatar if needed
        binding.myRankAvatar.setImageResource(com.example.madcamp2_frontend.R.drawable.default_profile)
    }
}
