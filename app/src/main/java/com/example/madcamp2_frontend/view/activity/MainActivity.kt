package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivityMainBinding
import com.example.madcamp2_frontend.view.utils.SharedPreferencesHelper
import com.example.madcamp2_frontend.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)
    }

    override fun onResume() {
        super.onResume()
        val userid = sharedPreferencesHelper.getUserId()
        Log.d("MainActivity", "User id: $userid")
        if (userid == null) {
            val signInIntent = Intent(this, SignInActivity::class.java)
            startActivity(signInIntent)
            finish()
        } else {
            Log.d("MainActivity", "Last Signed In Account exists")
            userViewModel.getUserInfo(userid)
            userViewModel.userInfo.observe(this) { userInfo ->
                if (userInfo != null) {
                    Log.d("MainActivity", "User info exists: $userInfo")

                    binding.startDrawingButton.setOnClickListener {
                        val intent = Intent(this, BeforeStartActivity::class.java)
                        intent.putExtra("userid", userInfo.userid)
                        startActivity(intent)
                    }

                    binding.profileButton.setOnClickListener {
                        val intent = Intent(this, ProfileConfigurationActivity::class.java)
                        intent.putExtra("userid", userInfo.userid)
                        startActivity(intent)
                    }

                    binding.rankingButton.setOnClickListener {
                        val intent = Intent(this, RankingActivity::class.java)
                        intent.putExtra("userid", userInfo.userid)
                        startActivity(intent)
                    }
                }
                else {
                    Log.d("MainActivity", "User info is null")
                }
            }

            Glide.with(this)
                .load(R.raw.drawdle)
                .into(binding.drawdleLogo)
        }
    }
}
