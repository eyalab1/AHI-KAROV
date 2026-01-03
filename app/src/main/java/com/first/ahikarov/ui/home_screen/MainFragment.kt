package com.first.ahikarov.ui.home_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.ahikarov.R
import com.first.ahikarov.databinding.MainLayoutBinding

class MainFragment : Fragment() {

    private var _binding: MainLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMyCenter.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_center_main)
        }

        binding.btnSos.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_sos)
        }

        binding.btnEmotionJournal.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_emotionJournal)
        }

        binding.btnPeaceOfMind.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_peaceOfMindFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
