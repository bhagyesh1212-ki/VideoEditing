package com.one.videoeditingapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aghajari.axvideotimelineview.AXTimelineViewListener
import com.one.videoeditingapp.R
import com.one.videoeditingapp.databinding.ActivityVideoEditingBinding


class VideoEditingActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoEditingBinding
    private var isVideoStart: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val videoPath = intent.getStringExtra("videoPath")
        binding.videoView.setVideoPath(videoPath)
//      val fileUri = Uri.parse(videoPath).path

        binding.axView.setListener(object : AXTimelineViewListener {
            override fun onLeftProgressChanged(progress: Float) {
            }

            override fun onRightProgressChanged(progress: Float) {
            }

            override fun onDurationChanged(Duration: Long) {
                val time = Duration.toString()
                binding.tvVideoTime.text = time
            }

            override fun onPlayProgressChanged(progress: Float) {
            }

            override fun onDraggingStateChanged(isDragging: Boolean) {
            }
        })

        binding.axView.setVideoPath(videoPath)

        binding.videoView.setOnCompletionListener {
            isVideoStart = false
            binding.imgStop.setImageResource(R.drawable.ic_stop)
        }

        binding.imgStop.setOnClickListener {
            isVideoStart = !isVideoStart
            if (isVideoStart) {
                binding.videoView.start()
                binding.imgStop.setImageResource(R.drawable.ic_start)
            } else {
                binding.videoView.pause()
                binding.imgStop.setImageResource(R.drawable.ic_stop)
            }
        }

        binding.imgBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}