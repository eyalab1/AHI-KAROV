package com.first.ahikarov.ui.emotion_journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.first.ahikarov.R
import com.first.ahikarov.databinding.EmotionJournalLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmotionJournalFragment : Fragment() {

    private lateinit var adapter: EmotionAdapter


    private val viewModel: EmotionJournalViewModel by viewModels()

    private var _binding: EmotionJournalLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EmotionJournalLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupAddButton()
    }

    private fun setupRecyclerView() {
        adapter = EmotionAdapter { entry ->
            viewModel.removeEntry(entry)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        adapter.attachSwipeToRecyclerView(binding.recyclerView)
    }

    private fun setupObservers() {
        viewModel.entries.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)

            if (list.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupAddButton() {
        binding.addJournal.setOnClickListener {
            findNavController().navigate(R.id.action_journal_to_add)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}