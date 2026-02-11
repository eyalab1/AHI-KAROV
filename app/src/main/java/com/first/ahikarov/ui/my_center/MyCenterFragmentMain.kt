package com.first.ahikarov.ui.my_center

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
import com.first.ahikarov.data.models.Item
import com.first.ahikarov.R
import com.first.ahikarov.databinding.MyCenterMainLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

        // אדפטר לתמונות
        val picturesAdapter = MediaAdapter(
            onItemClick = { item ->
                viewModel.setItem(item)
                findNavController().navigate(R.id.action_myCenterFragmentMain_to_detailItemFragment)
            },
            onItemLongClick = { item -> showDeleteDialog(item) }
        )
        binding.recyclerPictures.adapter = picturesAdapter

        // אדפטר לשירים
        val songsAdapter = MediaAdapter(
            onItemClick = { item ->
                viewModel.setItem(item)
                findNavController().navigate(R.id.action_myCenterFragmentMain_to_detailItemFragment)
            },
            onItemLongClick = { item -> showDeleteDialog(item) }
        )
        binding.recyclerSongs.adapter = songsAdapter

        // אדפטר לציטוטים
        val quotesAdapter = QuoteAdapter { itemToDelete ->
            showDeleteDialog(itemToDelete)
        }
        binding.recyclerQuotes.adapter = quotesAdapter


        // עדכון אדפטרים הקיימים + טיפול במצבים ריקים (Empty State)
        viewModel.itemsLiveData.observe(viewLifecycleOwner) { allItems ->

            // סינון הרשימות
            val imagesList = allItems.filter { it.type == 0 }
            val songsList = allItems.filter { it.type == 1 }
            val quotesList = allItems.filter { it.type == 2 }

            // 1. טיפול בתמונות
            picturesAdapter.updateList(imagesList)
            if (imagesList.isEmpty()) {
                binding.tvEmptyPictures.visibility = View.VISIBLE
                binding.recyclerPictures.visibility = View.GONE
            } else {
                binding.tvEmptyPictures.visibility = View.GONE
                binding.recyclerPictures.visibility = View.VISIBLE
            }

            // 2. טיפול בשירים
            songsAdapter.updateList(songsList)
            if (songsList.isEmpty()) {
                binding.tvEmptySongs.visibility = View.VISIBLE
                binding.recyclerSongs.visibility = View.GONE
            } else {
                binding.tvEmptySongs.visibility = View.GONE
                binding.recyclerSongs.visibility = View.VISIBLE
            }

            // 3. טיפול בציטוטים
            quotesAdapter.updateList(quotesList)
            if (quotesList.isEmpty()) {
                binding.tvEmptyQuotes.visibility = View.VISIBLE
                binding.recyclerQuotes.visibility = View.GONE
            } else {
                binding.tvEmptyQuotes.visibility = View.GONE
                binding.recyclerQuotes.visibility = View.VISIBLE
            }
        }

        //  כפתור ההוספה: פותח את הדיאלוג
        binding.add.setOnClickListener {
            showAddOptionsDialog()
        }

    } //  כאן נסגרת הפונקציה onViewCreated


    private fun showAddOptionsDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_options, null)

        dialog.setContentView(view)

        // כפתור 1: יצירה ידנית
        view.findViewById<View>(R.id.optionManual).setOnClickListener {
            dialog.dismiss()
            viewModel.setItem(null)
            findNavController().navigate(R.id.action_center_main_to_add)
        }

        // כפתור 2: חיפוש ברשת
        view.findViewById<View>(R.id.optionWeb).setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.action_myCenterFragmentMain_to_inspirationFragment)
        }

        dialog.show()
    }

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