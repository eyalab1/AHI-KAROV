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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.first.ahikarov.databinding.MyCenterLayoutBinding

class MyCenterFragment : Fragment() {

    private var _binding: MyCenterLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyCenterViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null
    private var selectedAudioUri: Uri? = null

    // משתנה חדש: שומר את המזהה של הפריט (אם 0 = חדש, אם מספר אחר = עריכה)
    private var currentEditingId: Int = 0

    // בחירת תמונה (עם הרשאות קבועות)
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

    // בחירת שיר
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

        // --- החלק החדש והחכם: האם הגענו לעריכה? 🧠 ---
        val itemToEdit = viewModel.chosenItem.value

        if (itemToEdit != null) {
            // כן! יש פריט לעריכה
            currentEditingId = itemToEdit.id // שומרים את ה-ID המקורי
            binding.finishBtn.text = getString(R.string.btn_update_item)

            // 1. מילוי הסוג והכותרת
            binding.typeSpinner.setSelection(itemToEdit.type)
            binding.etItemTitle.setText(itemToEdit.title)

            // 2. מילוי תוכן לפי סוג
            when (itemToEdit.type) {
                TYPE_IMAGE -> {
                    binding.etItemDescription.setText(itemToEdit.text)
                    if (itemToEdit.photo != null) {
                        selectedImageUri = Uri.parse(itemToEdit.photo)
                        binding.resultImage.setImageURI(selectedImageUri)
                    }
                }
                TYPE_SONG -> {
                    if (itemToEdit.text != null) {
                        selectedAudioUri = Uri.parse(itemToEdit.text)
                        binding.tvSelectedAudioName.text = getString(R.string.msg_audio_loaded)
                    }
                }
                TYPE_QUOTE -> {
                    binding.etQuote.setText(itemToEdit.text)
                }
            }
        } else {
            // לא, זו יצירה חדשה ➕
            currentEditingId = 0
            binding.finishBtn.text = getString(R.string.btn_save_new_item)
        }

        // --- כאן השינוי לטיפול בסגירת המקלדת ---
        val closeKeyboardListener = View.OnClickListener {
            hideKeyboard()
        }

        // מחברים את המאזין גם ל-Root (המסך הכללי) וגם ל-mainContainer (התוכן הפנימי)
        binding.root.setOnClickListener(closeKeyboardListener)
        binding.mainContainer.setOnClickListener(closeKeyboardListener)
        // ----------------------------------------

        setupListeners()
        validateButtonState()
    }

    // פונקציית עזר להסתרת המקלדת
    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        val currentFocusedView = view?.findFocus()
        if (currentFocusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
            currentFocusedView.clearFocus() // אופציונלי: מוריד את הפוקוס מהשדה
        }
    }

    private fun setupListeners() {
        binding.finishBtn.setOnClickListener { saveOrUpdateItem() }

        binding.imageBtn.setOnClickListener { pickImageLauncher.launch(arrayOf("image/*")) }
        binding.btnPickAudio.setOnClickListener { pickAudioLauncher.launch(arrayOf("audio/*")) }

        binding.etItemTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validateButtonState() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etQuote.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validateButtonState() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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

        // לוגיקת יצירת האובייקט (משתמשים ב-currentEditingId!)
        when (type) {
            TYPE_IMAGE -> {
                if (selectedImageUri == null) return
                newItem = Item(currentEditingId, title, binding.etItemDescription.text.toString(), selectedImageUri.toString(), type)
            }
            TYPE_SONG -> {
                if (selectedAudioUri == null) return
                newItem = Item(currentEditingId, title, selectedAudioUri.toString(), null, type)
            }
            TYPE_QUOTE -> {
                val quote = binding.etQuote.text.toString()
                if (quote.isEmpty()) return
                newItem = Item(currentEditingId, "", quote, null, type)
            }
        }

        if (newItem != null) {
            if (currentEditingId == 0) {
                // מזהה 0 -> יצירה חדשה
                viewModel.addItem(newItem)
                Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                // מזהה קיים -> עדכון
                viewModel.updateItem(newItem)
                viewModel.setItem(newItem) // מעדכן את ה-ViewModel כדי שמסך הפרטים יראה את השינוי
                Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show()
            }

            // הפקודה הזו מעולה, היא מחזירה אותך אחורה ל-DetailFragment
            findNavController().popBackStack()
        }
    }

    private fun validateButtonState() {
        val type = binding.typeSpinner.selectedItemPosition
        val hasTitle = binding.etItemTitle.text.toString().isNotEmpty()
        val isValid = when(type) {
            TYPE_IMAGE -> hasTitle && selectedImageUri != null
            TYPE_SONG -> hasTitle && selectedAudioUri != null
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