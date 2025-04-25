package com.one.videoeditingapp.model

import android.net.Uri

data class MusicModel(
    val musicThumbnail : Int,
    val artistName : String,
    val songName : String,
    val songUri :Uri
)
