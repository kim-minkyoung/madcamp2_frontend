package com.example.madcamp2_frontend.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp2_frontend.view.utils.DoodleClassifier
import com.example.madcamp2_frontend.databinding.ActivityResultBinding
import com.example.madcamp2_frontend.view.utils.ClassifyResult

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        val bitmapFileUriString = intent.getStringExtra("bitmapFileUri")
        val bitmapFileUri = Uri.parse(bitmapFileUriString)
        val inputStream = contentResolver.openInputStream(bitmapFileUri!!)
        val drawingBitmap = BitmapFactory.decodeStream(inputStream)

        // Display the drawing
        binding.drawingImageView.setImageBitmap(drawingBitmap)

        // Retrieve predictions from intent
        val bestPrediction = intent.getStringExtra("bestPrediction")
        val bestPredictionPercentage = intent.getFloatExtra("bestPredictionPercentage", 0f)
        val secondPrediction = intent.getStringExtra("secondPrediction")
        val secondPredictionPercentage = intent.getFloatExtra("secondPredictionPercentage", 0f)
        val thirdPrediction = intent.getStringExtra("thirdPrediction")
        val thirdPredictionPercentage = intent.getFloatExtra("thirdPredictionPercentage", 0f)
        val fourthPrediction = intent.getStringExtra("fourthPrediction")
        val fourthPredictionPercentage = intent.getFloatExtra("fourthPredictionPercentage", 0f)
        val score = intent.getFloatExtra("score", 0f)

        // Bind predictions to text views
        binding.bestPredictionTextView.text = "이 그림은 아마도 $bestPrediction"
        binding.bestPredictionPercentageTextView.text = String.format("%.1f%%만큼 일치해요!", bestPredictionPercentage * 100)
        binding.secondPredictionTextView.text = String.format("2등은 %s (%.1f%%)", secondPrediction, secondPredictionPercentage * 100)
        binding.thirdPredictionTextView.text = String.format("3등은 %s (%.1f%%)", thirdPrediction, thirdPredictionPercentage * 100)
        binding.fourthPredictionTextView.text = String.format("4등은 %s (%.1f%%)", fourthPrediction, fourthPredictionPercentage * 100)
        binding.scoreTextView.text = String.format("점수: %.1f", score)

        // Set up button click listeners
        binding.backToMainButton.setOnClickListener {
            finish()
        }

        binding.checkRankingButton.setOnClickListener {
            startActivity(Intent(this, RankingActivity::class.java))
        }
    }
}
