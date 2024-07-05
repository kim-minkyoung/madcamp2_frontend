package com.example.madcamp2_frontend.model.repository

import android.graphics.Bitmap
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.IOException

object DrawingRepository {

    // QuickDraw API 호출
    fun callQuickDrawAPI(currentWord: String, bitmap: Bitmap, callback: (List<Pair<String, Float>>, Int) -> Unit) {
        // QuickDraw API 호출을 위한 요청 생성
        val url = "https://quickdraw.withgoogle.com/drawings/$currentWord"
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "image.jpg", RequestBody.create("image/jpeg".toMediaTypeOrNull(), bitmapToByteArray(bitmap)))
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // API 요청 실행
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
////                callback.invoke(-1f) // 실패 시 일치율 0% 반환
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//                    // API 응답을 문자열로 변환
//                    val responseString = response.body?.string()
//                    val gson = Gson()
//
//                    // JSON 파싱
//                    val result = gson.fromJson(responseString, QuickDrawResponse::class.java)
//
//                    // 일치율(정확도) 반환
//                    val matchPercentage = result.confidence * 100 // confidence는 0에서 1 사이의 값
//
//                    val predictions: List<Pair<String, Float>> = listOf(
//                        "Cat" to 95.0f,
//                        "Dog" to 85.0f,
//                        "Mouse" to 80.0f,
//                        "Elephant" to 75.0f
//                    )
//
//                    callback(predictions, 0)
//                }
//            }
//        })
        val predictions: List<Pair<String, Float>> = listOf(
            "Cat" to 95.0f,
            "Dog" to 85.0f,
            "Mouse" to 80.0f,
            "Elephant" to 75.0f
        )

        callback(predictions, 0)
    }

    // Bitmap을 ByteArray로 변환하는 함수
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    // QuickDraw API 응답 클래스
    data class QuickDrawResponse(
        val confidence: Float // 일치율(정확도)
    )
}
