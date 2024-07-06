package com.example.madcamp2_frontend.view.utils

import android.app.Activity
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.Arrays

class DoodleClassifier(activity: Activity) {

    private val LOG_TAG = DoodleClassifier::class.java.simpleName

    private val MODEL_NAME = "model.tflite"
    private val IMG_HEIGHT = 28
    private val IMG_WIDTH = 28
    private val NUM_CHANNEL = 1
    private val NUM_CLASSES = 15

    private val options = Interpreter.Options()
    private val interpreter: Interpreter
    private val imageData: ByteBuffer
    private val imagePixels = IntArray(IMG_HEIGHT * IMG_WIDTH)
    private val result = Array(1) { FloatArray(NUM_CLASSES) }

    private var quickDrawJsonFile: JSONObject? = null

    init {
        interpreter = Interpreter(loadModelFile(activity), options)
        imageData = ByteBuffer.allocateDirect(4 * IMG_HEIGHT * IMG_WIDTH * NUM_CHANNEL)
        imageData.order(ByteOrder.nativeOrder())
        try {
            val json = loadJSONFromAsset(activity, "quickdraw_labels.json")
            quickDrawJsonFile = JSONObject(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadJSONFromAsset(activity: Activity, jsonFileName: String): String {
        var json: String? = null
        try {
            val inputStream: InputStream = activity.assets.open(jsonFileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json ?: ""
    }

    fun classify(bitmap: Bitmap): ClassifyResult {
        val bitmap_1 = Bitmap.createScaledBitmap(bitmap, 256, 256, true)
        val bitmap_2 = Bitmap.createScaledBitmap(bitmap_1, 128, 128, true)
        val bitmap_3 = Bitmap.createScaledBitmap(bitmap_2, 64, 64, true)
        val bitmap_4 = Bitmap.createScaledBitmap(bitmap_3, 28, 28, true)
        saveBitmap(bitmap_4, "bitbitbitbit.png")
        convertBitmapToByteBuffer(bitmap_4)
        val startTime = SystemClock.uptimeMillis()
        interpreter.run(imageData, result)
        val endTime = SystemClock.uptimeMillis()
        val timeCost = endTime - startTime
        Log.v(LOG_TAG, "classify(): result = ${Arrays.toString(result[0])}, timeCost = $timeCost")

        val res = ClassifyResult(result[0], timeCost)
        try {
            val top4 = "${quickDrawJsonFile?.getString(res.top4[0].toString())}, " +
                    "${quickDrawJsonFile?.getString(res.top4[1].toString())}, " +
                    "${quickDrawJsonFile?.getString(res.top4[2].toString())}, " +
                    "${quickDrawJsonFile?.getString(res.top4[3].toString())}"
            res.label = top4
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return res
    }

    private fun loadModelFile(activity: Activity): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = activity.assets.openFd(MODEL_NAME)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        imageData.rewind()
        bitmap.getPixels(imagePixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until IMG_WIDTH) {
            for (j in 0 until IMG_HEIGHT) {
                val value = imagePixels[pixel++]
                imageData.putFloat(convertPixel(value))
            }
        }
    }

    private fun convertPixel(color: Int): Float {
        return (255 - ((color shr 16 and 0xFF) * 0.299f +
                (color shr 8 and 0xFF) * 0.587f +
                (color and 0xFF) * 0.114f)) / 255.0f
    }

    private fun saveBitmap(bitmap: Bitmap, fileName: String) {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, fileName)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Log.d("DoodleClassifier", "Bitmap saved: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("DoodleClassifier", "Error saving bitmap", e)
        }
    }
}

data class ClassifyResult(val probabilities: FloatArray, val timeCost: Long) {
    val top4: List<Int> = probabilities.indices
        .sortedByDescending { probabilities[it] }
        .take(4)
    var label: String? = null
}
