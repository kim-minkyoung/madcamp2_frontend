package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivitySignInBinding
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserInfo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var retrofit: Retrofit
    private lateinit var service: ApiService

    private val TAG = "SignInActivity"
    private val URL = "http://143.248.226.31:3000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Initialize Retrofit
        retrofit = Retrofit.Builder()
            .baseUrl(URL) // Server URL
            .addConverterFactory(GsonConverterFactory.create()) // Gson Converter
            .build()

        // Create Retrofit service instance
        service = retrofit.create(ApiService::class.java)
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

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        Log.d(TAG, "signIn: Launching sign-in intent")
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, "handleSignInResult: success, account: ${account?.email}")
            if (account != null) {
                postUserEmail(account)
            }
        } catch (e: ApiException) {
            Log.w(TAG, "handleSignInResult: failed code=" + e.statusCode)
            Toast.makeText(this, "Sign-In Failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun postUserEmail(account: GoogleSignInAccount) {
        val email = account.email ?: return
        val nickname = account.displayName ?: ""
        val profileImage = account.photoUrl?.toString() ?: ""

        val userInfo = UserInfo(email, nickname, profileImage)

        GlobalScope.launch(Dispatchers.IO) {
            service.postUserEmail(userInfo).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "postUserEmail: Email posted successfully")
                        val user = response.body()?.string()
                        Log.d(TAG, "User info: $user")
                        runOnUiThread {
                            updateUI(account)
                        }
                    } else {
                        Log.e(TAG, "postUserEmail: Failed to post email, response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "postUserEmail: Failed to post email, error: ${t.message}")
                }
            })
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            Log.d(TAG, "updateUI: Sign-In successful, navigating to MainActivity")
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("account", account)
            startActivity(intent)
            finish()
        } else {
            Log.d(TAG, "updateUI: Sign-In failed, showing toast")
            Toast.makeText(this, "Sign-In Failed", Toast.LENGTH_SHORT).show()
        }
    }
}
