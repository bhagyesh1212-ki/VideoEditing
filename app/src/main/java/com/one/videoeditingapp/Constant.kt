package com.one.videoeditingapp

import android.net.Uri
import com.one.videoeditingapp.model.HeaderMusicModel
import com.one.videoeditingapp.model.MusicModel

object Constant {

    fun category_one(): ArrayList<MusicModel> {
        val listOne = ArrayList<MusicModel>()

        listOne.add(
            MusicModel(
                R.drawable.image_song_one,
                "Jasleen Royal",
                "Assi Sajna",
                Uri.parse("android.resource://com.one.videoeditingapp/${R.raw.song_one}")
            )
        )

        listOne.add(
            MusicModel(
                R.drawable.image_song_two,
                "Arijit Singh & Asees Kaur",
                "Ve Maahi",
                Uri.parse("android.resource://com.one.videoeditingapp/${R.raw.song_two}")
            )
        )

        listOne.add(
            MusicModel(
                R.drawable.image_song_three,
                "Mohit Chauhan",
                "Tum Se Hi",
                Uri.parse("android.resource://com.one.videoeditingapp/${R.raw.song_three}")
            )
        )

        listOne.add(
            MusicModel(
                R.drawable.image_song_four,
                "Lata Mangeshkar",
                "Dil Deewana",
                Uri.parse("android.resource://com.one.videoeditingapp/${R.raw.song_one}")
            )
        )

        listOne.add(
            MusicModel(
                R.drawable.image_song_five,
                "Kishore Kumar",
                "Aa Chalke Tujhe - Door G...",
                Uri.parse("android.resource://com.one.videoeditingapp/${R.raw.song_two}")
            )
        )

        return listOne
    }

    fun category_two(): ArrayList<MusicModel> {
        val listOne = ArrayList<MusicModel>()



        listOne.add(
            MusicModel(
                R.drawable.image_song_six,
                "Arijit Singh, Arko",
                "Desh Mere",
                Uri.parse("android.resource://com.one.videoeditingapp/${R.raw.song_three}")
            )
        )
        listOne.add(
            MusicModel(
                R.drawable.image_song_seven,
                "Shankar Mahadevan",
                "I Love My India",
                Uri.parse("android.resource://com.one.videoeditingapp/${R.raw.song_three}")
            )
        )
        return listOne
    }

    fun category_three(): ArrayList<MusicModel> {
        val listOne = ArrayList<MusicModel>()

        listOne.add(
            MusicModel(
                R.drawable.image_song_eight,
                "Badshah, Neha k.",
                "Garmi",
                Uri.parse("android.resource://com.one.videoeditingapp/${R.raw.song_one}")
            )
        )

        return listOne
    }

    fun headerList(): ArrayList<HeaderMusicModel> {
        val headerList = ArrayList<HeaderMusicModel>()
        headerList.add(HeaderMusicModel("For you"))
        headerList.add(HeaderMusicModel("Independence Day 2024"))
        headerList.add(HeaderMusicModel("Dance Hits"))
        return headerList
    }
}