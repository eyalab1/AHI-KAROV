package com.first.ahikarov

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.first.ahikarov.databinding.ItemSquareImageBinding


class MediaAdapter(
    private var items: List<Item>,
    private val onItemClick: (Item) -> Unit,
    private val onItemLongClick: (Item) -> Unit
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding =
            ItemSquareImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(items[position], onItemClick, onItemLongClick)
    }

    override fun getItemCount(): Int = items.size

    class MediaViewHolder(private val binding: ItemSquareImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, onClick: (Item) -> Unit, onLongClick: (Item) -> Unit) {
            binding.itemTitle.text = item.title

            // טיפול בתמונה/שיר
            if (item.type == 1) {
// במקום R.drawable.ic_play
                binding.itemImage.setImageResource(android.R.drawable.ic_media_play)            } else {
                if (item.photo != null) {
                    try {
                        Glide.with(binding.root.context)
                            .load(Uri.parse(item.photo))
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

            // --- חיבור הלחיצות ---

            // לחיצה קצרה (onClick)
            binding.root.setOnClickListener {
                onClick(item)
            }

            // לחיצה ארוכה (onLongClick)
            binding.root.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }
    fun updateList(newItems: List<Item>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}