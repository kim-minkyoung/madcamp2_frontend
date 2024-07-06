package com.example.madcamp2_frontend.view.activity

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
    private lateinit var doodleClassifier: DoodleClassifier

    private val LOG_TAG = ResultActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the classifier
        doodleClassifier = DoodleClassifier(this)

        // Get data from intent
        val bitmapFileUri = intent.getParcelableExtra<Uri>("bitmapFileUri")
        val inputStream = contentResolver.openInputStream(bitmapFileUri!!)
        val drawingBitmap = BitmapFactory.decodeStream(inputStream)

        // Perform prediction
        val result = doodleClassifier.classify(drawingBitmap)
        renderResult(result)

        // Display the drawing
        binding.drawingImageView.setImageBitmap(drawingBitmap)

        // Set up button click listeners
        binding.backToMainButton.setOnClickListener {
            finish()
        }

        binding.checkRankingButton.setOnClickListener {
            startActivity(Intent(this, RankingActivity::class.java))
        }
    }

    private fun renderResult(result: ClassifyResult) {
        val top4Labels = result.label?.split(',') ?: listOf("", "", "", "")
        if (top4Labels.size >= 4) {
            binding.bestPredictionTextView.text = "Best Prediction: ${top4Labels[0]}"
            binding.bestPredictionPercentageTextView.text = "Matching Percentage: ${result.probabilities[result.top4[0]] * 100}%"
            binding.secondPredictionTextView.text = "2nd Prediction: ${top4Labels[1]} (${result.probabilities[result.top4[1]] * 100}%)"
            binding.thirdPredictionTextView.text = "3rd Prediction: ${top4Labels[2]} (${result.probabilities[result.top4[2]] * 100}%)"
            binding.fourthPredictionTextView.text = "4th Prediction: ${top4Labels[3]} (${result.probabilities[result.top4[3]] * 100}%)"
        } else {
            binding.bestPredictionTextView.text = "Best Prediction: N/A"
            binding.bestPredictionPercentageTextView.text = "Matching Percentage: N/A"
            binding.secondPredictionTextView.text = "2nd Prediction: N/A"
            binding.thirdPredictionTextView.text = "3rd Prediction: N/A"
            binding.fourthPredictionTextView.text = "4th Prediction: N/A"
        }
        binding.scoreTextView.text = "Time Cost: ${result.timeCost}ms"
    }

}
