// SignInActivity
package com.example.madcamp2_frontend.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivitySignInBinding
import com.example.madcamp2_frontend.view.utils.SharedPreferencesHelper
import com.example.madcamp2_frontend.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private val TAG = "SignInActivity"

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        Glide.with(this)
            .load(R.raw.drawdle)
            .into(binding.drawdleLogo)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Initializing Google Sign-In options")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile() // Add if you need profile information
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        Log.d(TAG, "onResume: Google Sign-In client created")

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

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "handleSignInResult: success, account: ${account?.email}")
            userViewModel.postUserEmail(account)
            userViewModel.userInfo.observe(this, Observer { userInfo ->
                if (userInfo != null) {
                    saveUserId(userInfo.userid)
                }
                updateUI(account)
            })
        } catch (e: ApiException) {
            Log.w(TAG, "handleSignInResult: failed code=" + e.statusCode)
            when (e.statusCode) {
                CommonStatusCodes.NETWORK_ERROR -> Log.e(TAG, "Network error, please try again.")
                CommonStatusCodes.INVALID_ACCOUNT -> Log.e(TAG, "Invalid account, please check your credentials.")
                CommonStatusCodes.SIGN_IN_REQUIRED -> Log.e(TAG, "Sign-in required, please try again.")
                else -> Log.e(TAG, "Sign-In Failed: ${e.statusCode}")
            }
            Toast.makeText(this, "Sign-In Failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserId(userid: String?) {
        if (userid != null) {
            sharedPreferencesHelper.saveUserId(userid)
            Log.d(TAG, "Saved userId: $userid")
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            Log.d(TAG, "updateUI: Sign-In successful, navigating to MainActivity")
            val intent = Intent(this, MainActivity::class.java)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            Log.d(TAG, "updateUI: Sign-In failed, showing toast")
            Toast.makeText(this, "Sign-In Failed", Toast.LENGTH_SHORT).show()
        }
    }
}
