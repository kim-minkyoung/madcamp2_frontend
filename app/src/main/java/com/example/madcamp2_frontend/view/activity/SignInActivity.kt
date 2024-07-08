// SignInActivity
package com.example.madcamp2_frontend.view.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivitySignInBinding
import com.example.madcamp2_frontend.databinding.NicknameDialogBinding
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences

    private val TAG = "SignInActivity"
    private val USER_ID_KEY = "userId"

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        Glide.with(this)
            .load(R.raw.drawdle)
            .into(binding.drawdleLogo)

        Log.d(TAG, "onCreate: Initializing Google Sign-In options")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        Log.d(TAG, "onCreate: Google Sign-In client created")

        binding.signInButton.setOnClickListener {
            Log.d(TAG, "signInButton clicked")
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        Log.d(TAG, "signIn: Launching sign-in intent")
        signInLauncher.launch(signInIntent)
    }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "signInLauncher: result OK")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        } else {
            Log.d(TAG, "signInLauncher: result NOT OK, resultCode: ${result.resultCode}")
            Toast.makeText(this, "Sign-In Failed: Result Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, "handleSignInResult: success, account: ${account?.email}")
            if (account != null) {
                userViewModel.postUserEmail(account)
                checkLoggedInUser(account)
            }
        } catch (e: ApiException) {
            Log.w(TAG, "handleSignInResult: failed code=" + e.statusCode)
            Toast.makeText(this, "Sign-In Failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserId(userId: String?) {
        if (userId != null) {
            sharedPreferences.edit().putString(USER_ID_KEY, userId).apply()
            Log.d(TAG, "Saved userId: $userId")
        }
    }

    private fun checkLoggedInUser(account: GoogleSignInAccount) {
        userViewModel.userInfo.observe(this, Observer { userInfo ->
            if (userInfo != null) {
                saveUserId(userInfo.userid)
            }
            Log.d(TAG, "User exists, updating UI")
            updateUI(account)
        })
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            Log.d(TAG, "updateUI: Sign-In successful, navigating to MainActivity")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Log.d(TAG, "updateUI: Sign-In failed, showing toast")
            Toast.makeText(this, "Sign-In Failed", Toast.LENGTH_SHORT).show()
        }
    }
}
