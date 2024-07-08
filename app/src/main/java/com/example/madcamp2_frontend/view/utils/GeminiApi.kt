package com.example.madcamp2_frontend.view.utils

import android.graphics.Bitmap
import com.example.madcamp2_frontend.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.io.encoding.ExperimentalEncodingApi
import org.json.JSONArray
import org.json.JSONObject

class GeminiApi {
    private val apiKey = BuildConfig.API_KEY

    var generationConfig: GenerationConfig = generationConfig {
        maxOutputTokens = 200
    }

    val generativeVisionModel = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = apiKey,
        generationConfig = generationConfig
    )

    @OptIn(ExperimentalEncodingApi::class)
    fun generateContent(prompt: String, imageData: Bitmap): Flow<GenerateContentResponse> {
        val content = content {
            image(imageData)
            text(prompt)
        }

        return flow {
            val response = generativeVisionModel.generateContentStream(content).firstOrNull()
                ?: throw Exception("Empty response from Gemini")

            val responseText = response.text ?: throw Exception("No text in response from Gemini")
            val jsonResponse = try {
                JSONArray(responseText)
            } catch (e: Exception) {
                throw Exception("Invalid JSON response: ${e.message}")
            }

            emit(response)
        }.catch { e ->
            throw e
        }
    }
}
