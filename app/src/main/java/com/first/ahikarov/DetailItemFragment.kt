package com.first.ahikarov

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.first.ahikarov.databinding.DetailItemLayoutBinding

class DetailItemFragment : Fragment() {

    private var _binding: DetailItemLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyCenterViewModel by activityViewModels()

    // משתנה לנגן המוזיקה
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailItemLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.chosenItem.observe(viewLifecycleOwner) { item ->

            // 1. כותרת
            if (item.title.isEmpty()) {
                binding.detailTitle.visibility = View.GONE
            } else {
                binding.detailTitle.text = item.title
                binding.detailTitle.visibility = View.VISIBLE
            }

            // 2. לוגיקה לפי סוג
            when (item.type) {
                0 -> { // TYPE_IMAGE (תמונה)
                    binding.detailImage.visibility = View.VISIBLE
                    binding.btnPlayAudio.visibility = View.GONE
                    binding.detailDescription.text = item.text ?: ""

                    if (item.photo != null) {
                        try {
                            Glide.with(this).load(Uri.parse(item.photo)).into(binding.detailImage)
                        } catch (_: Exception) { // תיקון אזהרה: שימוש בקו תחתון (_)
                            binding.detailImage.setImageResource(R.mipmap.ic_launcher)
                        }
                    }
                }

                1 -> { // TYPE_SONG (שיר)
                    binding.detailImage.visibility = View.GONE
                    binding.btnPlayAudio.visibility = View.VISIBLE

                    binding.detailDescription.text = "Click Play to listen 🎵"

                    binding.btnPlayAudio.setOnClickListener {
                        playAudio(item.text)
                    }
                }

                2 -> { // TYPE_QUOTE (ציטוט)
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
        } catch (e: Exception) {
            Toast.makeText(context, "Error playing file", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}