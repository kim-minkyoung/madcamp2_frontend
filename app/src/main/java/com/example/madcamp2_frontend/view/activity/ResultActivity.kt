package com.example.madcamp2_frontend.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp2_frontend.databinding.ActivityResultBinding
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.viewmodel.UserViewModel

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val userViewModel: UserViewModel by viewModels()

    private var userInfo: UserInfo? = null

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userInfo = when {
            SDK_INT >= 33 -> intent.getParcelableExtra("userInfo", UserInfo::class.java)
            else -> intent.getParcelableExtra<UserInfo>("userInfo")
        }

        // Get data from intent
        val bitmapFileUriString = intent.getStringExtra("bitmapFileUri")
        val bitmapFileUri = Uri.parse(bitmapFileUriString)
        val inputStream = contentResolver.openInputStream(bitmapFileUri!!)
        val drawingBitmap = BitmapFactory.decodeStream(inputStream)

        // Display the drawing
        binding.drawingImageView.setImageBitmap(drawingBitmap)

        // Retrieve predictions from intent
        val bestPrediction = intent.getStringExtra("bestPrediction")
        val bestPredictionPercentage = intent.getFloatExtra("bestPredictionPercentage", 0f)
        val secondPrediction = intent.getStringExtra("secondPrediction")
        val secondPredictionPercentage = intent.getFloatExtra("secondPredictionPercentage", 0f)
        val thirdPrediction = intent.getStringExtra("thirdPrediction")
        val thirdPredictionPercentage = intent.getFloatExtra("thirdPredictionPercentage", 0f)
        val fourthPrediction = intent.getStringExtra("fourthPrediction")
        val fourthPredictionPercentage = intent.getFloatExtra("fourthPredictionPercentage", 0f)
        val score = intent.getIntExtra("score", 0)

        // Bind predictions to text views
        binding.bestPredictionTextView.text = "이 그림은 아마도 $bestPrediction"
        binding.bestPredictionPercentageTextView.text = String.format("%.1f%%만큼 일치해요!", bestPredictionPercentage * 100)
        binding.secondPredictionTextView.text = String.format("2등은 %s (%.1f%%)", secondPrediction, secondPredictionPercentage * 100)
        binding.thirdPredictionTextView.text = String.format("3등은 %s (%.1f%%)", thirdPrediction, thirdPredictionPercentage * 100)
        binding.fourthPredictionTextView.text = String.format("4등은 %s (%.1f%%)", fourthPrediction, fourthPredictionPercentage * 100)
        binding.scoreTextView.text = String.format("점수: %d", score)

        binding.oneMoreButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("광고를 보고 한 판 더 할 기회를 얻으세요!")
            builder.setMessage("광고를 보면 다음 정각이 되기 전가지 딱 한 판 더 할 수 있어요.\n단, 최고 점수를 선택할 수는 없어요.\n상남자 상여자는 무조건 마지막 점수로!")
            builder.setPositiveButton("오케이") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }

        // Set up button click listeners
        binding.backToMainButton.setOnClickListener {
            finish()
        }

        binding.checkRankingButton.setOnClickListener {
            intent = Intent(this, RankingActivity::class.java)
            intent.putExtra("userInfo", userInfo)
            startActivity(intent)
        }
    }
}
