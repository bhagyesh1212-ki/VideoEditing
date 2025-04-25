package com.one.videoeditingapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.one.videoeditingapp.databinding.ItemFilterBinding
import com.one.videoeditingapp.model.FilterModel

class FilterAdapter(
    val context: Context,
    private val listFilter: List<FilterModel>
) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterAdapter.ViewHolder {
        val binding = ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterAdapter.ViewHolder, position: Int) {
        val filterModel = listFilter.get(position)
        holder.binding.imgFilter.setImageResource(filterModel.imgFilter)
    }

    override fun getItemCount(): Int {
        return listFilter.size
    }

    class ViewHolder(val binding: ItemFilterBinding) : RecyclerView.ViewHolder(binding.root)
}