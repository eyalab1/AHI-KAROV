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
import com.first.ahikarov.databinding.MyCenterMainLayoutBinding

class MyCenterFragmentMain : Fragment() {

    private var _binding: MyCenterMainLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MyCenterMainLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // המסך של הmaincenter זה הדוגמאות בהתחלה
        if (ItemManager.items.isEmpty()) {
            ItemManager.add(Item("1", "Thailand", "Fun trip", "dummy_path", 0)) // תמונה
            ItemManager.add(Item("2", "Motivation", "Just do it", null, 2))     // ציטוט
        }

        refreshViews()

        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_center_main_to_add)
        }
    }
    private fun refreshViews(){
        val allItems = ItemManager.items

        val imagesList = allItems.filter { it.type == 0 }
        val songsList = allItems.filter { it.type == 1 }
        val quotesList = allItems.filter { it.type == 2 }




        binding.recyclerPictures.adapter = MediaAdapter(imagesList) { itemToDelete ->
            showDeleteDialog(itemToDelete)
        }
        binding.recyclerPictures.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 2. שירים
        binding.recyclerSongs.adapter = MediaAdapter(songsList) { itemToDelete ->
            showDeleteDialog(itemToDelete)
        }
        binding.recyclerSongs.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 3. משפטי השראה
        binding.recyclerQuotes.adapter = QuoteAdapter(quotesList) { itemToDelete ->
            showDeleteDialog(itemToDelete)
        }
        binding.recyclerQuotes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    // פונקציה שמציגה דיאלוג "האם למחוק?"
    private fun showDeleteDialog(item: Item) {
        AlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete '${item.title}'?")
            .setPositiveButton("Yes") { _, _ ->
                deleteItem(item)
            }
            .setNegativeButton("No", null)
            .show()
    }

    // הלוגיקה של המחיקה והרענון
    private fun deleteItem(item: Item) {
        ItemManager.remove(item)

        //  הודעה למשתמש
        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()

        //  רענון המסך כדי שהפריט יעלם
        refreshViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}