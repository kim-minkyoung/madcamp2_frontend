package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

class BeforeStartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeforeStartBinding
    private var randomWord: String = "Loading..."
    private var userInfo: UserInfo? = null
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeforeStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userid = intent.getStringExtra("userid")
        if (userid != null) {
            userViewModel.getUserInfo(userid)
            userViewModel.userInfo.observe(this, Observer { fetchedUserInfo ->
                if (fetchedUserInfo != null) {
                    userInfo = fetchedUserInfo
                }
            })
        }

        /* TODO: SharedPreferences에서 current word 받아서 비교
        *   playcount가 2고 단어가 같으면 block */

        // Display initial value
        binding.randomWordTextView.text = randomWord

        // Generate random word
        generateRandomWord { word ->
            val jsonObject = JSONObject(word)
            randomWord = jsonObject.optString("word", "error")
            // Update TextView once the random word is received
            binding.randomWordTextView.text = randomWord
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
}
