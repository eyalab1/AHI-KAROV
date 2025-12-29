package com.first.ahikarov

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.ahikarov.databinding.MyCenterLayoutBinding
import androidx.fragment.app.activityViewModels

class MyCenterFragment : Fragment() {

    private var _binding: MyCenterLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyCenterViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null
    private var selectedAudioUri: Uri? = null

    // --- שינוי: בוחר תמונות עם הרשאה קבועה (OpenDocument במקום GetContent) ---
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                // המפתח הקבוע שהמרצה דיבר עליו 🔑
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            selectedImageUri = uri
            binding.resultImage.setImageURI(null)
            binding.resultImage.setImageURI(uri)
            Toast.makeText(requireContext(), "Image Selected!", Toast.LENGTH_SHORT).show()
        }
    }

    // בוחר שירים (כבר היה תקין עם OpenDocument)
    private val pickAudioLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            selectedAudioUri = uri
            binding.tvSelectedAudioName.text = "Audio file selected! ✅"
            Toast.makeText(requireContext(), "Song loaded successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_SONG = 1
        private const val TYPE_QUOTE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyCenterLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateButtonState()

        binding.etItemTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { validateButtonState() }
        })

        binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateViewBasedOnSelection(position)
                validateButtonState()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.finishBtn.setOnClickListener {
            saveNewItem()
        }

        // --- שינוי: בגלל שעברנו ל-OpenDocument, צריך להעביר מערך של Mime Types ---
        binding.imageBtn.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        binding.btnPickAudio.setOnClickListener {
            pickAudioLauncher.launch(arrayOf("audio/*"))
        }
    }

    private fun validateButtonState() {
        val selectedType = binding.typeSpinner.selectedItemPosition
        val titleText = binding.etItemTitle.text.toString().trim()

        val enableButton = if (selectedType == TYPE_QUOTE) {
            binding.etQuote.text.toString().trim().isNotEmpty() // בציטוט נבדוק שהציטוט לא ריק
        } else {
            titleText.isNotEmpty()
        }

        binding.finishBtn.isEnabled = enableButton
        binding.finishBtn.alpha = if (enableButton) 1.0f else 0.5f
    }

    private fun updateViewBasedOnSelection(type: Int) {
        binding.containerImageInput.visibility = View.GONE
        binding.containerSongInput.visibility = View.GONE
        binding.containerQuoteInput.visibility = View.GONE

        if (type == TYPE_QUOTE) {
            binding.containerTitleInput.visibility = View.GONE
        } else {
            binding.containerTitleInput.visibility = View.VISIBLE
        }

        when (type) {
            TYPE_IMAGE -> binding.containerImageInput.visibility = View.VISIBLE
            TYPE_SONG -> binding.containerSongInput.visibility = View.VISIBLE
            TYPE_QUOTE -> binding.containerQuoteInput.visibility = View.VISIBLE
        }
    }

    private fun saveNewItem() {
        val selectedType = binding.typeSpinner.selectedItemPosition
        var titleText = binding.etItemTitle.text.toString()

        if (selectedType == TYPE_QUOTE) {
            titleText = ""
        }

        var newItem: Item? = null

        when (selectedType) {
            TYPE_IMAGE -> {
                if (selectedImageUri != null) {
                    val description = binding.etItemDescription.text.toString()
                    newItem = Item(
                        id = 0, // או המזהה הנכון אם זו עריכה
                        title = titleText,
                        text = description,
                        photo = selectedImageUri.toString(),
                        type = selectedType
                    )
                } else {
                    Toast.makeText(context, "Please select an image first", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            TYPE_SONG -> {
                if (selectedAudioUri != null) {
                    newItem = Item(
                        id = 0,
                        title = titleText,
                        text = selectedAudioUri.toString(),
                        photo = null,
                        type = selectedType
                    )
                } else {
                    Toast.makeText(context, "Please pick an audio file", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            TYPE_QUOTE -> {
                val quote = binding.etQuote.text.toString()
                if (quote.isNotEmpty()) {
                    newItem = Item(
                        id = 0,
                        title = titleText,
                        text = quote,
                        photo = null,
                        type = selectedType
                    )
                } else {
                    Toast.makeText(context, "Please write a quote", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

        if (newItem != null) {
            viewModel.addItem(newItem)
            Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}