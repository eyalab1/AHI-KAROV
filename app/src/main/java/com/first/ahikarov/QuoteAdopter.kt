package com.first.ahikarov

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.first.ahikarov.databinding.ItemQuoteCardBinding

class QuoteAdapter(private var items: List<Item>, private val onItemLongClick: (Item) -> Unit)
    : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val binding = ItemQuoteCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        holder.bind(items[position], onItemLongClick)
    }

    //return the size of the item
    override fun getItemCount(): Int = items.size

    class QuoteViewHolder(private val binding: ItemQuoteCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, onLongClick: (Item) -> Unit) {
            binding.quoteText.text = item.text

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