package com.one.videoeditingapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.videoeditingapp.databinding.ItemSpeedBinding
import com.one.videoeditingapp.model.SpeedModel

class SpeedAdapter(
    val context: Context,
    val listSpeed: List<SpeedModel>,
    val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<SpeedAdapter.ViewHolder>() {
    var selectedPosition = -1

    interface OnClickListener {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeedAdapter.ViewHolder {
        val binding = ItemSpeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpeedAdapter.ViewHolder, position: Int) {
        val SpeedModel = listSpeed.get(position)
        holder.binding.tvSpeed.text = SpeedModel.speed

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onClickListener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return listSpeed.size
    }

    class ViewHolder(val binding: ItemSpeedBinding) : RecyclerView.ViewHolder(binding.root)
}