package com.first.ahikarov

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.first.ahikarov.databinding.AddJournalLayoutBinding
import java.text.SimpleDateFormat
import java.util.*

class AddEmotionFragment : Fragment() {

    private val viewModel: EmotionJournalViewModel
            by navGraphViewModels(R.id.our_nav)
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

            // בדיקה אסינכרונית אם כבר קיימת רשומה
            viewModel.hasEntryForDate(entry.date) { exists ->
                if (exists) {
                    Toast.makeText(requireContext(), "כבר קיימת רשומה להיום", Toast.LENGTH_SHORT).show()
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
