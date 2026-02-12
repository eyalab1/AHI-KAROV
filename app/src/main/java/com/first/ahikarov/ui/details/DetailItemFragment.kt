package com.first.ahikarov.ui.details

import android.media.AudioAttributes
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailItemFragment : Fragment() {

    private var _binding: DetailItemLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyCenterViewModel by activityViewModels()
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

        viewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            if (item == null) return@observe

            // כפתור עריכה
            binding.btnEditItem.visibility = View.VISIBLE
            binding.btnEditItem.setOnClickListener {
                findNavController().navigate(R.id.action_detailItemFragment_to_myCenterFragment)
            }

            // כותרת
            if (item.title.isEmpty()) {
                binding.detailTitle.visibility = View.GONE
            } else {
                binding.detailTitle.text = item.title
                binding.detailTitle.visibility = View.VISIBLE
            }

            // הצגת המידע לפי סוג
            when (item.type) {
                0 -> { // תמונה
                    binding.detailImage.visibility = View.VISIBLE
                    binding.btnPlayAudio.visibility = View.GONE
                    binding.detailDescription.text = item.text ?: ""

                    // טעינת תמונה (מהאינטרנט או מקומי)
                    val imageSource = if (!item.link.isNullOrEmpty()) item.link else item.photo

                    imageSource?.let { path ->
                        try {
                            Glide.with(this)
                                .load(path) // Glide יודע לטעון גם URL וגם URI
                                .error(R.mipmap.ic_launcher)
                                .into(binding.detailImage)
                        } catch (_: Exception) {
                            binding.detailImage.setImageResource(R.mipmap.ic_launcher)
                        }
                    } ?: run {
                        binding.detailImage.setImageResource(R.mipmap.ic_launcher)
                    }
                }
                1 -> { // שיר
                    binding.detailImage.visibility = View.GONE
                    binding.btnPlayAudio.visibility = View.VISIBLE

                    val audioSource = if (!item.link.isNullOrEmpty()) item.link else item.text

                    binding.detailDescription.text = "Click Play to listen 🎵\n(Artist: ${item.text})"

                    binding.btnPlayAudio.setOnClickListener {
                        playAudio(audioSource)
                    }
                }
                2 -> { // ציטוט
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
                // מתחילים לנגן
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(requireContext(), Uri.parse(audioPath))
                    prepare() // או prepareAsync() אם זה נתקע, אבל לרוב prepare מספיק לקבצים קטנים
                    start()
                }

                binding.btnPlayAudio.text = "Stop Music ⏹️"

                // כשהשיר נגמר - מחזירים את הכפתור למצב התחלתי
                mediaPlayer?.setOnCompletionListener {
                    binding.btnPlayAudio.text = "Play Music ▶️"
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error playing file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}