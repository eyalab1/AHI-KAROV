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

class MyCenterFragment : Fragment() {

    private var _binding: MyCenterLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyCenterViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null
    private var selectedAudioUri: Uri? = null

    private var currentEditingId: Int = 0

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                requireContext().contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { e.printStackTrace() }

            selectedImageUri = uri
            binding.resultImage.setImageURI(null)
            binding.resultImage.setImageURI(uri)
            validateButtonState()
        }
    }

    private val pickAudioLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                requireContext().contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { e.printStackTrace() }

            selectedAudioUri = uri
            binding.tvSelectedAudioName.text = getString(R.string.msg_audio_updated)
            validateButtonState()
        }
    }

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_SONG = 1
        private const val TYPE_QUOTE = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MyCenterLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemToEdit = viewModel.selectedItem.value

        if (itemToEdit != null) {
            currentEditingId = itemToEdit.id
            binding.finishBtn.text = getString(R.string.btn_update_item)
            binding.typeSpinner.setSelection(itemToEdit.type)
            binding.etItemTitle.setText(itemToEdit.title)

            when (itemToEdit.type) {
                TYPE_IMAGE -> {
                    binding.etItemDescription.setText(itemToEdit.text)
                    if (itemToEdit.photo != null) {
                        selectedImageUri = itemToEdit.photo.toUri()
                        binding.resultImage.setImageURI(selectedImageUri)
                    }
                }
                TYPE_SONG -> {
                    if (itemToEdit.text != null) {
                        selectedAudioUri = itemToEdit.text.toUri()
                        binding.tvSelectedAudioName.text = getString(R.string.msg_audio_loaded)
                    }
                }
                TYPE_QUOTE -> {
                    binding.etQuote.setText(itemToEdit.text)
                }
            }
        } else {
            currentEditingId = 0
            binding.finishBtn.text = getString(R.string.btn_save_new_item)
        }

        val closeKeyboardListener = View.OnClickListener { hideKeyboard() }
        binding.root.setOnClickListener(closeKeyboardListener)
        binding.mainContainer.setOnClickListener(closeKeyboardListener)

        setupListeners()
        validateButtonState()
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = view?.findFocus()
        if (currentFocusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
            currentFocusedView.clearFocus()
        }
    }

    private fun setupListeners() {
        binding.finishBtn.setOnClickListener { saveOrUpdateItem() }
        binding.imageBtn.setOnClickListener { pickImageLauncher.launch(arrayOf("image/*")) }
        binding.btnPickAudio.setOnClickListener { pickAudioLauncher.launch(arrayOf("audio/*")) }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validateButtonState() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etItemTitle.addTextChangedListener(textWatcher)
        binding.etQuote.addTextChangedListener(textWatcher)

        binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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

        when (type) {
            TYPE_IMAGE -> {
                if (selectedImageUri == null && currentEditingId == 0) return
                if (selectedImageUri != null) {
                    newItem = Item(currentEditingId, title, binding.etItemDescription.text.toString(), selectedImageUri.toString(), type)
                } else if (viewModel.selectedItem.value?.photo != null) {
                    newItem = Item(currentEditingId, title, binding.etItemDescription.text.toString(), viewModel.selectedItem.value!!.photo, type)
                }
            }
            TYPE_SONG -> {
                if (selectedAudioUri == null && currentEditingId == 0) return
                if (selectedAudioUri != null) {
                    newItem = Item(currentEditingId, title, selectedAudioUri.toString(), null, type)
                } else if (viewModel.selectedItem.value?.text != null) {
                    newItem = Item(currentEditingId, title, viewModel.selectedItem.value!!.text!!, null, type)
                }
            }
            TYPE_QUOTE -> {
                val quote = binding.etQuote.text.toString()
                if (quote.isNotEmpty()) {
                    newItem = Item(currentEditingId, "", quote, null, type)
                }
            }
        }

        if (newItem != null) {
            if (currentEditingId == 0) {
                viewModel.addItem(newItem)
                Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updateItem(newItem)
                viewModel.setItem(newItem)
                Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show()
            }
            findNavController().popBackStack()
        }
    }

    private fun validateButtonState() {
        val type = binding.typeSpinner.selectedItemPosition
        val hasTitle = binding.etItemTitle.text.toString().isNotEmpty()
        val isEditMode = currentEditingId != 0
        val isValid = when(type) {
            TYPE_IMAGE -> hasTitle && (selectedImageUri != null || (isEditMode && viewModel.selectedItem.value?.photo != null))
            TYPE_SONG -> hasTitle && (selectedAudioUri != null || (isEditMode && viewModel.selectedItem.value?.text != null))
            TYPE_QUOTE -> binding.etQuote.text.toString().isNotEmpty()
            else -> false
        }
        binding.finishBtn.isEnabled = isValid
        binding.finishBtn.alpha = if (isValid) 1f else 0.5f
    }

    private fun updateViewBasedOnSelection(type: Int) {
        binding.containerImageInput.visibility = if (type == TYPE_IMAGE) View.VISIBLE else View.GONE
        binding.containerSongInput.visibility = if (type == TYPE_SONG) View.VISIBLE else View.GONE
        binding.containerQuoteInput.visibility = if (type == TYPE_QUOTE) View.VISIBLE else View.GONE
        binding.containerTitleInput.visibility = if (type == TYPE_QUOTE) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
