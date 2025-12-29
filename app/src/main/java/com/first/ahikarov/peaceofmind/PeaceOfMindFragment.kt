package com.first.ahikarov.peaceofmind

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.first.ahikarov.R
import com.first.ahikarov.databinding.PeaceOfMindBinding
import com.first.ahikarov.peaceofmind.PeaceOfMindViewModel.BreathingState

class PeaceOfMindFragment : Fragment() {

    companion object {
        private const val BREATH_DURATION = 4000L
        private const val RESET_DURATION = 500L
        private const val SCALE_ORIGINAL = 1f
        private const val SCALE_MAX = 1.4f
        private const val AURA_MAX_SCALE = 1.6f
        private const val AURA_MIN_ALPHA = 0.5f 
        private const val AURA_MAX_ALPHA = 0.8f

        private const val COLOR_IDLE = 0xCCB2DFDB.toInt()    
        private const val COLOR_INHALE = 0xCC80CBC4.toInt()  
        private const val COLOR_HOLD = 0xCCD1C4E9.toInt()    
        private const val COLOR_EXHALE = 0xCCC8E6C9.toInt()  
    }

    private var _binding: PeaceOfMindBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PeaceOfMindViewModel by viewModels()
    private var currentAnimator: Animator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PeaceOfMindBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        binding.btnStart.setOnClickListener { viewModel.toggleBreathing() }

        binding.breathingBox.backgroundTintList = ColorStateList.valueOf(COLOR_IDLE)
        binding.auraView.backgroundTintList = ColorStateList.valueOf(COLOR_IDLE)
        binding.auraView.alpha = AURA_MIN_ALPHA
    }

    private fun setupObservers() {
        viewModel.isActive.observe(viewLifecycleOwner) { isActive ->
            binding.btnStart.text = if (isActive) getString(R.string.stop_practice) else getString(R.string.start_practice)
            if (!isActive) stopPractice()
        }

        viewModel.currentState.observe(viewLifecycleOwner) { state ->
            updateStatusText(state)
            handleStateAnimation(state)
        }

        viewModel.formattedCycles.observe(viewLifecycleOwner) { binding.tvCycles.text = it }
        viewModel.formattedTime.observe(viewLifecycleOwner) { binding.tvTimer.text = it }
    }

    private fun handleStateAnimation(state: BreathingState) {
        currentAnimator?.cancel()

        val animator = when (state) {
            BreathingState.INHALE -> createCycleStep(SCALE_ORIGINAL, SCALE_MAX, COLOR_IDLE, COLOR_INHALE)
            BreathingState.HOLD_IN -> createWaitStep(SCALE_MAX, AURA_MAX_SCALE, AURA_MAX_ALPHA, COLOR_INHALE, COLOR_HOLD)
            BreathingState.EXHALE -> createCycleStep(SCALE_MAX, SCALE_ORIGINAL, COLOR_HOLD, COLOR_EXHALE)
            BreathingState.HOLD_OUT -> createWaitStep(SCALE_ORIGINAL, SCALE_ORIGINAL, AURA_MIN_ALPHA, COLOR_EXHALE, COLOR_IDLE)
            BreathingState.IDLE -> return // Exhaustive check
        }

        animator.apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    viewModel.onStepFinished()
                }
            })
            currentAnimator = this
            start()
        }
    }

    private fun updateStatusText(state: BreathingState) {
        val nextStatus = when (state) {
            BreathingState.INHALE -> getString(R.string.inhale)
            BreathingState.HOLD_IN -> getString(R.string.hold)
            BreathingState.EXHALE -> getString(R.string.exhale)
            BreathingState.HOLD_OUT -> getString(R.string.hold)
            BreathingState.IDLE -> getString(R.string.ready)
        }
        if (binding.tvBreathStatus.text == nextStatus) return

        binding.tvBreathStatus.animate().alpha(0f).setDuration(300).withEndAction {
            binding.tvBreathStatus.text = nextStatus
            binding.tvBreathStatus.animate().alpha(1f).setDuration(300).start()
        }
    }

    private fun stopPractice() {
        currentAnimator?.cancel()
        binding.breathingBox.animate().scaleX(SCALE_ORIGINAL).scaleY(SCALE_ORIGINAL).setDuration(RESET_DURATION).start()
        binding.auraView.animate().scaleX(SCALE_ORIGINAL).scaleY(SCALE_ORIGINAL).alpha(AURA_MIN_ALPHA).setDuration(RESET_DURATION).start()
        animateColor(COLOR_IDLE, RESET_DURATION)
    }

    private fun createCycleStep(from: Float, to: Float, colorFrom: Int, colorTo: Int): AnimatorSet {
        val isGrowing = to > from
        val auraFrom = if (isGrowing) SCALE_ORIGINAL else AURA_MAX_SCALE
        val auraTo = if (isGrowing) AURA_MAX_SCALE else SCALE_ORIGINAL
        val auraAlphaFrom = if (isGrowing) AURA_MIN_ALPHA else AURA_MAX_ALPHA
        val auraAlphaTo = if (isGrowing) AURA_MAX_ALPHA else AURA_MIN_ALPHA

        val anims = listOf(
            ObjectAnimator.ofFloat(binding.breathingBox, "scaleX", from, to),
            ObjectAnimator.ofFloat(binding.breathingBox, "scaleY", from, to),
            ObjectAnimator.ofFloat(binding.auraView, "scaleX", auraFrom, auraTo),
            ObjectAnimator.ofFloat(binding.auraView, "scaleY", auraFrom, auraTo),
            ObjectAnimator.ofFloat(binding.auraView, "alpha", auraAlphaFrom, auraAlphaTo),
            createColorAnimator(colorFrom, colorTo)
        )
        return AnimatorSet().apply {
            playTogether(anims)
            duration = BREATH_DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    private fun createWaitStep(scale: Float, auraScale: Float, alpha: Float, colorFrom: Int, colorTo: Int): AnimatorSet {
        val anims = listOf(
            createColorAnimator(colorFrom, colorTo),
            ObjectAnimator.ofFloat(binding.breathingBox, "scaleX", scale, scale),
            ObjectAnimator.ofFloat(binding.breathingBox, "scaleY", scale, scale),
            ObjectAnimator.ofFloat(binding.auraView, "scaleX", auraScale, auraScale),
            ObjectAnimator.ofFloat(binding.auraView, "scaleY", auraScale, auraScale),
            ObjectAnimator.ofFloat(binding.auraView, "alpha", alpha, alpha)
        )
        return AnimatorSet().apply {
            playTogether(anims)
            duration = BREATH_DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    private fun createColorAnimator(from: Int, to: Int): Animator = ValueAnimator.ofInt(from, to).apply {
        setEvaluator(ArgbEvaluator())
        addUpdateListener {
            val color = ColorStateList.valueOf(it.animatedValue as Int)
            binding.breathingBox.backgroundTintList = color
            binding.auraView.backgroundTintList = color
        }
    }

    private fun animateColor(toColor: Int, duration: Long) {
        val startColor = binding.breathingBox.backgroundTintList?.defaultColor ?: COLOR_IDLE
        createColorAnimator(startColor, toColor).setDuration(duration).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentAnimator?.cancel()
        _binding = null
    }
}
