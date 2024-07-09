package com.example.madcamp2_frontend.view.utils

import android.graphics.Bitmap
import android.util.Log
import com.example.madcamp2_frontend.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlin.io.encoding.ExperimentalEncodingApi

class GeminiApi {
    private val apiKey = BuildConfig.API_KEY

    var generationConfig: GenerationConfig = generationConfig {
        maxOutputTokens = 100
    }

    val generativeVisionModel = GenerativeModel(
        modelName = "gemini-1.5-pro-latest",
        apiKey = apiKey,
        generationConfig = generationConfig
    )

    @OptIn(ExperimentalEncodingApi::class)
    fun generateContent(prompt: String, imageData: Bitmap): Flow<String> {
        val content = content {
            image(imageData)
            text(prompt)
        }

        return flow {
            val responseTextBuilder = StringBuilder()

            generativeVisionModel.generateContentStream(content).collect { response ->
                response.text?.let { responseTextBuilder.append(it) }
            }

            val responseText = responseTextBuilder.toString()
            if (responseText.isEmpty()) {
                throw Exception("Empty response from Gemini")
            }
            Log.d("GeminiApi", responseText)

            emit(responseText)
        }.catch { e ->
            throw e
        }
    }
}
