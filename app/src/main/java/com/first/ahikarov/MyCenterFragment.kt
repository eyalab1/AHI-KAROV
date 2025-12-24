package com.first.ahikarov

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.ahikarov.databinding.MyCenterLayoutBinding
import java.util.UUID

class MyCenterFragment : Fragment() {

    private var _binding: MyCenterLayoutBinding? = null
    private val binding get() = _binding!!


    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_SONG = 1
        private const val TYPE_QUOTE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MyCenterLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // הספינר שמאפשר בחירה
        binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateViewBasedOnSelection(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.finishBtn.setOnClickListener {
            saveNewItem()
        }

        // כפתור בחירת תמונה
        binding.imageBtn.setOnClickListener {
            Toast.makeText(context, "Image Selected (Simulated)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateViewBasedOnSelection(type: Int) {
        // איפוס: הסתרת הכל
        binding.containerImageInput.visibility = View.GONE
        binding.containerSongInput.visibility = View.GONE
        binding.containerQuoteInput.visibility = View.GONE

        // הצגה לפי הבחירה
        when (type) {
            TYPE_IMAGE -> binding.containerImageInput.visibility = View.VISIBLE
            TYPE_SONG -> binding.containerSongInput.visibility = View.VISIBLE
            TYPE_QUOTE -> binding.containerQuoteInput.visibility = View.VISIBLE
        }
    }

    private fun saveNewItem() {
        val selectedType = binding.typeSpinner.selectedItemPosition
        val id = UUID.randomUUID().toString()
        var newItem: Item? = null

        when (selectedType) {
            TYPE_IMAGE -> {
                newItem = Item(id, "My Image",null, "dummy_image_path", selectedType)
            }
            TYPE_SONG -> {
                val link = binding.etSongLink.text.toString()
                if (link.isNotEmpty()) {
                    newItem = Item(id, "My Song", link, "dummy_song_icon", selectedType)
                }
            }
            TYPE_QUOTE -> {
                val quote = binding.etQuote.text.toString()
                if (quote.isNotEmpty()) {
                    newItem = Item(id, "My Quote", quote, null, selectedType)
                }
            }
        }

        if (newItem != null) {
            ItemManager.add(newItem)
            Toast.makeText(context, "Item Saved!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        } else {
            Toast.makeText(context, "Please fill in the text", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}