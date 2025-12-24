package com.first.ahikarov.peaceofmind

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.first.ahikarov.R
import com.first.ahikarov.databinding.PeaceOfMindBinding
import com.first.ahikarov.peaceofmind.PeaceOfMindViewModel.BreathingState
import java.util.Locale

class PeaceOfMindFragment : Fragment() {

    // ריכוז קבועים למניעת חזרתיות ו-"Magic Numbers"
    companion object {
        private const val BREATH_DURATION = 4000L
        private const val RESET_DURATION = 500L
        private const val SCALE_ORIGINAL = 1f
        private const val SCALE_MAX = 1.5f
        private const val AURA_MAX_SCALE = 1.7f
        private const val AURA_MIN_ALPHA = 0.3f
        private const val AURA_MAX_ALPHA = 0.6f
    }

    private var _binding: PeaceOfMindBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PeaceOfMindViewModel by viewModels()
    private var animatorSet: AnimatorSet? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PeaceOfMindBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        binding.btnStart.setOnClickListener { viewModel.toggleBreathing() }
    }

    private fun setupObservers() {
        viewModel.isActive.observe(viewLifecycleOwner) { isActive ->
            if (isActive) startBreathingSequence() else stopPractice()
        }

        viewModel.currentState.observe(viewLifecycleOwner) { state ->
            updateStatusText(state)
        }

        viewModel.cycleCount.observe(viewLifecycleOwner) { count ->
            binding.tvCycles.text = "מחזור $count"
        }

        viewModel.secondsElapsed.observe(viewLifecycleOwner) { totalSeconds ->
            updateTimerDisplay(totalSeconds)
        }
    }

    private fun updateStatusText(state: BreathingState) {
        binding.tvBreathStatus.text = when (state) {
            BreathingState.INHALE -> getString(R.string.inhale)
            BreathingState.HOLD_IN -> getString(R.string.hold)
            BreathingState.EXHALE -> getString(R.string.exhale)
            BreathingState.IDLE -> "מוכן?"
        }
    }

    private fun updateTimerDisplay(totalSeconds: Int) {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        binding.tvTimer.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun startBreathingSequence() {
        binding.btnStart.text = getString(R.string.stop_practice)

        val inhale = createCycleStep(SCALE_ORIGINAL, SCALE_MAX, BreathingState.INHALE)
        val hold = createWaitStep(BreathingState.HOLD_IN)
        val exhale = createCycleStep(SCALE_MAX, SCALE_ORIGINAL, BreathingState.EXHALE)

        animatorSet = AnimatorSet().apply {
            playSequentially(inhale, hold, exhale)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (viewModel.isActive.value == true) {
                        viewModel.incrementCycle()
                        startBreathingSequence()
                    }
                }
            })
            start()
        }
    }

    private fun stopPractice() {
        animatorSet?.cancel()
        binding.btnStart.text = getString(R.string.start_practice)
        
        // Reset Views to original state
        binding.breathingBox.animate().scaleX(SCALE_ORIGINAL).scaleY(SCALE_ORIGINAL).setDuration(RESET_DURATION).start()
        binding.auraView.animate().scaleX(SCALE_ORIGINAL).scaleY(SCALE_ORIGINAL).alpha(AURA_MIN_ALPHA).setDuration(RESET_DURATION).start()
    }

    // פונקציה גנרית ליצירת שלב נשימה (מונעת חזרתיות על יצירת ObjectAnimators)
    private fun createCycleStep(from: Float, to: Float, state: BreathingState): AnimatorSet {
        val isGrowing = to > from
        val auraTargetScale = if (isGrowing) AURA_MAX_SCALE else SCALE_ORIGINAL
        val auraTargetAlpha = if (isGrowing) AURA_MAX_ALPHA else AURA_MIN_ALPHA

        val anims = listOf(
            ObjectAnimator.ofFloat(binding.breathingBox, "scaleX", from, to),
            ObjectAnimator.ofFloat(binding.breathingBox, "scaleY", from, to),
            ObjectAnimator.ofFloat(binding.auraView, "scaleX", from, auraTargetScale),
            ObjectAnimator.ofFloat(binding.auraView, "scaleY", from, auraTargetScale),
            ObjectAnimator.ofFloat(binding.auraView, "alpha", binding.auraView.alpha, auraTargetAlpha)
        )

        return AnimatorSet().apply {
            playTogether(anims)
            duration = BREATH_DURATION
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) { viewModel.updateState(state) }
            })
        }
    }

    private fun createWaitStep(state: BreathingState): ObjectAnimator {
        return ObjectAnimator.ofFloat(binding.breathingBox, "alpha", 1f, 1f).apply {
            duration = BREATH_DURATION
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) { viewModel.updateState(state) }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animatorSet?.cancel()
        _binding = null
    }
}