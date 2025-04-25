package com.one.videoeditingapp.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.one.videoeditingapp.databinding.ActivityEditAudioBinding


class EditAudioActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditAudioBinding
    private var isDraggingStart = false
    private var isDraggingEnd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val songThumbnail = intent.getIntExtra("SongImg", 0)
        val songName = intent.getStringExtra("SongName")
        val songString = intent.getStringExtra("SongUri")

        val songUri = Uri.parse(songString)

        binding.imgThumbnail.setImageResource(songThumbnail)
        binding.tvSongName.text = songName

        val sampleCount = 300  // number of bars in the waveform
        val samples = IntArray(sampleCount) { (0..255).random() } // random amplitude values
        binding.WaveformSeekBar.setSampleFrom(samples)

//      binding.WaveformSeekBar.setSampleFrom(songUri)
        setupHandles()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupHandles() {
        val handleStart = binding.handleStart
        val handleEnd = binding.handleEnd
        val playhead = binding.playhead
        val waveform = binding.WaveformSeekBar

        // Add layout listener to get width of waveform after it’s rendered
        waveform.post {
            val waveformWidth = waveform.width.toFloat()

            handleStart.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> isDraggingStart = true
                    MotionEvent.ACTION_MOVE -> {
                        if (isDraggingStart) {
                            val newX = event.rawX - view.width / 2
                            val limitedX = newX.coerceIn(0f, handleEnd.x - view.width)
                            view.x = limitedX
                        }
                    }
                    MotionEvent.ACTION_UP -> isDraggingStart = false
                }
                true
            }

            handleEnd.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> isDraggingEnd = true
                    MotionEvent.ACTION_MOVE -> {
                        if (isDraggingEnd) {
                            val newX = event.rawX - view.width / 2
                            val limitedX = newX.coerceIn(handleStart.x + handleStart.width, waveformWidth - view.width)
                            view.x = limitedX
                        }
                    }
                    MotionEvent.ACTION_UP -> isDraggingEnd = false
                }
                true
            }

            // Setup playhead animation
            val animator = ValueAnimator.ofFloat(handleStart.x, handleEnd.x)
            animator.duration = 5000L
            animator.repeatMode = ValueAnimator.RESTART
            animator.repeatCount = ValueAnimator.INFINITE
            animator.addUpdateListener {
                val value = it.animatedValue as Float
                playhead.x = value
            }

            // Sample control triggers (you can wire this to buttons if you want)
            binding.WaveformSeekBar.setOnClickListener {
                animator.start()
            }

            binding.tvInstruction.setOnClickListener {
                animator.cancel()
                playhead.x = handleStart.x // Reset to start
            }
        }
    }
}
