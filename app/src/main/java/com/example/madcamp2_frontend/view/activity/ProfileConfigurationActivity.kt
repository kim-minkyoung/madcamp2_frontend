package com.example.madcamp2_frontend.view.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivityProfileConfigurationBinding
import com.example.madcamp2_frontend.databinding.ItemProfileInfoBinding
import com.example.madcamp2_frontend.databinding.NicknameDialogBinding
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.view.utils.SharedPreferencesHelper
import com.example.madcamp2_frontend.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class ProfileConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileConfigurationBinding
    private val userViewModel: UserViewModel by viewModels()
    private var userInfo: UserInfo? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var hasUpdated = false

//    private val profileImages = listOf(
//        R.drawable.bear,
//        R.drawable.cat,
//        R.drawable.cow,
//        R.drawable.dog,
//        R.drawable.fox,
//        R.drawable.frog,
//        R.drawable.hamster,
//        R.drawable.koala,
//        R.drawable.lion,
//        R.drawable.monkey,
//        R.drawable.mouse,
//        R.drawable.octopus,
//        R.drawable.panda,
//        R.drawable.pig,
//        R.drawable.polarbear,
//        R.drawable.rabbit,
//        R.drawable.tiger
//    )

    private var currentImageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        Log.d("ProfileConfigurationActivity", "Page Created")

        val userid = intent.getStringExtra("userid")
        if (userid != null) {
            userViewModel.getUserInfo(userid)
            userViewModel.userInfo.observe(this, Observer { fetchedUserInfo ->
                if (fetchedUserInfo != null) {
                    userInfo = fetchedUserInfo
                    if (!hasUpdated) {
                        updateUIWithUserInfo(fetchedUserInfo)
                        hasUpdated = true
                    }
                }
            })
        }

//        binding.profileImageView.setOnClickListener {
//            changeProfileImage()
//        }

        binding.modifyNicknameView.setOnClickListener {
            showNicknameDialog()
        }

        binding.signOutButton.setOnClickListener {
            showSignOutDialog()
        }

        binding.deleteAccountButton.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun updateUIWithUserInfo(userInfo: UserInfo?) {
        if (userInfo == null) {
            Log.e("ProfileConfigurationActivity", "User info is null")
            return
        }
        binding.nicknameLabel.text = userInfo.nickname
        if (!userInfo.profileImage.isNullOrEmpty()) {
            val resId = resources.getIdentifier(userInfo.profileImage, "drawable", packageName)
            if (resId != 0) {
                binding.profileImageView.setImageResource(resId)
            } else {
                Glide.with(this).load(R.drawable.default_profile_light).into(binding.profileImageView)
            }
        } else {
            Glide.with(this).load(R.drawable.default_profile_light).into(binding.profileImageView)
        }
        addProfileInfo("Email", userInfo.email)
        addProfileInfo("누적 점수", userInfo.totalScore.toString())
        addProfileInfo("최근 점수", userInfo.score.toString())
    }

    private fun addProfileInfo(title: String, detail: String) {
        val itemBinding = ItemProfileInfoBinding.inflate(layoutInflater)
        itemBinding.infoTextView.text = title
        itemBinding.infoDetailsTextView.text = detail
        binding.infoContainer.addView(itemBinding.root)
    }

//    private fun changeProfileImage() {
//        currentImageIndex = (currentImageIndex + 1) % profileImages.size
//        val selectedImageResId = profileImages[currentImageIndex]
//        binding.profileImageView.setImageResource(selectedImageResId)
//
//        val imageName = resources.getResourceEntryName(selectedImageResId)
//        val updatedUserInfo = userInfo?.copy(profileImage = imageName, score = 0) ?: return
//        userViewModel.updateUserInfo(updatedUserInfo)
//    }

    private fun updateNickname(newNickname: String) {
        Log.d("updateNickname", "Updating nickname to $newNickname")
        binding.nicknameLabel.text = newNickname
        val updatedUserInfo = userInfo?.copy(nickname = newNickname) ?: return
        Toast.makeText(applicationContext, "닉네임을 성공적으로 업데이트했습니다.", Toast.LENGTH_SHORT).show()
        userViewModel.updateUserInfo(updatedUserInfo)
    }

    private fun showNicknameDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = NicknameDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)

        dialogBinding.submitNicknameButton.setOnClickListener {
            val newNickname = dialogBinding.nicknameEditText.text.toString().ifEmpty { "No name" }
            updateNickname(newNickname)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showSignOutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ -> signOut() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Yes") { _, _ -> deleteAccount() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteAccount() {
        val userId = userInfo?.userid ?: return
        userViewModel.deleteUser(userId)

        sharedPreferencesHelper.clearUserId()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
