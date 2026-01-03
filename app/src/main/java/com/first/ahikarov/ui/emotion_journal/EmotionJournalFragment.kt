package com.first.ahikarov.ui.emotion_journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.first.ahikarov.databinding.EmotionJournalLayoutBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.first.ahikarov.ui.emotion_journal.EmotionAdapter
import com.first.ahikarov.R


class EmotionJournalFragment: Fragment() {
    private lateinit var adapter: EmotionAdapter

    private val viewModel: EmotionJournalViewModel
            by navGraphViewModels(R.id.our_nav)
    private var _binding : EmotionJournalLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EmotionJournalLayoutBinding.inflate(inflater,container,false)
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
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerView.adapter = adapter
        adapter.attachSwipeToRecyclerView(binding.recyclerView)
    }

    private fun setupObservers() {
        viewModel.entries.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.toList())
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