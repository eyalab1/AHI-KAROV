package com.first.ahikarov

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.activityViewModels
import com.first.ahikarov.databinding.MyCenterMainLayoutBinding

class MyCenterFragmentMain : Fragment() {

    private var _binding: MyCenterMainLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyCenterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MyCenterMainLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. הגדרת LayoutManager (כמו קודם)
        binding.recyclerPictures.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerSongs.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerQuotes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 2. יצירת אדפטרים ריקים וחיבור מיידי! (זה מה שפותר את האזהרה)

        // אדפטר לתמונות
        val picturesAdapter = MediaAdapter(emptyList(),
            onItemClick = { item ->
                viewModel.setItem(item)
                findNavController().navigate(R.id.action_myCenterFragmentMain_to_detailItemFragment)
            },
            onItemLongClick = { item -> showDeleteDialog(item) }
        )
        binding.recyclerPictures.adapter = picturesAdapter

        // אדפטר לשירים
        val songsAdapter = MediaAdapter(emptyList(),
            onItemClick = { item ->
                viewModel.setItem(item)
                findNavController().navigate(R.id.action_myCenterFragmentMain_to_detailItemFragment)
            },
            onItemLongClick = { item -> showDeleteDialog(item) }
        )
        binding.recyclerSongs.adapter = songsAdapter

        // אדפטר לציטוטים
        val quotesAdapter = QuoteAdapter(emptyList()) { itemToDelete ->
            showDeleteDialog(itemToDelete)
        }
        binding.recyclerQuotes.adapter = quotesAdapter


        // 3. האזנה לשינויים - עכשיו רק מעדכנים את האדפטרים הקיימים
        viewModel.itemsLiveData.observe(viewLifecycleOwner) { allItems ->

            // סינון הרשימות
            val imagesList = allItems.filter { it.type == 0 }
            val songsList = allItems.filter { it.type == 1 }
            val quotesList = allItems.filter { it.type == 2 }

            // עדכון הנתונים בתוך האדפטרים
            picturesAdapter.updateList(imagesList)
            songsAdapter.updateList(songsList)
            quotesAdapter.updateList(quotesList)
        }

        // כפתור הוספה
        binding.add.setOnClickListener {
            viewModel.setItem(null)
            findNavController().navigate(R.id.action_center_main_to_add)
        }
    }

    // הצגת הדיאלוג
    private fun showDeleteDialog(item: Item) {
        AlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete '${item.title}'?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.removeItem(item)
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}