package com.one.videoeditingapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.videoeditingapp.R
import com.one.videoeditingapp.databinding.ItemMusicBinding
import com.one.videoeditingapp.model.MusicModel

class MusicAdapter(val context: Context, val musicList: ArrayList<MusicModel>) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingPosition: Int = -1
    private var isPlaying = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.ViewHolder {
        val binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MusicAdapter.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val musicModel = musicList.get(position)
        holder.binding.imgMusic.setImageResource(musicModel.musicThumbnail)
        holder.binding.tvSongName.text = musicModel.songName
        holder.binding.tvSongArtist.text = musicModel.artistName

        holder.binding.imgPlay.setImageResource(
            if (position == currentlyPlayingPosition && isPlaying) R.drawable.ic_resume
            else R.drawable.ic_pause
        )

        holder.itemView.setOnClickListener {
            if (position == currentlyPlayingPosition) {
                // Toggle play/pause
                if (isPlaying) {
                    mediaPlayer?.pause()
                    isPlaying = false
                    holder.binding.imgPlay.setImageResource(R.drawable.ic_pause)
                } else {
                    mediaPlayer?.start()
                    isPlaying = true
                    holder.binding.imgPlay.setImageResource(R.drawable.ic_resume)
                }
            } else {
                // Stop previous playback
                mediaPlayer?.release()

                mediaPlayer = MediaPlayer.create(context, musicModel.songUri)
                mediaPlayer?.start()

                notifyItemChanged(currentlyPlayingPosition) // update previous item
                currentlyPlayingPosition = position
                isPlaying = true
                notifyItemChanged(position)

                val i = Intent(context, EditAudioActivity::class.java)
                i.putExtra("SongImg", musicModel.musicThumbnail)
                i.putExtra("SongName", musicModel.songName)
                i.putExtra("SongUri", musicModel.songUri.toString())
                context.startActivity(i)
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    class ViewHolder(val binding: ItemMusicBinding) : RecyclerView.ViewHolder(binding.root)

}