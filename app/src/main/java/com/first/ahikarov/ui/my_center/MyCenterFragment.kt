package com.first.ahikarov.ui.my_center

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.first.ahikarov.data.models.Item
import com.first.ahikarov.R
import com.first.ahikarov.databinding.MyCenterLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCenterFragment : Fragment() {

    private var _binding: MyCenterLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyCenterViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null
    private var selectedAudioUri: Uri? = null

    private var currentEditingId: Int = 0 // 0 means new item

    private var originalLink: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            selectedImageUri = uri
            binding.resultImage.setImageURI(null) // Reset view
            binding.resultImage.setImageURI(uri)
            validateButtonState()
        }
    }

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
            binding.tvSelectedAudioName.text = getString(R.string.msg_audio_updated) // "Audio Selected"
            validateButtonState()
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

        val itemToEdit = viewModel.selectedItem.value

        if (itemToEdit != null && itemToEdit.id != 0) {
            //  מצב עריכה
            currentEditingId = itemToEdit.id
            originalLink = itemToEdit.link
            binding.finishBtn.text = getString(R.string.btn_update_item)

            // מגדירים את הספינר
            binding.typeSpinner.setSelection(itemToEdit.type)

            // מגדירים כותרת
            binding.etItemTitle.setText(itemToEdit.title)

            when (itemToEdit.type) {
                TYPE_IMAGE -> {
                    // טקסט תיאור
                    binding.etItemDescription.setText(itemToEdit.text)
                    // תמונה
                    if (itemToEdit.photo != null) {
                        selectedImageUri = itemToEdit.photo.toUri()
                        binding.resultImage.setImageURI(selectedImageUri)
                    }
                }
                TYPE_SONG -> {
                    if (itemToEdit.text != null) {

                        try {
                            selectedAudioUri = itemToEdit.text.toUri()
                            binding.tvSelectedAudioName.text = getString(R.string.msg_audio_loaded)
                        } catch (e: Exception) {
                            binding.tvSelectedAudioName.text = itemToEdit.text
                        }
                    }
                }
                TYPE_QUOTE -> {
                    binding.etQuote.setText(itemToEdit.text)
                }
            }
        } else {
            //  מצב יצירה חדשה
            currentEditingId = 0
            originalLink = null
            binding.finishBtn.text = getString(R.string.btn_save_new_item) // "Save Item"
        }

        // סגירת מקלדת בלחיצה בחוץ
        val closeKeyboardListener = View.OnClickListener { hideKeyboard() }
        binding.root.setOnClickListener(closeKeyboardListener)
        binding.mainContainer.setOnClickListener(closeKeyboardListener)

        setupListeners()

        // עדכון ראשוני של ה-UI לפי הספינר (ברירת מחדל 0 = תמונה)
        updateViewBasedOnSelection(binding.typeSpinner.selectedItemPosition)
        validateButtonState()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = view?.findFocus()
        if (currentFocusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
            currentFocusedView.clearFocus()
        }
    }

    private fun setupListeners() {
        binding.finishBtn.setOnClickListener {
            saveOrUpdateItem()
        }

        binding.imageBtn.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        binding.btnPickAudio.setOnClickListener {
            pickAudioLauncher.launch(arrayOf("audio/*"))
        }

        // מאזין לשינויי טקסט (כדי להפעיל/לכבות את כפתור השמירה)
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etItemTitle.addTextChangedListener(textWatcher)
        binding.etQuote.addTextChangedListener(textWatcher)

        // מאזין לספינר (מחליף בין תמונה / שיר / ציטוט)
        binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateViewBasedOnSelection(position)
                validateButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun saveOrUpdateItem() {
        val type = binding.typeSpinner.selectedItemPosition
        val title = binding.etItemTitle.text.toString()
        var newItem: Item? = null

        // בניית האובייקט לפי הסוג שנבחר
        when (type) {
            TYPE_IMAGE -> {
                val description = binding.etItemDescription.text.toString()

                // תמונה חדשה שנבחרה מהגלריה
                if (selectedImageUri != null) {
                    newItem = Item(
                        id = currentEditingId,
                        title = title,
                        text = description,
                        photo = selectedImageUri.toString(),
                        link = originalLink,
                        type = type
                    )
                }

                else if (currentEditingId != 0 && viewModel.selectedItem.value?.photo != null) {
                    newItem = Item(
                        id = currentEditingId,
                        title = title,
                        text = description,
                        photo = viewModel.selectedItem.value!!.photo,
                        link = originalLink,
                        type = type
                    )
                }
            }

            TYPE_SONG -> {
                // שיר חדש מהקבצים
                if (selectedAudioUri != null) {
                    newItem = Item(
                        id = currentEditingId,
                        title = title,
                        text = selectedAudioUri.toString(),
                        photo = null,
                        link = null,
                        type = type
                    )
                }
                // שיר קיים (עריכה)
                else if (currentEditingId != 0 && viewModel.selectedItem.value != null) {
                    val oldItem = viewModel.selectedItem.value!!
                    newItem = Item(
                        id = currentEditingId,
                        title = title,
                        text = oldItem.text,
                        photo = oldItem.photo,
                        link = oldItem.link,
                        type = type
                    )
                }
            }

            TYPE_QUOTE -> {
                val quoteContent = binding.etQuote.text.toString()
                if (quoteContent.isNotEmpty()) {
                    newItem = Item(
                        id = currentEditingId,
                        title = title.ifEmpty { "My Quote" },
                        text = quoteContent,
                        photo = null,
                        link = null,
                        type = type
                    )
                }
            }
        }

        // ביצוע השמירה בפועל
        if (newItem != null) {
            if (currentEditingId == 0) {
                // הוספה חדשה
                viewModel.addItem(newItem)
                Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                // עדכון קיים
                viewModel.updateItem(newItem)
                // מאפסים את הבחירה כדי שלא נחזור למצב עריכה בפעם הבאה
                viewModel.setItem(null)
                Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show()
            }
            // חזרה אחורה
            findNavController().popBackStack()
        } else {
            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateButtonState() {
        val type = binding.typeSpinner.selectedItemPosition
        val title = binding.etItemTitle.text.toString()
        val hasTitle = title.isNotEmpty()
        val isEditMode = currentEditingId != 0

        val isValid = when (type) {
            TYPE_IMAGE -> {
                val hasImage = selectedImageUri != null || (isEditMode && viewModel.selectedItem.value?.photo != null)
                hasTitle && hasImage
            }
            TYPE_SONG -> {
                val hasAudio = selectedAudioUri != null || (isEditMode && (viewModel.selectedItem.value?.text != null || viewModel.selectedItem.value?.link != null))
                hasTitle && hasAudio
            }
            TYPE_QUOTE -> {
                binding.etQuote.text.toString().isNotEmpty()
            }
            else -> false
        }

        binding.finishBtn.isEnabled = isValid
        binding.finishBtn.alpha = if (isValid) 1f else 0.5f
    }

    private fun updateViewBasedOnSelection(type: Int) {
        // מציג/מסתיר שדות לפי הסוג הנבחר
        binding.containerImageInput.visibility = if (type == TYPE_IMAGE) View.VISIBLE else View.GONE
        binding.containerSongInput.visibility = if (type == TYPE_SONG) View.VISIBLE else View.GONE
        binding.containerQuoteInput.visibility = if (type == TYPE_QUOTE) View.VISIBLE else View.GONE
        binding.containerTitleInput.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}