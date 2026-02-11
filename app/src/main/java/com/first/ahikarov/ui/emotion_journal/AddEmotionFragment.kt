package com.first.ahikarov.ui.emotion_journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // <--- שים לב: זה ה-Import הנכון
import androidx.navigation.fragment.findNavController
import com.first.ahikarov.data.models.EmotionEntry
import com.first.ahikarov.R
import com.first.ahikarov.databinding.AddJournalLayoutBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEmotionFragment : Fragment() {


    private val viewModel: EmotionJournalViewModel by viewModels()

    private var _binding: AddJournalLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddJournalLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayDate = formatter.format(Date())
        binding.tvDate.text = todayDate

        binding.btnSaveEntry.setOnClickListener {
            val content = binding.etContent.text.toString()
            if (content.isBlank()) return@setOnClickListener

            val entry = EmotionEntry(
                date = binding.tvDate.text.toString(),
                content = content
            )


            viewModel.hasEntryForDate(entry.date) { exists ->
                if (exists) {
                    Toast.makeText(requireContext(), getString(R.string.entry_exists_today), Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    viewModel.addEntry(entry)
                    binding.etContent.text?.clear()
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}