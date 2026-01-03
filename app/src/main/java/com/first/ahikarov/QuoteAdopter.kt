package com.first.ahikarov

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.first.ahikarov.databinding.ItemQuoteCardBinding


class QuoteAdapter(
    private val onItemLongClick: (Item) -> Unit
) : ListAdapter<Item, QuoteAdapter.QuoteViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val binding = ItemQuoteCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        holder.bind(getItem(position), onItemLongClick)
    }

    class QuoteViewHolder(private val binding: ItemQuoteCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, onLongClick: (Item) -> Unit) {
            binding.quoteText.text = item.text

            binding.root.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id // בדיקה לפי תעודת זהות
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem // בדיקת תוכן
        }
    }

    fun updateList(newItems: List<Item>) {
        submitList(newItems)
    }
}