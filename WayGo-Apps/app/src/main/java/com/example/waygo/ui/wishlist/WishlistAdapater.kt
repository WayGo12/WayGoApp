package com.example.waygo.ui.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.waygo.data.response.VocationEntity
import com.example.waygo.databinding.VacationItemsBinding

class WishlistAdapater (private val wishlist: List<VocationEntity>) : RecyclerView.Adapter<WishlistAdapater.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: VocationEntity)
    }

    inner class ViewHolder(binding: VacationItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvVocation: TextView = binding.tvItemName
        val tvCategory: TextView = binding.tvItemCategory
        val ivPhoto: ImageView = binding.imgItemPhoto
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VacationItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = wishlist.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvVocation.text = wishlist[position].name
        holder.tvCategory.text = wishlist[position].category
        Glide.with(holder.itemView.context)
            .load(wishlist[position].imgUrl)
            .into(holder.ivPhoto)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(wishlist[holder.absoluteAdapterPosition])
        }
    }
}