package com.example.madcamp2_frontend.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.madcamp2_frontend.databinding.ActivityProfileConfigurationBinding
import com.example.madcamp2_frontend.databinding.ItemProfileInfoBinding

class ProfileConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileConfigurationBinding
    private val REQUEST_CODE_STORAGE_PERMISSION = 1

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

        binding.profileImageView.setOnClickListener {
            if (!allPermissionsGranted()) {
                requestPermissions()
            } else {
                pickImageFromGallery()
            }
        }

        // Dynamically add profile info items
        addProfileInfo("Email", "user@example.com")
        addProfileInfo("Score", "1200")
        addProfileInfo("Last Activity", "2022-12-01")
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

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        profileImageLauncher.launch(intent)
    }

    private val profileImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val selectedImageUri: Uri? = result.data!!.data
            if (selectedImageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                val roundedBitmap = getRoundedCroppedBitmap(bitmap)
                binding.profileImageView.setImageBitmap(roundedBitmap)
            }
        }
    }

    private fun getRoundedCroppedBitmap(bitmap: Bitmap): Bitmap {
        val widthLight = bitmap.width
        val heightLight = bitmap.height

        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paintColor = Paint()
        paintColor.isAntiAlias = true

        val rectF = RectF(Rect(0, 0, widthLight, heightLight))
        canvas.drawRoundRect(rectF, widthLight / 2f, heightLight / 2f, paintColor)

        val paintImage = Paint()
        paintImage.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(bitmap, 0f, 0f, paintImage)

        return output
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
}
