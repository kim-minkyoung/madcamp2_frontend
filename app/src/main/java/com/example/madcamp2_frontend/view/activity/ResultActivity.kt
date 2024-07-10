package com.example.madcamp2_frontend.view.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp2_frontend.databinding.ActivityResultBinding
import com.example.madcamp2_frontend.databinding.OnemoreDialogBinding
import com.example.madcamp2_frontend.databinding.OnemoreProhibitedDialogBinding
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.view.utils.AdHelper
import com.example.madcamp2_frontend.view.utils.Constants
import com.example.madcamp2_frontend.view.utils.SharedPreferencesHelper
import com.example.madcamp2_frontend.viewmodel.UserViewModel

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var adHelper: AdHelper
    private val TAG: String = "ResultActivity"

    private var userInfo: UserInfo? = null

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)
        adHelper = AdHelper(this)
        adHelper.loadRewardedAd(Constants.AD_UNIT_ID)

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
        val currentWord = intent.getStringExtra("currentWord")

        if (currentWord != null) {
            sharedPreferencesHelper.saveCurrentWord(currentWord)
        }

        // Bind predictions to text views
        binding.bestPredictionTextView.text = "이 그림은 아마도 $bestPrediction"
        binding.bestPredictionPercentageTextView.text = String.format("%.1f%%만큼 일치해요!", bestPredictionPercentage * 100)
        binding.secondPredictionTextView.text = String.format("2등은 %s (%.1f%%)", secondPrediction, secondPredictionPercentage * 100)
        binding.thirdPredictionTextView.text = String.format("3등은 %s (%.1f%%)", thirdPrediction, thirdPredictionPercentage * 100)
        binding.fourthPredictionTextView.text = String.format("4등은 %s (%.1f%%)", fourthPrediction, fourthPredictionPercentage * 100)
        binding.scoreTextView.text = String.format("점수: %d", score)

        val userid = intent.getStringExtra("userid")
        if (userid != null) {
            userViewModel.getUserInfo(userid)
            userViewModel.userInfo.observe(this) { fetchedUserInfo ->
                if (fetchedUserInfo != null && fetchedUserInfo.userid != null) {
                    userInfo = fetchedUserInfo
                    setupClickListeners(score)
                } else {
                    Log.d(TAG, "User info is null")
                }
            }
        }
    }

    private fun setupClickListeners(score: Int) {
        Log.d("ResultActivity", "Set up click listeners for buttons with score $score")
        binding.scoreHelpButton.setOnClickListener {
            val view = binding.scoreExplanationTextView
            view.visibility = if (view.visibility == View.VISIBLE) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
        }

        binding.oneMoreButton.setOnClickListener {
            userInfo?.let {
                Log.d("ResultActivity", userInfo.toString())
                if (it.playCount!! < 1) {
                    showOnemoreDialog(score)
                } else {
                    showOnemoreProhibitedDialog()
                }
            }
        }

        binding.backToMainButton.setOnClickListener {
            userInfo?.let {
                it.playCount?.let { playCount ->
                    userViewModel.updateUserScore(it.userid, score, playCount + 1)
                }
            }
            finish()
        }

        binding.checkRankingButton.setOnClickListener {
            userInfo?.let {
                it.playCount?.let { playCount ->
                    userViewModel.updateUserScore(it.userid, score, playCount + 1)
                }
                val intent = Intent(this, RankingActivity::class.java)
                intent.putExtra("userid", it.userid)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showOnemoreDialog(score: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = OnemoreDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)

        dialogBinding.PositiveButton.setOnClickListener {
            dialog.dismiss()
            userInfo?.let {
                it.playCount?.let { playCount ->
                    userViewModel.updateUserScore(it.userid, score, playCount + 1)
                }
            }
            adHelper.showRewardedAd(onAdReward = {
                Log.d(TAG, "User earned the reward.")
            }, onAdClosed = {
                val intent = Intent(this, BeforeStartActivity::class.java)
                intent.putExtra("userid", userInfo?.userid)
                startActivity(intent)
                finish()
            })
        }
        dialogBinding.NegativeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showOnemoreProhibitedDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = OnemoreProhibitedDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setLayout(350.dpToPx(this), LinearLayout.LayoutParams.WRAP_CONTENT)

        dialogBinding.PositiveButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}
