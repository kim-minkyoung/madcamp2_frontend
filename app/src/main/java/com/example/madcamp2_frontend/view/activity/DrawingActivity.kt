package com.example.madcamp2_frontend.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.madcamp2_frontend.R
import com.example.madcamp2_frontend.databinding.ActivityDrawingBinding
import com.example.madcamp2_frontend.model.repository.DrawingRepository
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class DrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingBinding
    private val repository = DrawingRepository
    private var countdownTimer: CountDownTimer? = null

    private var currentWord: String = ""
    private var drawingBitmap: Bitmap? = null
    private var tempBitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var tempCanvas: Canvas? = null
    private val paint: Paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }
    private val path: Path = Path()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get random word from intent
        currentWord = intent.getStringExtra("random_word") ?: ""

        // Set the word to TextView
        binding.wordTextView.text = currentWord

        // Initial setup
        setupCanvas()

        // Start countdown timer
        startCountdownTimer()

        // Check match percentage after drawing is done
        binding.drawingEndButton.setOnClickListener {
            countdownTimer?.cancel()
            checkMatchPercentage()
        }

        // Clear the canvas when the re-draw button is clicked
        binding.redrawButton.setOnClickListener {
            clearCanvas()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCanvas() {
        drawingBitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
        tempBitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
        binding.drawingView.setImageBitmap(drawingBitmap)

        canvas = Canvas(drawingBitmap!!)
        tempCanvas = Canvas(tempBitmap!!)
        canvas?.drawColor(Color.WHITE)

        binding.drawingView.setOnTouchListener { v, event ->
            val scaleX = drawingBitmap!!.width.toFloat() / v.width
            val scaleY = drawingBitmap!!.height.toFloat() / v.height
            handleTouch(event, scaleX, scaleY)
            true
        }
    }

    private fun handleTouch(event: MotionEvent, scaleX: Float, scaleY: Float) {
        val x = event.x * scaleX
        val y = event.y * scaleY

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                tempCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                tempCanvas?.drawPath(path, paint)
                binding.drawingView.setImageBitmap(mergeBitmaps(drawingBitmap!!, tempBitmap!!))
            }
            MotionEvent.ACTION_UP -> {
                canvas?.drawPath(path, paint)
                path.reset()
                binding.drawingView.setImageBitmap(drawingBitmap)
            }
        }
    }

    private fun mergeBitmaps(base: Bitmap, overlay: Bitmap): Bitmap {
        val mergedBitmap = Bitmap.createBitmap(base.width, base.height, base.config)
        val canvas = Canvas(mergedBitmap)
        canvas.drawBitmap(base, 0f, 0f, null)
        canvas.drawBitmap(overlay, 0f, 0f, null)
        return mergedBitmap
    }

    private fun checkMatchPercentage() {
        repository.callQuickDrawAPI(currentWord, drawingBitmap ?: return) { predictions, score ->
            runOnUiThread {
                Log.d("DrawingActivity", "onDrawingComplete called")
                onDrawingComplete(predictions, score)
            }
        }
    }

    private fun onDrawingComplete(predictions: List<Pair<String, Float>>, score: Int) {
        Log.d("DrawingActivity", "Predictions: $predictions, Score: $score")

        val bestPrediction = predictions[0]
        val secondPrediction = predictions[1]
        val thirdPrediction = predictions[2]
        val fourthPrediction = predictions[3]

        // Save bitmap to file
        val bitmapFileUri = saveBitmapToFile(drawingBitmap)

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("bitmapFileUri", bitmapFileUri)
            putExtra("bestPrediction", bestPrediction.first)
            putExtra("bestPredictionPercentage", bestPrediction.second)
            putExtra("secondPrediction", secondPrediction.first)
            putExtra("secondPredictionPercentage", secondPrediction.second)
            putExtra("thirdPrediction", thirdPrediction.first)
            putExtra("thirdPredictionPercentage", thirdPrediction.second)
            putExtra("fourthPrediction", fourthPrediction.first)
            putExtra("fourthPredictionPercentage", fourthPrediction.second)
            putExtra("score", score)
        }
        startActivity(intent)
        finish()
    }

    private fun saveBitmapToFile(bitmap: Bitmap?): Uri {
        val file = File(cacheDir, "drawing.png")
        FileOutputStream(file).use { out ->
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(20000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt() + 1
                binding.timerTextView.text = secondsLeft.toString()
                if (secondsLeft <= 5) {
                    val fraction = (5000 - millisUntilFinished) / 5000.0f
                    binding.timerTextView.textSize = 24.0f * (1.0f + fraction)
                    val backgroundColor = interpolateColor(ContextCompat.getColor(this@DrawingActivity, R.color.veryLightGray), Color.RED, fraction)
                    binding.root.setBackgroundColor(backgroundColor)
                } else {
                    binding.root.setBackgroundColor(ContextCompat.getColor(this@DrawingActivity, R.color.veryLightGray))
                }
            }

            override fun onFinish() {
                binding.timerTextView.text = "0"
                binding.drawingView.isEnabled = false
                binding.drawingEndButton.isEnabled = false
                Toast.makeText(applicationContext, "시간이 초과되었습니다!", Toast.LENGTH_SHORT).show()
                checkMatchPercentage()
            }
        }.start()
    }

    private fun interpolateColor(colorStart: Int, colorEnd: Int, fraction: Float): Int {
        val startRed = Color.red(colorStart)
        val startGreen = Color.green(colorStart)
        val startBlue = Color.blue(colorStart)
        val startAlpha = Color.alpha(colorStart)

        val endRed = Color.red(colorEnd)
        val endGreen = Color.green(colorEnd)
        val endBlue = Color.blue(colorEnd)
        val endAlpha = Color.alpha(colorEnd)

        val red = (startRed + ((endRed - startRed) * fraction)).roundToInt()
        val green = (startGreen + ((endGreen - startGreen) * fraction)).roundToInt()
        val blue = (startBlue + ((endBlue - startBlue) * fraction)).roundToInt()
        val alpha = (startAlpha + ((endAlpha - startAlpha) * fraction)).roundToInt()

        return Color.argb(alpha, red, green, blue)
    }

    private fun clearCanvas() {
        drawingBitmap?.eraseColor(Color.WHITE)
        tempBitmap?.eraseColor(Color.TRANSPARENT)
        binding.drawingView.setImageBitmap(drawingBitmap)
        canvas = Canvas(drawingBitmap!!)
        tempCanvas = Canvas(tempBitmap!!)
        path.reset()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val scaleX = drawingBitmap!!.width.toFloat() / binding.drawingView.width
            val scaleY = drawingBitmap!!.height.toFloat() / binding.drawingView.height
            handleTouch(event, scaleX, scaleY)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel()
    }
}
