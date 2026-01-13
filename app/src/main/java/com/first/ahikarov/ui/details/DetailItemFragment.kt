package com.first.ahikarov.ui.details

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.first.ahikarov.R
import com.first.ahikarov.databinding.DetailItemLayoutBinding
import com.first.ahikarov.ui.my_center.MyCenterViewModel

class DetailItemFragment : Fragment() {

    private var _binding: DetailItemLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyCenterViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DetailItemLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            if (item == null) return@observe

            binding.btnEditItem.visibility = View.VISIBLE
            binding.btnEditItem.setOnClickListener {
                findNavController().navigate(R.id.action_detailItemFragment_to_myCenterFragment)
            }

            if (item.title.isEmpty()) {
                binding.detailTitle.visibility = View.GONE
            } else {
                binding.detailTitle.text = item.title
                binding.detailTitle.visibility = View.VISIBLE
            }

            when (item.type) {
                0 -> {
                    binding.detailImage.visibility = View.VISIBLE
                    binding.btnPlayAudio.visibility = View.GONE
                    binding.detailDescription.text = item.text ?: ""

                    item.photo?.let { photoUri ->
                        try {
                            Glide.with(this)
                                .load(Uri.parse(photoUri))
                                .error(R.mipmap.ic_launcher)
                                .into(binding.detailImage)
                        } catch (_: Exception) {
                            binding.detailImage.setImageResource(R.mipmap.ic_launcher)
                        }
                    } ?: run {
                        binding.detailImage.setImageResource(R.mipmap.ic_launcher)
                    }
                }
                1 -> {
                    binding.detailImage.visibility = View.GONE
                    binding.btnPlayAudio.visibility = View.VISIBLE
                    binding.detailDescription.text = "Click Play to listen 🎵"
                    binding.btnPlayAudio.setOnClickListener { playAudio(item.text) }
                }
                2 -> {
                    binding.detailImage.visibility = View.GONE
                    binding.btnPlayAudio.visibility = View.GONE
                    binding.detailDescription.text = item.text
                }
            }
        }
    }

    private fun playAudio(audioPath: String?) {
        if (audioPath.isNullOrEmpty()) {
            Toast.makeText(context, "No audio file found", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            if (mediaPlayer != null) {
                mediaPlayer?.release()
                mediaPlayer = null
                binding.btnPlayAudio.text = "Play Music ▶️"
            } else {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(requireContext(), Uri.parse(audioPath))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                binding.btnPlayAudio.text = "Stop Music ⏹️"
                mediaPlayer?.setOnCompletionListener {
                    binding.btnPlayAudio.text = "Play Music ▶️"
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
            }
        } catch (_: Exception) {
            Toast.makeText(context, "Error playing file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        _binding = null
    }
}
