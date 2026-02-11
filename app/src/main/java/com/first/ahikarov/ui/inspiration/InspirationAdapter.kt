package com.first.ahikarov.ui.inspiration

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.first.ahikarov.R
import com.first.ahikarov.data.models.ImageItem
import com.first.ahikarov.data.models.QuoteResponse
import com.first.ahikarov.data.models.Track

class InspirationAdapter(
    private val onSaveClick: (Any) -> Unit
) : RecyclerView.Adapter<InspirationAdapter.UnifiedViewHolder>() {

    private var items: List<Any> = emptyList()

    fun updateData(newItems: List<Any>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnifiedViewHolder {
        // טוען את הקובץ item_inspiration_result.xml ששלחת
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inspiration_result, parent, false)
        return UnifiedViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnifiedViewHolder, position: Int) {
        holder.bind(items[position], onSaveClick)
    }

    override fun getItemCount(): Int = items.size

    class UnifiedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // IDs מתוך item_inspiration_result.xml
        private val ivImage: ImageView = itemView.findViewById(R.id.ivResultImage)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvResultTitle)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tvResultSubtitle)
        private val btnSave: ImageButton = itemView.findViewById(R.id.btnSaveResult)

        fun bind(item: Any, onSave: (Any) -> Unit) {
            ivImage.visibility = View.GONE
            tvTitle.visibility = View.VISIBLE
            tvSubtitle.visibility = View.VISIBLE

            when (item) {
                is QuoteResponse -> {
                    tvTitle.text = "\"${item.q}\""
                    tvSubtitle.text = "- ${item.a}"
                }
                is ImageItem -> {
                    ivImage.visibility = View.VISIBLE
                    tvTitle.visibility = View.GONE
                    tvSubtitle.visibility = View.GONE
                    Glide.with(itemView.context).load(item.imageUrl).into(ivImage)
                }
                is Track -> {
                    ivImage.visibility = View.VISIBLE
                    tvTitle.text = item.title
                    tvSubtitle.text = item.artist.name
                    Glide.with(itemView.context).load(item.album.cover_medium).into(ivImage)
                    itemView.setOnClickListener {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.preview))
                            itemView.context.startActivity(intent)
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
            }
            btnSave.setOnClickListener { onSave(item) }
        }
    }
}