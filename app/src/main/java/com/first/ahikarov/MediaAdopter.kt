package com.first.ahikarov

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.first.ahikarov.databinding.ItemSquareImageBinding

class MediaAdapter(
    private val onItemClick: (Item) -> Unit,
    private val onItemLongClick: (Item) -> Unit
) : ListAdapter<Item, MediaAdapter.MediaViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemSquareImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, onItemLongClick)
    }

    class MediaViewHolder(private val binding: ItemSquareImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, onClick: (Item) -> Unit, onLongClick: (Item) -> Unit) {
            binding.itemTitle.text = item.title

            if (item.type == 1) { // 1 = Song
                binding.itemImage.setImageResource(android.R.drawable.ic_media_play)
            } else {
                if (item.photo != null) {
                    try {
                        Glide.with(binding.root.context)
                            .load(item.photo.toUri())
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher)
                            .into(binding.itemImage)
                    } catch (_: Exception) {
                        binding.itemImage.setImageResource(R.mipmap.ic_launcher)
                    }
                } else {
                    binding.itemImage.setImageResource(R.mipmap.ic_launcher)
                }
            }

            // לחיצה קצרה
            binding.root.setOnClickListener {
                onClick(item)
            }

            // לחיצה ארוכה
            binding.root.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id // בדיקה לפי מזהה ייחודי
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem // בדיקת תוכן
        }
    }

    fun updateList(newItems: List<Item>) {
        submitList(newItems)
    }
}