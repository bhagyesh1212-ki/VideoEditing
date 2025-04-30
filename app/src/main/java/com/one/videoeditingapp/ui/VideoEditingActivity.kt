package com.one.videoeditingapp.ui

import android.media.PlaybackParams
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aghajari.axvideotimelineview.AXTimelineViewListener
import com.one.videoeditingapp.R
import com.one.videoeditingapp.databinding.ActivityVideoEditingBinding

class VideoEditingActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoEditingBinding
    private var isVideoStart: Boolean = false
    var playbackSpeed = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val videoPath = intent.getStringExtra("videoPath")
        val speedPosition = intent.getIntExtra("selectedSpeed", 0)

        when (speedPosition) {
            0 -> playbackSpeed = 0.5f
            1 -> playbackSpeed = 0.4f
            2 -> playbackSpeed = 3.0f
            3 -> playbackSpeed = 2.0f
        }

        binding.videoView.setVideoPath(videoPath)
        binding.videoView.setOnPreparedListener { mediaPlayer ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val params = PlaybackParams()
                params.speed = playbackSpeed
                mediaPlayer.playbackParams = params
            }
            binding.videoView.pause()
        }

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