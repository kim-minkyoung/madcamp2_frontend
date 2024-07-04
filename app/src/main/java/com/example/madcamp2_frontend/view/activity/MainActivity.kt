package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.madcamp2_frontend.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // DrawingActivity 시작하는 버튼 클릭 리스너
        binding.startDrawingButton.setOnClickListener {
            startActivity(Intent(this, BeforeStartActivity::class.java))
        }
    }
}
