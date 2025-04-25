package com.one.videoeditingapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.videoeditingapp.R
import com.one.videoeditingapp.databinding.ItemCountdownBinding
import com.one.videoeditingapp.model.CountDownModel

class CountDownAdapter(
    val context: Context,
    val listCountDown: List<CountDownModel>,
    val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<CountDownAdapter.Viewholder>() {
    var selectedPosition: Int = 0

    interface OnClickListener {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountDownAdapter.Viewholder {
        val binding =
            ItemCountdownBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: CountDownAdapter.Viewholder, position: Int) {
        val countDownModel = listCountDown.get(position)
        holder.binding.tvCountDownTime.text = countDownModel.timeCountDown

        if (selectedPosition == position) {
            holder.binding.tvCountDownTime.foreground =
                context.getDrawable(R.drawable.bg_selected_coutdown)
        } else {
            holder.binding.tvCountDownTime.foreground = null
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onClickListener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return listCountDown.size
    }

    class Viewholder(val binding: ItemCountdownBinding) : RecyclerView.ViewHolder(binding.root)
}