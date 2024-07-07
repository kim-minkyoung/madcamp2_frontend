package com.example.madcamp2_frontend.view.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProfileConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileConfigurationBinding
    private val REQUEST_CODE_STORAGE_PERMISSION = 1
    private val URL = "http://143.248.226.86:3000/"

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

        val userInfo: UserInfo? = intent.getParcelableExtra("userInfo")
        userInfo?.let {
            userViewModel.setUserInfo(it)
        }

        userViewModel.userInfo.observe(this) { userInfo ->
            Log.d("ProfileConfigurationActivity", "User Info updated to: ${userInfo.nickname}, ${userInfo.email}")
            binding.nicknameLabel.text = userInfo.nickname
            if(userInfo.profileImage != null) {
                Glide.with(this).load(userInfo.profileImage).into(binding.profileImageView)
            }
            addProfileInfo("Email", userInfo.email)
            // Update other profile info items as needed
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
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data!!.data
            if (selectedImageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                binding.profileImageView.setImageBitmap(bitmap)
                uploadProfileImage(bitmap)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                pickImageFromGallery()
            } else {
                Toast.makeText(this, "Permission denied to read external storage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addProfileInfo(title: String, detail: String) {
        val itemBinding = ItemProfileInfoBinding.inflate(layoutInflater)
        itemBinding.infoTextView.text = title
        itemBinding.infoDetailsTextView.text = detail
        binding.infoContainer.addView(itemBinding.root)
    }

    private fun uploadProfileImage(bitmap: Bitmap) {
        val userId = userViewModel.userInfo.value?.userid ?: return
        val url = "$URL/users/$userId"
        val base64Image = encodeImage(bitmap)
        val json = """
            {
                "profileImage": "$base64Image"
            }
        """.trimIndent()

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)
        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ProfileConfigurationActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProfileConfigurationActivity, "Profile image updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfileConfigurationActivity, "Failed to update image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun deleteProfileImage() {
        val userId = userViewModel.userInfo.value?.userid ?: return
        val url = "$URL/users/$userId/deleteProfileImage"

        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ProfileConfigurationActivity, "Failed to delete image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        binding.profileImageView.setImageResource(R.drawable.default_profile)
                        Toast.makeText(this@ProfileConfigurationActivity, "Profile image deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfileConfigurationActivity, "Failed to delete image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun updateNickname(newNickname: String) {
        Log.d("ProfileConfigurationActivity", "updateNickname launched with ${userViewModel.userInfo.value}")
        val userId = userViewModel.userInfo.value!!.userid
        val updatedUserInfo = userViewModel.userInfo.value?.copy(nickname = newNickname) ?: return
        val url = "$URL/users/$userId"
        Log.d("ProfileConfigurationActivity", "updateNickname to $newNickname")

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), Gson().toJson(updatedUserInfo))
        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()


        Log.d("ProfileConfigurationActivity", "requestBody is $requestBody")

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ProfileConfigurationActivity, "Failed to update nickname", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        userViewModel.setUserInfo(updatedUserInfo)
                        Toast.makeText(this@ProfileConfigurationActivity, "Nickname updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfileConfigurationActivity, "Failed to update nickname", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun showNicknameDialog() {
        val dialogBinding = NicknameDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.submitNicknameButton.setOnClickListener {
            val newNickname = dialogBinding.nicknameEditText.text.toString().ifEmpty { "No name" }
            Log.d("ProfileConfigurationActivity", "Nickname updated to $newNickname")
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
            // Clear SharedPreferences
            val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            // Navigate to SignInActivity
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
        val userId = userViewModel.userInfo.value?.userid ?: return
        val url = "$URL/users/$userId"

        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ProfileConfigurationActivity, "Failed to delete account", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        // Clear SharedPreferences
                        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.clear()
                        editor.apply()

                        // Navigate to SignInActivity
                        val intent = Intent(this@ProfileConfigurationActivity, SignInActivity::class.java)
                        startActivity(intent)
                        finish()

                        Toast.makeText(this@ProfileConfigurationActivity, "Account deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProfileConfigurationActivity, "Failed to delete account", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
