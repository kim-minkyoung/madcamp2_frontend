package com.example.madcamp2_frontend.view.activity

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp2_frontend.databinding.ActivityDrawingBinding
import com.example.madcamp2_frontend.model.repository.DrawingRepository

class DrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingBinding
    private val repository = DrawingRepository

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

        // Check match percentage after drawing is done
        binding.drawingEndButton.setOnClickListener {
            checkMatchPercentage()
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
        repository.callQuickDrawAPI(currentWord, drawingBitmap ?: return) { matchPercentage ->
            runOnUiThread {
                Toast.makeText(applicationContext, "일치율: ${matchPercentage}%", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val scaleX = drawingBitmap!!.width.toFloat() / binding.drawingView.width
            val scaleY = drawingBitmap!!.height.toFloat() / binding.drawingView.height
            handleTouch(event, scaleX, scaleY)
        }
        return true
    }
}
