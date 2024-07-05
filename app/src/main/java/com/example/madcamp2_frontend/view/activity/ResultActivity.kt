package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp2_frontend.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        val bitmapFileUri = intent.getParcelableExtra<Uri>("bitmapFileUri")
        val bestPrediction = intent.getStringExtra("bestPrediction")
        val bestPredictionPercentage = intent.getFloatExtra("bestPredictionPercentage", 0f)
        val secondPrediction = intent.getStringExtra("secondPrediction")
        val secondPredictionPercentage = intent.getFloatExtra("secondPredictionPercentage", 0f)
        val thirdPrediction = intent.getStringExtra("thirdPrediction")
        val thirdPredictionPercentage = intent.getFloatExtra("thirdPredictionPercentage", 0f)
        val fourthPrediction = intent.getStringExtra("fourthPrediction")
        val fourthPredictionPercentage = intent.getFloatExtra("fourthPredictionPercentage", 0f)
        val score = intent.getIntExtra("score", 0)

        // Load bitmap from file URI
        val inputStream = contentResolver.openInputStream(bitmapFileUri!!)
        val drawingBitmap = BitmapFactory.decodeStream(inputStream)

        // Display the data
        binding.drawingImageView.setImageBitmap(drawingBitmap)
        binding.bestPredictionTextView.text = "Best Prediction: $bestPrediction"
        binding.bestPredictionPercentageTextView.text = "Matching Percentage: ${bestPredictionPercentage}%"
        binding.secondPredictionTextView.text = "2nd Prediction: $secondPrediction (${secondPredictionPercentage}%)"
        binding.thirdPredictionTextView.text = "3rd Prediction: $thirdPrediction (${thirdPredictionPercentage}%)"
        binding.fourthPredictionTextView.text = "4th Prediction: $fourthPrediction (${fourthPredictionPercentage}%)"
        binding.scoreTextView.text = "Score: $score"

        // Set up button click listeners
        binding.backToMainButton.setOnClickListener {
            finish()
        }

        binding.checkRankingButton.setOnClickListener {
            startActivity(Intent(this, RankingActivity::class.java))
        }
    }
}
