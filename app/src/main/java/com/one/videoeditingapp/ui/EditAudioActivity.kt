package com.one.videoeditingapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.one.videoeditingapp.databinding.ActivityEditAudioBinding


class EditAudioActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditAudioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val songThumbnail = intent.getIntExtra("SongImg", 0)
        val songName = intent.getStringExtra("SongName")
        val songString = intent.getStringExtra("SongUri")
        val songUri = songString?.toUri()

        binding.imgThumbnail.setImageResource(songThumbnail)
        binding.tvSongName.text = songName

        val sampleCount = 200
        val samples = IntArray(sampleCount) { (0..255).random() } // random amplitude values
        binding.WaveformSeekBar.setSampleFrom(samples)
    }
}
