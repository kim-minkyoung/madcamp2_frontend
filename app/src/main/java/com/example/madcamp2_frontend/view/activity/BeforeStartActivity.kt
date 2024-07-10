package com.example.madcamp2_frontend.view.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp2_frontend.databinding.ActivityBeforeStartBinding
import com.example.madcamp2_frontend.databinding.OnemoreDialogBinding
import com.example.madcamp2_frontend.databinding.OnemoreProhibitedDialogBinding
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.view.utils.AdHelper
import com.example.madcamp2_frontend.view.utils.Constants
import com.example.madcamp2_frontend.view.utils.SharedPreferencesHelper
import com.example.madcamp2_frontend.viewmodel.UserViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeforeStartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeforeStartBinding
    private var randomWord: String = "Loading..."
    private var userInfo: UserInfo? = null
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var adHelper: AdHelper
    private val TAG: String = "BeforeStartActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeforeStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)
        adHelper = AdHelper(this)
        adHelper.loadRewardedAd(Constants.AD_UNIT_ID)

        binding.randomWordTextView.text = randomWord

        generateRandomWord { word ->
            val jsonObject = JSONObject(word)
            randomWord = jsonObject.optString("word", "error")
            binding.randomWordTextView.text = randomWord
        }

        val userid = intent.getStringExtra("userid")
        val adWatched = intent.getBooleanExtra("adWatched", false)
        if (userid != null) {
            userViewModel.getUserInfo(userid)
            userViewModel.userInfo.observe(this) { fetchedUserInfo ->
                if (fetchedUserInfo != null && fetchedUserInfo.userid != null) {
                    userInfo = fetchedUserInfo
                    if (userInfo!!.playCount!! >= 2) {
                        showOnemoreProhibitedDialog()
                    } else if (userInfo!!.playCount!! == 1 && !adWatched) {
                        showOnemoreDialog()
                    }
                } else {
                    Log.d("BeforeStartActivity", "User info is null")
                }
            }
        } else {
            Log.e("BeforeStartActivity", "User ID is null")
        }

        binding.startButton.setOnClickListener {
            if (randomWord == "Loading...") {
                Toast.makeText(this, "Please wait until the word is loaded.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, DrawingActivity::class.java)
            intent.putExtra("userid", userInfo?.userid)
            intent.putExtra("random_word", randomWord)
            startActivity(intent)
            finish()
        }
    }

    private fun generateRandomWord(callback: (String) -> Unit) {
        val apiService = ApiService.create()
        apiService.getGlobalWord().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val word = response.body()?.string() ?: "Error"
                    callback(word)
                } else {
                    Log.e("BeforeStartActivity", "Failed to generate random word: ${response.errorBody()?.string()}")
                    callback("Error")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("BeforeStartActivity", "Failed to generate random word: ${t.message}")
                callback("Error")
            }
        })
    }

    private fun showOnemoreDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = OnemoreDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialogBinding.PositiveButton.setOnClickListener {
            if (adHelper.isAdLoaded()) {
                dialog.dismiss()
                adHelper.showRewardedAd(onAdReward = {
                    Log.d("BeforeStartActivity", "User earned the reward.")
                })
            } else {
                Toast.makeText(this, "광고가 아직 준비되지 않았어요😢", Toast.LENGTH_SHORT).show()
            }
        }
        dialogBinding.NegativeButton.setOnClickListener {
            onBackPressed()
        }

        dialog.show()
    }

    private fun showOnemoreProhibitedDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = OnemoreProhibitedDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setLayout(350.dpToPx(this), LinearLayout.LayoutParams.WRAP_CONTENT)

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialogBinding.PositiveButton.setOnClickListener {
            onBackPressed()
        }
        dialog.show()
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}
