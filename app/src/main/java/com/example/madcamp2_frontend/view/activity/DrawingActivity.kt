package com.example.madcamp2_frontend.view.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.madcamp2_frontend.databinding.ActivityDrawingBinding
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import com.example.madcamp2_frontend.model.repository.DrawingRepository

class DrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingBinding
    private val repository = DrawingRepository

    private var currentWord: String = "" // 현재 단어를 저장할 변수
    private var drawingBitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var paint: Paint = Paint()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트에서 랜덤 단어 받기
        currentWord = intent.getStringExtra("random_word") ?: ""

        // 단어 TextView에 설정
        binding.wordTextView.text = currentWord

        // 초기 설정
        setupCanvas()

        // 그리기 완료 후 일치율 확인
        binding.drawingEndButton.setOnClickListener {
            checkMatchPercentage()
        }
    }

    // Canvas 설정 메서드
    @SuppressLint("ClickableViewAccessibility")
    private fun setupCanvas() {
        // Canvas 초기화
        drawingBitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
        binding.drawingView.setImageBitmap(drawingBitmap)

        canvas = Canvas(drawingBitmap!!)
        canvas?.drawColor(Color.WHITE)

        binding.drawingView.setOnTouchListener { _, event ->
            startDrawing(event)
            true
        }
    }

    // 그림 그리기 이벤트 처리
    private fun startDrawing(event: android.view.MotionEvent) {
        val x = event.x
        val y = event.y

        when (event.action) {
            android.view.MotionEvent.ACTION_DOWN -> {
                canvas?.drawCircle(x, y, 10f, paint)
            }
            android.view.MotionEvent.ACTION_MOVE -> {
                canvas?.drawCircle(x, y, 10f, paint)
            }
        }

        binding.drawingView.invalidate()
    }

    // 그리기 완료 후 일치율 확인
    private fun checkMatchPercentage() {
        repository.callQuickDrawAPI(currentWord, drawingBitmap ?: return) { matchPercentage ->
            runOnUiThread {
                Toast.makeText(applicationContext, "일치율: ${matchPercentage}%", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 터치 이벤트 처리
    override fun onTouchEvent(event: android.view.MotionEvent?): Boolean {
        if (event != null) {
            startDrawing(event)
        }
        return true
    }
}
