package com.example.waygo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waygo.data.response.AllTouristSpotsItem
import com.example.waygo.data.response.RundownItem
import com.example.waygo.databinding.GenerateItemsBinding
import com.example.waygo.databinding.VacationItemsBinding

class RundownAdapter : PagingDataAdapter<RundownItem, RundownAdapter.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GenerateItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null){
            holder.bind(item)
            holder.itemView.setOnClickListener{
                onItemClickCallback.onItemClicked(item)
            }
        }
    }

    class ViewHolder(val binding: GenerateItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RundownItem){
//            Glide.with(binding.root.context)
//                .load(item.imageUrl)
//                .into(binding.imgItemPhoto)
            binding.apply {
                tvGenName.text = item.namaTempat
                tvItemCategory.text = item.jamRundown
            }
        }

    }

    interface OnItemClickCallback{
        fun onItemClicked(data: RundownItem)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RundownItem>() {
            override fun areItemsTheSame(oldItem: RundownItem, newItem: RundownItem): Boolean {
                return oldItem == newItem
            }
            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: RundownItem, newItem: RundownItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}