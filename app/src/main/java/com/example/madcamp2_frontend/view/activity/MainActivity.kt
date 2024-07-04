package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp2_frontend.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        if (account == null) {
//            val signInIntent = Intent(this, SignInActivity::class.java)
//            startActivity(signInIntent)
//            finish()
//        } else {
            binding.startDrawingButton.setOnClickListener {
                startActivity(Intent(this, BeforeStartActivity::class.java))
            }

            binding.rankingButton.setOnClickListener {
                startActivity(Intent(this, RankingActivity::class.java))
            }
//        }
    }
}
