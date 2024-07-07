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
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivitySignInBinding
import com.example.madcamp2_frontend.databinding.NicknameDialogBinding
import com.example.madcamp2_frontend.model.network.ApiService
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
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
    private lateinit var sharedPreferences: SharedPreferences

    private val TAG = "SignInActivity"
    private val URL = "http://143.248.226.86:3000/"

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

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
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create Retrofit service instance
        service = retrofit.create(ApiService::class.java)

        checkLoggedInUser()
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun postUserEmail(account: GoogleSignInAccount) {
        val email = account.email ?: return
        val nickname = account.displayName?.ifEmpty { "No name" } ?: "No name"
        val profileImage = account.photoUrl?.toString() ?: ""
        Log.d("postUserEmail", "Posting $email, $nickname, $profileImage")

        val userInfo = UserInfo("", email, nickname, profileImage)

        GlobalScope.launch(Dispatchers.IO) {
            service.postUserEmail(userInfo).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val userResponse = response.body()?.string()
                        Log.d("postUserEmail", "User response: ${response.body()}")
                        try {
                            val jsonResponse = JSONObject(userResponse ?: "")
                            Log.d("postUserEmail", "jsonResponse: $jsonResponse")
                            if (jsonResponse.has("userid")) {
                                val userid = jsonResponse.getString("userid")
                                val isExistingUser = jsonResponse.getBoolean("isExistingUser")
                                Log.d("postUserEmail", "User ID retrieved: $userid")
                                val updatedUserInfo = userInfo.copy(userid = userid)
                                Log.d("postUserEmail", "User info updated: $updatedUserInfo")
                                userViewModel.setUserInfo(updatedUserInfo)
                                Log.d("postUserEmail", "userViewModel set")
                                saveUserToPreferences(updatedUserInfo)
                                Log.d("postUserEmail", "User saved in SharedPreferences")

                                if (!isExistingUser) {
                                    runOnUiThread {
                                        showNicknameDialog(account, updatedUserInfo)
                                    }
                                } else {
                                    runOnUiThread {
                                        updateUI(account)
                                    }
                                }
                            } else {
                                Log.e("postUserEmail", "No userid field in JSON response")
                                runOnUiThread {
                                    Toast.makeText(this@SignInActivity, "Sign-In Failed: No userid field in response", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("postUserEmail", "Failed to parse JSON response", e)
                            runOnUiThread {
                                Toast.makeText(this@SignInActivity, "Sign-In Failed: Error parsing response", Toast.LENGTH_SHORT).show()
                            }
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

    private fun saveUserToPreferences(userInfo: UserInfo) {
        val editor = sharedPreferences.edit()
        editor.putString("userId", userInfo.userid)
        editor.putString("email", userInfo.email)
        editor.putString("nickname", userInfo.nickname)
        editor.putString("profileImage", userInfo.profileImage)
        editor.apply()
    }

    private fun checkLoggedInUser() {
        val userId = sharedPreferences.getString("userId", null)
        val email = sharedPreferences.getString("email", null)
        val nickname = sharedPreferences.getString("nickname", null)
        val profileImage = sharedPreferences.getString("profileImage", null)

        if (userId != null && email != null && nickname != null && profileImage != null) {
            val userInfo = UserInfo(userId, email, nickname, profileImage)
            userViewModel.setUserInfo(userInfo)
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                updateUI(account)
            }
        }
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

    private fun showNicknameDialog(account: GoogleSignInAccount, userInfo: UserInfo) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val bindingDialog = NicknameDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(bindingDialog.root)

        bindingDialog.submitNicknameButton.setOnClickListener {
            val newNickname = bindingDialog.nicknameEditText.text.toString().ifEmpty { "No name" }
            val updatedUserInfo = userInfo.copy(nickname = newNickname)
            userViewModel.setUserInfo(updatedUserInfo)
            saveUserToPreferences(updatedUserInfo)
            updateUserNicknameOnServer(updatedUserInfo)
            dialog.dismiss()
            updateUI(account)
        }

        dialog.show()
    }

    private fun updateUserNicknameOnServer(userInfo: UserInfo) {
        GlobalScope.launch(Dispatchers.IO) {
            service.updateUserInfo(userInfo.userid, userInfo).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "updateUserInfoOnServer: User info updated successfully")
                    } else {
                        Log.e(TAG, "updateUserInfoOnServer: Failed to update User info, response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "updateUserNicknameOnServer: Failed to update nickname, error: ${t.message}")
                }
            })
        }
    }
}
