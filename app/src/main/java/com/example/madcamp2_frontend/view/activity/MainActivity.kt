package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivityMainBinding
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn

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
            userViewModel.userInfo.observe(this) { userInfo ->
                if (userInfo != null) {
                    binding.profileButton.setOnClickListener {
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

            account.id?.let { userViewModel.getUserInfo(it) }

            Glide.with(this)
                .load(R.raw.drawdle)
                .into(binding.drawdleLogo)
        }
    }
}
