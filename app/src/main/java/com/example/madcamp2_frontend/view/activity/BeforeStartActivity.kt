package com.example.madcamp2_frontend.view.activity

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.madcamp2_frontend.databinding.ActivityBeforeStartBinding
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.viewmodel.UserViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.activity.viewModels
import com.example.madcamp2_frontend.databinding.OnemoreDialogBinding
import com.example.madcamp2_frontend.databinding.OnemoreProhibitedDialogBinding
import com.example.madcamp2_frontend.view.utils.Constants
import com.example.madcamp2_frontend.view.utils.SharedPreferencesHelper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class BeforeStartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeforeStartBinding
    private var randomWord: String = "Loading..."
    private var userInfo: UserInfo? = null
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private var rewardedAd: RewardedAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeforeStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)
        val currentWord = sharedPreferencesHelper.getCurrentWord()

        // Display initial value
        binding.randomWordTextView.text = randomWord

        loadRewardedAd()

        // Generate random word
        generateRandomWord { word ->
            val jsonObject = JSONObject(word)
            randomWord = jsonObject.optString("word", "error")
            // Update TextView once the random word is received
            binding.randomWordTextView.text = randomWord
        }

        val userid = intent.getStringExtra("userid")
        if (userid != null) {
            userViewModel.getUserInfo(userid)
            userViewModel.userInfo.observe(this) { fetchedUserInfo ->
                if (fetchedUserInfo != null && fetchedUserInfo.userid != null) {
                    userInfo = fetchedUserInfo
                    Log.d("BeforeStartActivity", "Fetched user info: $fetchedUserInfo")

                    if (userInfo!!.playCount!! >= 2) {
                        showOnemoreProhibitedDialog()
                    } else if (userInfo!!.playCount!! == 1) {
                        showOnemoreDialog()
                    }
                } else {
                    Log.d("BeforeStartActivity", "User info is null")
                }
            }
        } else {
            Log.e("BeforeStartActivity", "User ID is null")
        }

        // Start button click listener
        binding.startButton.setOnClickListener {
            if (randomWord == "Loading...") {
                Toast.makeText(this, "Please wait until the word is loaded.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Navigate to DrawingActivity with random word
            val intent = Intent(this, DrawingActivity::class.java)
            intent.putExtra("userid", userInfo?.userid)
            intent.putExtra("random_word", randomWord)
            startActivity(intent)
            finish()
        }
    }

    // Function to generate a random word (example)
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
            dialog.dismiss()
            showRewardedAd()
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

    private fun showRewardedAd() {
        rewardedAd?.let { ad ->
            ad.show(this) {
                Log.d(TAG, "User earned the reward.")
                val intent = Intent(this, BeforeStartActivity::class.java)
                intent.putExtra("userid", userInfo?.userid)  // Pass the user ID instead
                startActivity(intent)
                finish()
            }
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            Toast.makeText(this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show()
            loadRewardedAd() // Load the ad if not loaded
        }
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(this, Constants.AD_UNIT_ID, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "Ad wasn't loaded: ${adError.message}")
                rewardedAd = null
                loadRewardedAd()
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                this@BeforeStartActivity.rewardedAd = rewardedAd

                rewardedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                        loadRewardedAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(TAG, "Ad failed to show.")
                        loadRewardedAd()
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        this@BeforeStartActivity.rewardedAd = null
                    }
                }
            }
        })
    }
}
