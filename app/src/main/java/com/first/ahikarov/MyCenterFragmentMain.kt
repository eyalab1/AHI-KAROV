package com.first.ahikarov

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.first.ahikarov.databinding.MyCenterMainLayoutBinding

class MyCenterFragmentMain : Fragment() {

    private var _binding: MyCenterMainLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyCenterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyCenterMainLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerPictures.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerSongs.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerQuotes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        // --- תיקון 1: מחקנו את emptyList() ---
        // אדפטר לתמונות
        val picturesAdapter = MediaAdapter(
            onItemClick = { item ->
                viewModel.setItem(item)
                findNavController().navigate(R.id.action_myCenterFragmentMain_to_detailItemFragment)
            },
            onItemLongClick = { item -> showDeleteDialog(item) }
        )
        binding.recyclerPictures.adapter = picturesAdapter

        // --- תיקון 2: מחקנו את emptyList() ---
        // אדפטר לשירים
        val songsAdapter = MediaAdapter(
            onItemClick = { item ->
                viewModel.setItem(item)
                findNavController().navigate(R.id.action_myCenterFragmentMain_to_detailItemFragment)
            },
            onItemLongClick = { item -> showDeleteDialog(item) }
        )
        binding.recyclerSongs.adapter = songsAdapter

        // --- תיקון 3: מחקנו את emptyList() ---
        // אדפטר לציטוטים
        val quotesAdapter = QuoteAdapter { itemToDelete ->
            showDeleteDialog(itemToDelete)
        }
        binding.recyclerQuotes.adapter = quotesAdapter


        // עדכון אדפטרים הקיימים
        viewModel.itemsLiveData.observe(viewLifecycleOwner) { allItems ->

            // סינון הרשימות
            val imagesList = allItems.filter { it.type == 0 }
            val songsList = allItems.filter { it.type == 1 }
            val quotesList = allItems.filter { it.type == 2 }

            // עדכון הנתונים בתוך האדפטרים
            // (הפונקציה updateList קיימת כי שמרנו אותה בתוך האדפטרים לנוחות)
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
                viewModel.deleteItem(item)
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