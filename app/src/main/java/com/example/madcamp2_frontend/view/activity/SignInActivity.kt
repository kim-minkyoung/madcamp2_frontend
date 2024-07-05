package com.example.madcamp2_frontend.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.madcamp2_frontend.model.`interface`.ApiService  // ApiService 임포트 추가
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivitySignInBinding
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
import retrofit2.converter.gson.GsonConverterFactory  // 이 부분 추가

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var retrofit: Retrofit
    private lateinit var service: ApiService

    private val TAG = "SignInActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: Initializing Google Sign-In options")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        Log.d(TAG, "onCreate: Google Sign-In client created")

        binding.signInButton.setOnClickListener {
            Log.d(TAG, "signInButton clicked")
            signIn()
        }

        // Retrofit 초기화
        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.174:3000") // 서버 URL 설정
            .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 설정
            .build()

        // Retrofit 서비스 인스턴스 생성
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
            updateUI(account)
        } catch (e: ApiException) {
            Log.w(TAG, "handleSignInResult: failed code=" + e.statusCode)
            Toast.makeText(this, "Sign-In Failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            updateUI(null)
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

    // Retrofit을 통한 서버 통신 메서드들
    private fun getDataFromServer() {
        val call: Call<ResponseBody> = service.getFunc("data") // ApiService에서 정의한 메서드 호출
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val result = response.body()?.string()
                        Log.d(TAG, "getDataFromServer onResponse: result = $result")
                        runOnUiThread { Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show() }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.d(TAG, "getDataFromServer onResponse: error = ${response.code()}")
                    runOnUiThread { Toast.makeText(applicationContext, "Error: ${response.code()}", Toast.LENGTH_SHORT).show() }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(TAG, "getDataFromServer onFailure: error = ${t.message}")
                runOnUiThread { Toast.makeText(applicationContext, "Response Fail", Toast.LENGTH_SHORT).show() }
            }
        })
    }

    // 다른 HTTP 메서드들도 동일한 방식으로 구현 가능
    private fun postDataToServer() {
        val call: Call<ResponseBody> = service.postFunc("data")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // 처리
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 처리
            }
        })
    }

    private fun updateDataOnServer() {
        val call: Call<ResponseBody> = service.putFunc("board", "data")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // 처리
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 처리
            }
        })
    }

    private fun deleteDataOnServer() {
        val call: Call<ResponseBody> = service.deleteFunc("board")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // 처리
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 처리
            }
        })
    }
}
