package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp2_frontend.view.utils.GeminiApi
import com.example.madcamp2_frontend.R
import kotlinx.coroutines.*
import java.io.InputStream

class LoadingActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var bitmapFileUriString: String
    private lateinit var drawingBitmap: Bitmap
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private val geminiApi = GeminiApi()
    private var remainingMilliSeconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        progressBar = findViewById(R.id.loadingProgressBar)

        // Get data from intent
        bitmapFileUriString = intent.getStringExtra("bitmapFileUri") ?: ""
        remainingMilliSeconds = intent.getLongExtra("remainingMilliSeconds", 0)
        val bitmapFileUri = Uri.parse(bitmapFileUriString)
        val inputStream: InputStream? = contentResolver.openInputStream(bitmapFileUri)
        drawingBitmap = BitmapFactory.decodeStream(inputStream)

        // Start processing the image
        processImage()
    }

    private fun processImage() {
        coroutineScope.launch {
            val predictions = mutableListOf<Pair<String, Float>>()
            geminiApi.generateContent("Which of the 345 objects of 'quick, draw' dataset does this image look like? Give four responses in the format of apple\uD83C\uDF4E:0.9(confidence from 0 to 1). Even if you cannot recognize, give any predictions.", drawingBitmap)
                .collect { response ->
                    response.text?.let { text ->
                        predictions.addAll(parsePredictions(text))
                    }
                }
            val sortedPredictions = predictions.sortedByDescending { it.second }
            navigateToResultActivity(sortedPredictions)
        }
    }

    private fun parsePredictions(resultText: String): List<Pair<String, Float>> {
        val predictions = mutableListOf<Pair<String, Float>>()
        val lines = resultText.split("\n")
        for (line in lines) {
            val parts = line.split(":")
            if (parts.size == 2) {
                val label = parts[0].trim()
                val confidence = parts[1].trim().toFloatOrNull() ?: 0f
                predictions.add(Pair(label, confidence))
            }
        }
        return predictions
    }

    private fun navigateToResultActivity(predictions: List<Pair<String, Float>>) {
        if (predictions.size < 4) {
            runOnUiThread {
                Toast.makeText(this, "Insufficient predictions received.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val bestPrediction = predictions[0]
        val secondPrediction = predictions[1]
        val thirdPrediction = predictions[2]
        val fourthPrediction = predictions[3]

        val score = calculateScore(bestPrediction.second, remainingMilliSeconds)

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("bitmapFileUri", bitmapFileUriString)
            putExtra("bestPrediction", bestPrediction.first)
            putExtra("bestPredictionPercentage", bestPrediction.second)
            putExtra("secondPrediction", secondPrediction.first)
            putExtra("secondPredictionPercentage", secondPrediction.second)
            putExtra("thirdPrediction", thirdPrediction.first)
            putExtra("thirdPredictionPercentage", thirdPrediction.second)
            putExtra("fourthPrediction", fourthPrediction.first)
            putExtra("fourthPredictionPercentage", fourthPrediction.second)
            putExtra("score", score)
        }
        startActivity(intent)
        finish()
    }

    private fun calculateScore(bestPredictionConfidence: Float, remainingMilliSeconds: Long): Float {
        return 100 * bestPredictionConfidence * (1 - 0.1f * (5000 - remainingMilliSeconds) / 5000)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
