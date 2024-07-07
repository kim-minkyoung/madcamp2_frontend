package com.example.madcamp2_frontend.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivityMainBinding
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            val signInIntent = Intent(this, SignInActivity::class.java)
            startActivity(signInIntent)
            finish()
        } else {
            fetchUserInfoFromPreferences()
            Glide.with(this)
                .load(R.raw.drawdle)
                .into(binding.drawdleLogo)

            binding.profileButton.setOnClickListener {
                val userInfo = userViewModel.userInfo.value
                val intent = Intent(this, ProfileConfigurationActivity::class.java)
                intent.putExtra("userInfo", userInfo)
                startActivity(intent)
            }

            binding.startDrawingButton.setOnClickListener {
                startActivity(Intent(this, BeforeStartActivity::class.java))
            }

            binding.rankingButton.setOnClickListener {
                startActivity(Intent(this, RankingActivity::class.java))
            }
        }
    }

    private fun fetchUserInfoFromPreferences() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
        val email = sharedPreferences.getString("email", null)
        val nickname = sharedPreferences.getString("nickname", "No name")
        val profileImage = sharedPreferences.getString("profileImage", "")

        Log.d("ProfileConfigurationActivity", "User information fetched: $userId, $email, $nickname, $profileImage")

        if (userId != null && email != null) {
            val userInfo = UserInfo(userId, email, nickname, profileImage)
            Log.d("ProfileConfigurationActivity", "User information set: $userInfo")
            userViewModel.setUserInfo(userInfo)
            Log.d("ProfileConfigurationActivity", "User information set: ${userViewModel.userInfo.value}")
        }
    }
}
