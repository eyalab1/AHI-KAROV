package com.first.ahikarov.ui.inspiration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.first.ahikarov.data.models.ImageItem
import com.first.ahikarov.data.models.Item
import com.first.ahikarov.data.models.QuoteResponse
import com.first.ahikarov.data.models.Track
import com.first.ahikarov.databinding.InspirationLayoutBinding
import com.first.ahikarov.ui.my_center.MyCenterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InspirationFragment : Fragment() {

    private var _binding: InspirationLayoutBinding? = null
    private val binding get() = _binding!!

    // ViewModel של המסך הזה (לטעינת נתונים מהאינטרנט)
    private val viewModel: InspirationViewModel by viewModels()

    // ViewModel משותף (לשמירת נתונים ב-Room דרך MyCenter)
    private val mainViewModel: MyCenterViewModel by activityViewModels()

    private lateinit var adapter: InspirationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = InspirationLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // אתחול האדפטר עם פונקציית השמירה
        adapter = InspirationAdapter { itemToSave ->
            saveItemToMyCenter(itemToSave)
        }

        binding.rvResults.layoutManager = GridLayoutManager(context, 2)
        binding.rvResults.adapter = adapter

        // --- כפתורים ראשיים ולוגיקת תצוגה ---

        // 1. ציטוטים -> הסתרת החיפוש
        binding.btnGetQuote.setOnClickListener {
            binding.searchContainer.visibility = View.GONE
            binding.tvInstruction.visibility = View.GONE // העלמת ההנחיה בלחיצה
            viewModel.fetchQuotes()
        }

        // 2. תמונות -> הסתרת החיפוש
        binding.btnGetImages.setOnClickListener {
            binding.searchContainer.visibility = View.GONE
            binding.tvInstruction.visibility = View.GONE // העלמת ההנחיה בלחיצה
            viewModel.fetchImages()
        }

        // 3. מוזיקה (להיטים) -> הצגת החיפוש
        binding.btnGetMusic.setOnClickListener {
            binding.searchContainer.visibility = View.VISIBLE
            binding.tvInstruction.visibility = View.GONE // העלמת ההנחיה בלחיצה
            binding.etSearchQuery.text.clear() // ניקוי טקסט קודם
            viewModel.fetchTopTracks()
        }

        // --- כפתור החיפוש הספציפי ---
        binding.btnSearchMusic.setOnClickListener {
            val query = binding.etSearchQuery.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.fetchMusic(query)
            } else {
                Toast.makeText(context, "Please enter a song name", Toast.LENGTH_SHORT).show()
            }
        }

        // --- תצפיתנים (Observers) ---

        viewModel.quotes.observe(viewLifecycleOwner) { quotes ->
            adapter.updateData(quotes)
            // אם הרשימה לא ריקה - וודא שההנחיה מוסתרת
            if (quotes.isNotEmpty()) binding.tvInstruction.visibility = View.GONE
        }

        viewModel.images.observe(viewLifecycleOwner) { images ->
            adapter.updateData(images)
            if (images.isNotEmpty()) binding.tvInstruction.visibility = View.GONE
        }

        viewModel.music.observe(viewLifecycleOwner) { tracks ->
            adapter.updateData(tracks)
            if (tracks.isNotEmpty()) binding.tvInstruction.visibility = View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) binding.tvInstruction.visibility = View.GONE
        }
    }

    private fun saveItemToMyCenter(data: Any) {
        val newItem: Item? = when (data) {
            is QuoteResponse -> {
                Item(
                    title = data.q,
                    text = "- ${data.a}",
                    photo = null,
                    type = 2,
                    link = null
                )
            }
            is ImageItem -> {
                Item(
                    title = "Inspiration Image",
                    text = "From Picsum",
                    photo = data.imageUrl,
                    type = 0,
                    link = data.imageUrl
                )
            }
            is Track -> {
                Item(
                    title = data.title,
                    text = data.artist.name,
                    photo = data.album.cover_medium,
                    type = 1,
                    link = data.preview
                )
            }
            else -> null
        }

        if (newItem != null) {
            mainViewModel.addItem(newItem)
            Toast.makeText(context, "Saved to My Center!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}