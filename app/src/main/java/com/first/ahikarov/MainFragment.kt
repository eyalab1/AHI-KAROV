package com.first.ahikarov

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.first.ahikarov.databinding.MainLayoutBinding

class MainFragment : Fragment() {

    private var _binding: MainLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. האזנה ללחיצה על כפתור My Center
        binding.btnMyCenter.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_center_main)
        }

        // 2. האזנה ללחיצה על כפתור SOS
        binding.btnSos.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_sos)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}