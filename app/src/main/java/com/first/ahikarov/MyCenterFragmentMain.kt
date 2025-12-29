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

        // הגדרת כיוון הגלילה
        binding.recyclerPictures.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerSongs.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerQuotes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        // מאפשר את האזנה לשינויים
        viewModel.itemsLiveData.observe(viewLifecycleOwner) { allItems ->

            // סינון הרשימה הגדולה ל-3 רשימות קטנות
            val imagesList = allItems.filter { it.type == 0 }
            val songsList = allItems.filter { it.type == 1 }
            val quotesList = allItems.filter { it.type == 2 }

            // 1. רשימת התמונות
            binding.recyclerPictures.adapter = MediaAdapter(
                items = imagesList,
                onItemClick = { item ->
                    // שלב א: מעדכנים את ה-ViewModel באיזה פריט נבחר
                    viewModel.setItem(item)

                    // שלב ב: מנווטים למסך הפרטים! 🚀
                    findNavController().navigate(R.id.action_myCenterFragmentMain_to_detailItemFragment)
                },
                onItemLongClick = { item ->
                    showDeleteDialog(item)
                }
            )

            // 2. רשימת השירים (עשינו שגם שירים יפתחו את מסך הפרטים)
            binding.recyclerSongs.adapter = MediaAdapter(
                items = songsList,
                onItemClick = { item ->
                    viewModel.setItem(item)
                    findNavController().navigate(R.id.action_myCenterFragmentMain_to_detailItemFragment)
                },
                onItemLongClick = { item ->
                    showDeleteDialog(item)
                }
            )

            // 3. רשימת הציטוטים (נשאר ללא שינוי כרגע)
            binding.recyclerQuotes.adapter = QuoteAdapter(quotesList) { itemToDelete ->
                showDeleteDialog(itemToDelete)
            }
        }

        // כפתור הוספה
        binding.add.setOnClickListener {
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