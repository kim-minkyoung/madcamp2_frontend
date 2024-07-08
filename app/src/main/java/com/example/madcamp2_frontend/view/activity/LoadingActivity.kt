package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.view.utils.GeminiApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream

class LoadingActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var bitmapFileUriString: String
    private lateinit var drawingBitmap: Bitmap
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private val geminiApi = GeminiApi()
    private var remainingMilliSeconds: Long = 0

    private var userInfo: UserInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        userInfo = intent.getParcelableExtra<UserInfo>("userInfo")

        progressBar = findViewById(R.id.loadingProgressBar)

        // Get data from intent
        bitmapFileUriString = intent.getStringExtra("bitmapFileUri") ?: ""
        remainingMilliSeconds = intent.getLongExtra("remainingMilliSeconds", 0)
        val bitmapFileUri = Uri.parse(bitmapFileUriString)
        val inputStream: InputStream? = contentResolver.openInputStream(bitmapFileUri)
        drawingBitmap = BitmapFactory.decodeStream(inputStream)

        processImage()
    }

    // Clean up response by removing line breaks and trimming spaces
    private fun cleanUpResponse(response: String): String {
        return response.replace("\n", "").replace("\r", "").replace("\\s+".toRegex(), " ")
    }

    // Enhanced processImage function
    private fun processImage() {
        coroutineScope.launch {
            val predictions = mutableListOf<Pair<String, Float>>()
            val wordList = getWordListFromJson()
            try {
                var responseText: String? = null
                var jsonResponse: JSONArray? = null
                while (predictions.size < 4) {
                    try {
                        val response = geminiApi.generateContent(
                            "Which of the 345 objects of 'quick, draw' dataset does this image look like? Give exactly four responses in the format of a JSON array, e.g., [\"사과(apple):0.9\", \"배(pear):0.8\"]. Word list: $wordList", drawingBitmap)
                            .firstOrNull()

                        responseText = response?.text
                    } catch (e: Exception) {
                        Log.e("processImage", "Error during content generation: ${e.message}")
                        null
                    }

                    if (responseText != null) {
                        Log.d("LoadingActivity", responseText)
                        responseText = cleanUpResponse(responseText)
                        jsonResponse = parseAndValidateJson(responseText)
                        if (jsonResponse != null) {
                            predictions.clear()
                            predictions.addAll(parsePredictionsFromJson(jsonResponse))
                        }
                    }
                }

                val sortedPredictions = predictions.sortedByDescending { it.second }
                navigateToResultActivity(sortedPredictions)
            } catch (e: Exception) {
                Log.e("processImage", "Error during image processing: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@LoadingActivity, "Failed to process image. Please try again.", Toast.LENGTH_SHORT).show()
                }
                navigateToBeforeStartActivity()
            }
        }
    }

    // Parse predictions from JSON array
    private fun parsePredictionsFromJson(jsonArray: JSONArray): List<Pair<String, Float>> {
        val predictions = mutableListOf<Pair<String, Float>>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.optJSONObject(i)
            if (jsonObject != null) {
                val entry = jsonObject.keys().next()
                val confidence = jsonObject.optDouble(entry, 0.0).toFloat()
                predictions.add(Pair(entry, confidence))
            }
        }
        return predictions
    }

    // Validate and parse JSON response
    private fun parseAndValidateJson(response: String): JSONArray? {
        return try {
            JSONArray(response)
        } catch (e: JSONException) {
            Log.e("parseAndValidateJson", "Error parsing JSON: ${e.message}")
            null
        }
    }

    // Navigate to BeforeStartActivity
    private fun navigateToBeforeStartActivity() {
        val intent = Intent(this, BeforeStartActivity::class.java)
        intent.putExtra("userInfo", userInfo)
        startActivity(intent)
        finish()
    }

    private fun getWordListFromJson(): String {
        val inputStream = assets.open("word_list.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val wordsArray = jsonObject.getJSONArray("words")
        val wordsList = mutableListOf<String>()
        for (i in 0 until wordsArray.length()) {
            wordsList.add(wordsArray.getString(i))
        }
        return wordsList.joinToString(", ")
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

        val score = calculateScoreIfMatches(listOf(bestPrediction, secondPrediction, thirdPrediction, fourthPrediction))

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("userInfo", userInfo)
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

    private fun calculateScoreIfMatches(predictions: List<Pair<String, Float>>): Int {
        val targetWord = intent.getStringExtra("target_word")?.trim() ?: ""
        val match = predictions.find { it.first.trim().contains(targetWord.split("(")[0].trim()) }
        return if (match != null) {
            val matchingConfidence = match.second
            (100 * matchingConfidence * (1 - 0.1f * (5000 - remainingMilliSeconds) / 5000)).toInt()
        } else {
            0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
