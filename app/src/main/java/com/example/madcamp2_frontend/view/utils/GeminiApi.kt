package com.example.madcamp2_frontend.view.utils

import android.graphics.Bitmap
import com.example.madcamp2_frontend.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlin.io.encoding.ExperimentalEncodingApi

class GeminiApi {
    private val apiKey = BuildConfig.API_KEY

    val generativeVisionModel = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = apiKey
    )

    @OptIn(ExperimentalEncodingApi::class)
    fun generateContent(prompt: String, imageData: Bitmap): Flow<GenerateContentResponse> {
        val content = content {
            image(imageData)
            text(prompt)
        }
        return generativeVisionModel.generateContentStream(content)
    }
}
