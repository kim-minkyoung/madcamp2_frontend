package com.example.madcamp2_frontend.view.activity

import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.TextView
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)


        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHelpItems()
    }

    private fun setupHelpItems() {
        // 질문과 답변을 배열로 만듭니다.
        val questions = arrayOf(
            "Q. Drawdle은 무엇인가요?",
            "Q. 한 시간에 두 번 이상 플레이할 수는 없나요?"
        )

        val answers = arrayOf(
            "drawdle은 매 시간마다 업데이트되는 단어를 맞히는 게임입니다. 정확한 단어를 추측하여 당신의 그림 실력과 순발력을 테스트해보세요.\n",
            "drawdle은 매 시간마다 단어가 업데이트되지만, 각 단어 당 두 번의 기회를 줍니다.\n우리는 하루에 24 번, 모든 참여자가 동일한 정답을 맞추는 게임 경험을 제공합니다.\n정답 단어는 매 시각의 정각에 업데이트됩니다."
        )

        // 질문과 답변을 동적으로 화면에 추가합니다.
        for (i in questions.indices) {
            val question = questions[i]
            val answer = answers[i]

            val questionTextView = TextView(this)
            questionTextView.text = question
            questionTextView.textSize = 18f
            questionTextView.setTypeface(null, Typeface.BOLD) // 질문을 굵은 글자로 설정
            questionTextView.setPadding(0, 0, 0, 8) // Bottom padding

            val answerTextView = TextView(this)
            answerTextView.text = answer
            answerTextView.textSize = 16f

            binding.helpContainer.addView(questionTextView)
            binding.helpContainer.addView(answerTextView)
        }
    }
}