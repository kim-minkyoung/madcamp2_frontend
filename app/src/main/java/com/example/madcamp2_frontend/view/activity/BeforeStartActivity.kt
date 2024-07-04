package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.madcamp2_frontend.databinding.ActivityBeforeStartBinding

class BeforeStartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeforeStartBinding
    private lateinit var randomWord: String // 랜덤 단어를 저장할 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeforeStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 랜덤 단어 생성
        randomWord = generateRandomWord()

        // TextView에 랜덤 단어 설정
        binding.randomWordTextView.text = randomWord

        // 시작하기 버튼 클릭 리스너 설정
        binding.startButton.setOnClickListener {
            // DrawingActivity로 이동하면서 랜덤 단어 전달
            val intent = Intent(this, DrawingActivity::class.java)
            intent.putExtra("random_word", randomWord)
            startActivity(intent)
            finish()
        }
    }

    // 랜덤 단어를 생성하는 함수 (예시)
    private fun generateRandomWord(): String {
        // 여기서 실제로 랜덤 단어를 생성하는 로직을 구현하면 됩니다.
        // 예시로 고정된 단어 "Apple"을 반환하도록 구현합니다.
        return "Apple"
    }
}
