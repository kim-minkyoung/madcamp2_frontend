package com.example.madcamp2_frontend.view.activity

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivityProfileConfigurationBinding
import com.example.madcamp2_frontend.databinding.ItemProfileInfoBinding
import com.example.madcamp2_frontend.databinding.NicknameDialogBinding
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.io.ByteArrayOutputStream

class ProfileConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileConfigurationBinding
    private val REQUEST_CODE_STORAGE_PERMISSION = 1

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    private val PERMISSIONS_32 = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val PERMISSIONS_33 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
    )

    private val PERMISSIONS_34 = arrayOf(
        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    )

    private val permissionsToRequest: Array<String> by lazy {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> PERMISSIONS_34
            Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU -> PERMISSIONS_33
            else -> PERMISSIONS_32
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        Log.d("ProfileConfigurationActivity", "Page Created")

        val userInfo = intent.getParcelableExtra<UserInfo>("userInfo")
        Log.d("ProfileConfigurationActivity", "user Info is $userInfo")
        userInfo?.let {
            binding.nicknameLabel.text = it.nickname
            if (it.profileImage != "") {
                Glide.with(this).load(it.profileImage).into(binding.profileImageView)
            }
            else {
                Glide.with(this).load(R.drawable.default_profile).into(binding.profileImageView)
            }
            addProfileInfo("Email", it.email)
        }

        binding.profileImageView.setOnClickListener {
            showProfileImageOptions(it)
        }

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

    private fun allPermissionsGranted(): Boolean {
        return permissionsToRequest.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        if (permissionsToRequest.any { permission ->
                ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, REQUEST_CODE_STORAGE_PERMISSION)
        }
    }

    private fun addProfileInfo(title: String, detail: String) {
        val itemBinding = ItemProfileInfoBinding.inflate(layoutInflater)
        itemBinding.infoTextView.text = title
        itemBinding.infoDetailsTextView.text = detail
        binding.infoContainer.addView(itemBinding.root)
    }

    private fun showProfileImageOptions(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.profile_image_options_menu, popup.menu)

        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.modify_image -> {
                    if (!allPermissionsGranted()) {
                        requestPermissions()
                    } else {
                        pickImageFromGallery()
                    }
                    true
                }
                R.id.delete_image -> {
                    deleteProfileImage()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        profileImageLauncher.launch(intent)
    }

    private val profileImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data!!.data
            if (selectedImageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                binding.profileImageView.setImageBitmap(bitmap)
                uploadProfileImage(bitmap)
            }
        }
    }

    private fun uploadProfileImage(bitmap: Bitmap) {
        val userId = userViewModel.userInfo.value?.userid ?: return
        val base64Image = encodeImage(bitmap)
        val updatedUserInfo = userViewModel.userInfo.value?.copy(profileImage = base64Image) ?: return
        userViewModel.updateUserInfo(updatedUserInfo)
    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun deleteProfileImage() {
        val userId = userViewModel.userInfo.value?.userid ?: return
        val updatedUserInfo = userViewModel.userInfo.value?.copy(profileImage = null) ?: return
        userViewModel.updateUserInfo(updatedUserInfo)
    }

    private fun updateNickname(newNickname: String) {
        val userId = userViewModel.userInfo.value?.userid ?: return
        val updatedUserInfo = userViewModel.userInfo.value?.copy(nickname = newNickname) ?: return
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

    fun deleteAccount() {
        val userId = userViewModel.userInfo.value?.userid ?: return
        userViewModel.deleteUser(userId)
    }
}
