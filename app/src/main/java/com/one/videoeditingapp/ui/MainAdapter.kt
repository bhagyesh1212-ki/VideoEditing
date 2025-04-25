package com.one.videoeditingapp.ui

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.videoeditingapp.Constant
import com.one.videoeditingapp.databinding.ItemHeaderBinding
import com.one.videoeditingapp.model.HeaderMusicModel

class MainAdapter(val context : Context,private val headerList: ArrayList<HeaderMusicModel>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val headerModel = headerList.get(position)
        holder.binding.tvHeader.text = headerModel.musicCategory
        if(headerModel.musicCategory.equals("For you")){
            val adapter = MusicAdapter(context,Constant.category_one())
            holder.binding.rcvMusic.adapter = adapter
        }else if (headerModel.musicCategory.equals("Independence Day 2024")){
            val adapter = MusicAdapter(context,Constant.category_two())
            holder.binding.rcvMusic.adapter = adapter
        }else if (headerModel.musicCategory.equals("Dance Hits")){
            val adapter = MusicAdapter(context,Constant.category_three())
            holder.binding.rcvMusic.adapter = adapter
        }
    }

    override fun getItemCount(): Int {
        return headerList.size
    }

    class ViewHolder(val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root)
}