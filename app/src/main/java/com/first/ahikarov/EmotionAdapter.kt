package com.first.ahikarov

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import com.first.ahikarov.databinding.ItemEmotionJournalBinding

class EmotionAdapter(
    private val onDelete: (EmotionEntry) -> Unit
) : ListAdapter<EmotionEntry, EmotionAdapter.EmotionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionViewHolder {
        val binding = ItemEmotionJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmotionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmotionViewHolder, position: Int) {
        val entry = getItem(position)
        holder.bind(entry)
    }

    class EmotionViewHolder(private val binding: ItemEmotionJournalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: EmotionEntry) {
            binding.tvDate.text = entry.date
            binding.tvContent.text = entry.content
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EmotionEntry>() {
        override fun areItemsTheSame(oldItem: EmotionEntry, newItem: EmotionEntry) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: EmotionEntry, newItem: EmotionEntry) = oldItem == newItem
    }

    fun attachSwipeToRecyclerView(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val entry = getItem(position)
                Toast.makeText(recyclerView.context, recyclerView.context.getString(R.string.toast_delete), Toast.LENGTH_SHORT).show()
                onDelete(entry)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }
}
