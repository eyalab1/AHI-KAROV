package com.first.ahikarov.ui.my_center

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.first.ahikarov.R // שים לב לייבוא הזה, חשוב שזה יהיה ה-R של האפליקציה שלך
import com.first.ahikarov.data.models.Item
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

            // בדיקה: האם יש תמונה? (לא משנה אם זה שיר או תמונה רגילה)
            if (!item.photo.isNullOrEmpty()) {
                // יש תמונה (מהאינטרנט או מהגלריה) - נציג אותה
                Glide.with(binding.root.context)
                    .load(item.photo)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher) // תמונה זמנית
                    .error(R.mipmap.ic_launcher) // אם נכשל
                    .into(binding.itemImage)
            } else {
                // אין תמונה בכלל -> נציג אייקון ברירת מחדל לפי הסוג
                if (item.type == 1) {
                    // אייקון לשירים שאין להם תמונה
                    binding.itemImage.setImageResource(android.R.drawable.ic_media_play)
                } else {
                    // אייקון כללי
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
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    fun updateList(newItems: List<Item>) {
        submitList(newItems)
    }
}