package com.one.videoeditingapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.one.videoeditingapp.Constant
import com.one.videoeditingapp.databinding.ActivityMusicBinding

class MusicActivity : AppCompatActivity() {
    lateinit var binding: ActivityMusicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MainAdapter(this, Constant.headerList())
        binding.rcvMain.adapter = adapter

    }
}